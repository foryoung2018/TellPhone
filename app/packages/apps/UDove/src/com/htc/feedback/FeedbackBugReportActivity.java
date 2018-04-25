package com.htc.feedback;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.ApplicationErrorReport;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.htc.feedback.reportagent.Common;

public class FeedbackBugReportActivity extends Activity {
    private static final String TAG = "BugreportCollectDialog";
    private final static boolean DEBUG = Common.DEBUG;
    private static final String DEFAULT_DUMP_FOLDER="/data/htclog/";
    private static final String DUMP_STATE_FILE_EXTENSION=".txt";
    private String mDumpStateFile = null;
    private ProgressDialog mProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApplicationErrorReport report = (ApplicationErrorReport) getIntent()
                .getParcelableExtra(Intent.EXTRA_BUG_REPORT);

        if (report == null) {
            Intent intent = new Intent(this, FeedbackService.class);
            intent.putExtras(getIntent());
            startService(intent);
        } else {
            mDumpStateFile = getCacheDir().getPath() + "/bugreport_anr_" + report.time + ".txt.gz";
            ThreadUtil.getInstance().post(new DumpStateRunnable());
        }
    }

    private final class DumpStateRunnable implements Runnable {
        public void run() {

            // 20151124, sy_wang, We don't assign the path to htcservice so the default dump folder is /data/htclog/. But we can't delete file in it so we should check is data partition storage low before dump.
            // Our low storage threshold (20% or 1 GB) should be stricter than Android default threshold from StorageManager (10% or 500 MB)
            File dataFile = Environment.getDataDirectory(); // User data directory /data/
            long totalSpace = dataFile.getTotalSpace();
            long usableSpace = dataFile.getUsableSpace();
            final long lowStorageThreshold = Math.min(totalSpace * 20 / 100, 1024 * 1024 * 1024); // min of 20% or 1 GB
            boolean isLowStorage = false;
            if (usableSpace < lowStorageThreshold) {
                Log.d(TAG, "Usable storage is low, we don't dump bugreport. (usableSpace="+usableSpace+" byte, lowStorageThreshold="+lowStorageThreshold+" byte)");
                isLowStorage = true;
            }

            if (!isLowStorage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog = ProgressDialog.show(FeedbackBugReportActivity.this, null, getText(R.string.msg_wait_log_complete), true, false, null);
                    }
                });

                getDumpstateBySSDTestTool();
            }

            // Although ProgressDialog.dismiss() is thread safe, we still have to make sure dismiss run after show.
            // Since we use runOnUiThread() to show dialog, we use runOnUiThread() to dismiss dialog as well.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                }
            });

            Intent intent = new Intent(FeedbackBugReportActivity.this, FeedbackService.class);
            intent.putExtras(getIntent());
            if (mDumpStateFile != null)
                intent.putExtra("dumpstate", mDumpStateFile);
            startService(intent);
            finish();

            // TODO handle kill case
        }
    }

    private void getDumpstateBySSDTestTool() {
        if (mDumpStateFile == null) {
            return;
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }

        long startTime = System.currentTimeMillis();
        String filePath = null;

        // 20150416, sy_wang, Since we can't use su to run dumpstate in enforcing mode, we dump it by htcservice via SSD Test Tool
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            Log.d(TAG, "Run command: htcservice -s logctl :bugreport:");
            ProcessBuilder processBuilder = new ProcessBuilder("htcservice", "-s", "logctl", ":bugreport:").redirectErrorStream(true);
            java.lang.Process dumpstateProc = processBuilder.start();
            try { dumpstateProc.getOutputStream().close(); } catch (IOException e) {e.printStackTrace();}
            try { dumpstateProc.getErrorStream().close(); } catch (IOException e) {e.printStackTrace();}

            // Get dumpstate file path from htcservice's output message
            isr = new InputStreamReader(dumpstateProc.getInputStream());
            br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            String line = null;
            for (line = br.readLine(); line != null; line = br.readLine()) {
                if (sb.length() <= 1000)
                    sb.append(line).append("\n");

                if (line.startsWith(DEFAULT_DUMP_FOLDER)) {
                    filePath = line;
                }
            }
            if (DEBUG)
                Log.d(TAG, "Dump msg:\n"+sb);

        } catch (Exception e) {
            Log.e(TAG, "Exception when run htcservice", e);
        } finally {
            try {
                if(br != null)
                    br.close();
            } catch (IOException e) {
                Log.e(TAG, "error in closing object", e);
            }
            try {
                if(isr != null)
                    isr.close();
            } catch (IOException e) {
                Log.e(TAG, "error in closing object", e);
            }
        }

        // Read bugreport and zip it to cache folder
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        GZIPOutputStream gos = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;

        try {
            if (!TextUtils.isEmpty(filePath)) {
                Log.d(TAG, "Bugreport file path:" + filePath);

                /**
                 * 20160715, eric_lu, Google has new naming rule, take workaround to correct filepath name.
                 * e.g.
                 * Original filePath:
                 * dumpstate_20160715_122913.txt
                 *
                 * New naming rule:
                 * dumpstate_20160715_122913-NRD42E-undated.txt
                 */
                if (!new File(filePath).exists()) {
                    Pattern pattern = Pattern.compile(filePath.substring(DEFAULT_DUMP_FOLDER.length(),filePath.length()-DUMP_STATE_FILE_EXTENSION.length()));
                    Matcher matcher;
                    File folder = new File(DEFAULT_DUMP_FOLDER);
                    if (folder.exists()) {
                        String[] files = folder.list();
                        if (files != null) {
                            for (int i = 0; i < files.length; i++) {
                                matcher = pattern.matcher(files[i]);
                                if (matcher.find()) {
                                    filePath = DEFAULT_DUMP_FOLDER + files[i];
                                    if (DEBUG)
                                        Log.d(TAG, "Revised bugreport file path:" + filePath);
                                    break;
                                }
                            }
                        } else {
                            Log.d(TAG,"Fail to get files name");
                        }
                    } else {
                        Log.d(TAG,"Folder: " + DEFAULT_DUMP_FOLDER + " does not exist");
                    }
                }

                fos = new FileOutputStream(mDumpStateFile);
                bos = new BufferedOutputStream(fos);
                gos = new GZIPOutputStream(bos);

                fis = new FileInputStream(filePath);
                bis = new BufferedInputStream(fis);

                int bytesRead;
                byte[] buffer = new byte[256];
                while (true) {
                    bytesRead = bis.read(buffer);
                    if (bytesRead == -1) break;
                    gos.write(buffer, 0, bytesRead);
                }
                gos.flush();
            } else {
                Log.d(TAG, "Can't get file path from htcservice");
                mDumpStateFile = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "error in zip bugreport", e);
            mDumpStateFile = null;
        } finally {
            try {
                if(gos != null)
                    gos.close();
            } catch (IOException e) {
                Log.e(TAG, "error in closing object", e);
            }
            try {
                if(bos != null)
                    bos.close();
            } catch (IOException e) {
                Log.e(TAG, "error in closing object", e);
            }
            try {
                if(fos != null)
                    fos.close();
            } catch (IOException e) {
                Log.e(TAG, "error in closing object", e);
            }
            try {
                if(bis != null)
                    bis.close();
            } catch (IOException e) {
                Log.e(TAG, "error in closing object", e);
            }
            try {
                if(fis != null)
                    fis.close();
            } catch (IOException e) {
                Log.e(TAG, "error in closing object", e);
            }
        }

        // SELinux block our app to delete dumpstate file in /data/htclog/
//            // Delete the dumped bugreport file eventually
//            if (!TextUtils.isEmpty(filePath)) {
//                Utils.deleteFilesInDir(new File(filePath).getParentFile());
//            }

        if (DEBUG)
            Log.i(TAG, "dumpstate complete, spend " + (System.currentTimeMillis() - startTime) + "ms");
    }

    @Override
    protected void onDestroy() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        super.onDestroy();
    }

    // Use thread handler to run thread one by one, or create/delete dumpstate file may have conflict
    private static class ThreadUtil {
    	private static ThreadUtil sThreadHandler;
    	private ThreadUtil() {
    		if(mHThread == null){
    			mHThread = new HandlerThread("BugReportDumpThread");
    			mHThread.start();
    		}
    		if(mHandler == null)
    			mHandler = new Handler(mHThread.getLooper());
    	}

    	public static synchronized ThreadUtil getInstance() {
    		if(sThreadHandler == null)
    			sThreadHandler = new ThreadUtil();
    		return sThreadHandler;
    	}

    	public void post(Runnable r) {
    		mHandler.post(r);
    	}

    	private HandlerThread mHThread;
    	private Handler mHandler;
    }
}
