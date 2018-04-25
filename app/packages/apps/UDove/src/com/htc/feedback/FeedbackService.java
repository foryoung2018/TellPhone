package com.htc.feedback;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.app.ApplicationErrorReport;
import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.text.TextUtils;

import com.htc.feedback.reportagent.util.LogStream;
import com.htc.feedback.reportagent.Common;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.io.IOException;
import java.util.regex.PatternSyntaxException;

public class FeedbackService extends IntentService {
    private final static String TAG = "FeedbackService";
    private final static boolean DEBUG = Common.DEBUG;
    private final String logsDir = "logs";

    public FeedbackService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    	try {
			onHandleIntentInner(intent);
		} catch (Exception e) {
			Log.e(TAG, "Exception in onHandleIntentInner", e);
		}
    }
    
    private void onHandleIntentInner(Intent intent) {
        String tag = null;
        long time = -1;
        String radio = null;
        Intent reportIntent = new Intent("com.htc.intent.action.BUGREPORT");
        reportIntent.putExtras(intent);

        ApplicationErrorReport report = (ApplicationErrorReport) intent.getParcelableExtra(Intent.EXTRA_BUG_REPORT);
    	if(report == null) {
    		tag = intent.getStringExtra("tag");
    		time = intent.getLongExtra("time", System.currentTimeMillis());
    	} else {
        	tag = intent.getStringExtra("dropboxTag");
            time = report.time;
    	}
        DropBoxManager dbox = (DropBoxManager) getSystemService("dropbox");
        DropBoxManager.Entry entry = null;

        int count = 0;
        entry = getDropboxEntry(tag, time);
        while (entry == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e(TAG, "Interrupt " + e.getMessage());
            }
            if (++count == 120)
                break;
            entry = getDropboxEntry(tag, time);
        }

        if (entry == null) {
            // try last time
            entry = getDropboxEntry(tag, time);
            if (entry == null) {
                Log.e(TAG, "cannot get entry from dbox, skip. tag:" + tag + ", time:" + time);
                return;
            }
        }

        String path = null;
        String extraName = intent.getStringExtra("extra");// ex:tombstone file path name when system crash
        String tombstoneName = intent.getStringExtra("tombstoneName");
        String mediaServerTombstoneName = intent.getStringExtra("mediaServerTombstoneName");
        ArrayList<String> tombstones = new ArrayList<String>();
        if(!TextUtils.isEmpty(tombstoneName))
        	tombstones.add(tombstoneName);
        if(!ReportConfig.isShippingRom() && !TextUtils.isEmpty(mediaServerTombstoneName))
        	tombstones.add(mediaServerTombstoneName);
        String dumpstateFile = intent.getStringExtra("dumpstate");
        byte[] errorReportKey = intent.getByteArrayExtra("errorReportKey");
        byte[] errorReportIv = intent.getByteArrayExtra("errorReportIv");
        int oomPid = intent.getIntExtra("oomPid", 0);
        
        // sy_wang, 20131209, delete unnecessary temp files and folder with wrong permission
        deleteLogFolderOncePerFreshROM();
        
        if (DEBUG)
            Log.d(TAG, "dumpstateFile=" + dumpstateFile + ", extra=" + extraName 
            		+ ", tombstoneName=" + tombstoneName + ", mediaServerTombstoneName=" + mediaServerTombstoneName + ", oomPid="+oomPid);
        if (report != null && report.type == ApplicationErrorReport.TYPE_ANR) {
            File bugreportFile = dumpstateFile == null? null : new File(dumpstateFile);
            path = composeLog(dbox, tag, time, entry, bugreportFile, "dumpstate.txt.gz", null, errorReportKey, errorReportIv);
            if (dumpstateFile != null) {
                try {
                    bugreportFile.delete();
                } catch (Exception e) {
                    Log.e(TAG, "Exception when delete bugreportFile", e);
                }
            }
        } else if (report != null && (report.type == ApplicationErrorReport.TYPE_CRASH || report.type == ApplicationErrorReport.TYPE_NONE)
        		&& oomPid > 0 && !ReportConfig.isShippingRom()) { // app or system out of memory
			File hprofFile = getHprofFile(oomPid);
			if (hprofFile != null)
				path = composeLog(dbox, tag, time, entry, hprofFile, hprofFile.getName(), null, errorReportKey, errorReportIv);
			else
				path = readDropboxFileToLocal(entry, errorReportKey, errorReportIv);
        } else if (tombstones != null && tombstones.size() > 0) { //app native crash & system native crash
            path = composeLog(dbox, tag, time, entry, null, null, tombstones, errorReportKey, errorReportIv);
        } else {
        	// app java crash or LASTKMSG/HTC_HW_RST
        	path = readDropboxFileToLocal(entry, errorReportKey, errorReportIv);
        }

        if (path != null) {
        	reportIntent.putExtra("file", path);
        }

        if (entry != null) {
            entry.close();
        }

        long start = System.currentTimeMillis();
        radio = new AdditionalInfo(getApplicationContext()).getLocationInformation();
        if(DEBUG) Log.d(TAG,"It tooks "+(System.currentTimeMillis()-start)+" ms get location from service");
        
        reportIntent.putExtra("radio", radio);
        reportIntent.putExtra("tag", tag);
        reportIntent.putExtra("time", time);
        // sy_wang, 20140811, Since MyBreeze has been merged into UDove, we start its service directly
        reportIntent.setComponent(new ComponentName("com.htc.feedback", "com.htc.feedback.reportagent.ReportService"));
        startService(reportIntent);
        // sy_wang, 20130724, TELLHTC_ERROR_EVENT this intent is for addSentErrorCount in HDIM only, so use HDIM's permission with signature protection
        Intent errorEventIntent = (Intent)reportIntent.clone();
        errorEventIntent.setAction("com.htc.intent.action.TELLHTC_ERROR_EVENT");
        errorEventIntent.setComponent(null);
        sendBroadcast(errorEventIntent, "com.htc.permission.DEVICEINFO_INTERNAL_USAGE");
        if (Common.SECURITY_DEBUG)
            Log.d(TAG, "send intent tag:" + tag + " time:" + time + " radio:" + radio);
        else
        	Log.d(TAG, "send intent tag:" + tag + " time:" + time);
        sendBroadcast(new Intent("com.htc.updater.NOTIFY_SEND"));
    }
    
    // sy_wang, 20131209, After sense40, since the temp log file won't be deleted after put into dropbox and the log folder permission is not private, 
    // we should delete the problem folder and files in it. But this action is only need to do once, the new created folder is private and temp log files
    // will be deleted after added to dropbox.
    private void deleteLogFolderOncePerFreshROM() {
        try {
            boolean hasChecked = ErrorReportPreference.getHasCheckedLogFolder(this);
            if (hasChecked) {
                // Do nothing
                if (DEBUG)
                    Log.d(TAG, "Log folder has been checked");
            } else {
                // We need to get the correct folder which we may have created. So, we use getDir although it will create the folder if not exist.
                File dir = getDir(logsDir, Context.MODE_PRIVATE);
                // Delete the log folder and files in it.
                if (dir.exists()) {
                    Utils.deleteFilesInDir(dir);
                    if (dir.delete()) {
                        ErrorReportPreference.setHasCheckedLogFolder(this);
                        Log.d(TAG, "Delete logs folder succeeded, path=" + dir.getAbsolutePath());
                    } else {
                        Log.d(TAG, "Delete logs folder failed, path=" + dir.getAbsolutePath());
                    }
                } else {
                    ErrorReportPreference.setHasCheckedLogFolder(this);
                    Log.d(TAG, "There is no log folder");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in deleteLogFolderOncePerFreshROM", e);
        }
    }
    
    private String readDropboxFileToLocal(DropBoxManager.Entry entry, byte[] errorReportKey, byte[] errorReportIv) {
    	String path = null;
        InputStream is = null;
        OutputStream os = null;
        try {
        	File dir = getDir(logsDir, Context.MODE_PRIVATE);
            path = dir.getAbsolutePath() + "/logs." + System.currentTimeMillis() + ".zip";
        	os = new FileOutputStream(path);
        	if(errorReportKey == null || errorReportIv == null)
        		is = entry.getInputStream();
        	else
        		is = LogStream.concatenateInputStream(entry.getInputStream(), errorReportKey, errorReportIv);
			if (is != null) {
				byte[] buffer = new byte[4096];
	            int size = 0;
	            while ((size = is.read(buffer, 0, 4096)) != -1) {
	            	os.write(buffer, 0, size);
	            }
			} else {
				Log.d(TAG, "InputStream from dropbox is null");
				path = null;
			}
		} catch (Exception e) {
			Log.e(TAG, "Exception in getDropboxFile", e);
			if (path != null) {
                new File(path).delete();
                path = null;
            }
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				Log.e(TAG, "Error when is.close()", e);
			}
			try {
				if (os != null)
					os.close();
			} catch (IOException e) {
				Log.e(TAG, "Error when os.close()", e);
			}
		}
        
        return path;
    }
    
    private String composeLog(DropBoxManager dbox, String tag, long time, DropBoxManager.Entry entry, File file, String name, ArrayList<String> tombstones, byte[] errorReportKey, byte[] errorReportIv) {
        String path = null;
        InputStream is = null;
        ZipOutputStream zip = null;
        FileInputStream fis_log = null;
        FileInputStream fis_tombstone = null;
        try {
            File dir = getDir(logsDir, Context.MODE_PRIVATE);
            path = dir.getAbsolutePath() + "/logs." + System.currentTimeMillis() + ".zip";
            String tmp = dir.getAbsolutePath() + "/tmp.zip";
            zip = new ZipOutputStream(new FileOutputStream(tmp));
            is = LogStream.concatenateInputStream(entry.getInputStream(), errorReportKey, errorReportIv);
            if (is != null) {
                compressLog(zip, "Logfile", is);
            } else {
                entry = getDropboxEntry(tag, time);
                if (entry != null) {
                	// [CQG #20123797] sy_wang, 20130507, begin
                	InputStream inputStream = entry.getInputStream();
                	if(inputStream != null) {
                		compressLog(zip, "Logfile", inputStream);
                	}
                	else
                		Log.d(TAG, "entry.getInputStream() is null");
                	// [CQG #20123797] sy_wang, 20130507, end
                }
                else {
                	Log.d(TAG, "entry is null");
                }
            }
            
            if (file != null) {
                /// [CQG #11952498] yunmei_lu, 20120925, begin
                fis_log = new FileInputStream(file);
                compressLog(zip, name, fis_log);
                /// [CQG #11952498] end
            }
            if(tombstones != null) {
            	for (int i = 0; i < tombstones.size(); i++) {
            		String tombstoneName = tombstones.get(i);
            		
                    if(tombstoneName != null) {
                    	DropBoxManager.Entry tombstoneEntry = getDropboxEntry(tombstoneName, time);
                        if (tombstoneEntry != null) {
                        	InputStream inputStream = LogStream.concatenateInputStream(tombstoneEntry.getInputStream(), errorReportKey, errorReportIv);
                        	if(inputStream != null) {
                        		compressLog(zip, tombstoneName, inputStream);
                        	}
                        	else
                        		Log.d(TAG, "tombstoneEntry.getInputStream() is null, tombstoneName="+tombstoneName);
                        	
                        	tombstoneEntry.close();
                        }
                        else {
                        	Log.d(TAG, "tombstoneEntry is null");
                        }
                    } else {
                    	Log.d(TAG, "tombstoneName is null");
                    }
            	}
            }

            zip.finish();
            
            if(!new File(tmp).renameTo(new File(path)))
                path = tmp;
        } catch (Exception ex) {
            Log.e(TAG, "error in send report ", ex);
            if (path != null) {
                try {
                    new File(path).delete();
                } catch (Exception e) {
                	Log.v(TAG, "error composeLog (delete file path)");
                }
            }
            path = null;
        } finally {
           try {
               if(fis_log != null)
            	   fis_log.close();
           } catch (IOException e) {
        	   Log.e(TAG, "error composeLog", e);            
           }
           try {
        	   if(fis_tombstone != null)
        		   fis_tombstone.close();
           } catch (IOException e) {
        	   Log.e(TAG, "error composeLog", e);            
           }
           try {
               if(is != null)
            	   is.close();
           } catch (IOException e) {
        	   Log.e(TAG, "error composeLog", e);            
           }
           try {
               if(zip != null)
            	   zip.close();
           } catch (IOException e) {
        	   Log.e(TAG, "error composeLog", e);            
           }
        }
        return path;
    }
    
    private void compressLog(ZipOutputStream zip, String name, InputStream in) {
        ZipEntry zipEntry = new ZipEntry(name);
        try {
            zip.putNextEntry(zipEntry);
            byte[] buffer = new byte[4096];
            int size = 0;
            while ((size = in.read(buffer, 0, 4096)) != -1) {
                zip.write(buffer, 0, size);
            }
            zip.closeEntry();
            in.close();
        } catch (Exception ex) {
            Log.e(TAG, "error in compressLog ", ex);
        }
    }
    
    private DropBoxManager.Entry getDropboxEntry(String tag, long time) {
    	DropBoxManager.Entry entry = null;
    	try { //M7_UL_JB_50, #10048, getNextEntry in IDropBoxManagerService maybe throw exception.
    		DropBoxManager dbox = (DropBoxManager) getSystemService("dropbox");
    		if (dbox != null)
    			entry = dbox.getNextEntry(tag, time);  		
    	} catch (Exception e) {
    		entry = null;
        	Log.e(TAG, "getDropboxEntry error", e);
        }
    	return entry;
    }

    private File getHprofFile(final int oomPid) {
        long targetTime = System.currentTimeMillis();
        long timeRange = 1000 * 60 * 3;
        FileFilter ff = new FileFilter() {
            @Override
            public boolean accept(File file) {
                // Prevent from listing sub directories
                String hprofileName = file.getName();
                if (file.isFile()) {
                    // hprofile format:
                    // /data/misc/hprof/heap-dump-tm%d-pid%d.hprof
                    if (hprofileName.startsWith("heap-dump") && hprofileName.endsWith(".hprof")
                            && hprofileName.contains("pid" + oomPid))
                        return true;
                }
                return false;
            }
        };
        /**
         * [2016.8.10 Eric Lu]
         *
         * If untrusted app occur OMM, hprof will be created in hprof_untrusted
         * folder from N. And trusted app will still created in hprof folder. So
         * we search both path for hprof file.
         *
         * Original hprof file path:
         * /data/misc/hprof/
         *
         * Newly added hprof file path:
         * /data/misc/hprof_untrusted/
         *
         * file name example:
         * heap-dump-1470797060-pid10061.hprof
         */
        File[] files = new File("/data/misc/hprof_untrusted/").listFiles(ff);
        if ((files == null) || (files != null && files.length == 0)) {
            if (DEBUG)
                Log.d(TAG, "Fail to get hprof from hprof_untrusted folder");
            files = new File("/data/misc/hprof/").listFiles(ff);
        }
        if (files == null) {
            Log.d(TAG, "Fail to access hprof folder");
        } else if (files.length != 0) {
            try {
                Arrays.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        if (f1.lastModified() < f2.lastModified()) {
                            return 1;
                        } else if (f1.lastModified() > f2.lastModified()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                });

                int count = 0;
                for (File f : files) {
                    // If there are more than 10 hprof files, we only check 10
                    // ones
                    if (count < 10)
                        count++;
                    else
                        break;

                    // Only get the hprof file created before target timestamp
                    // in specific time range
                    if (targetTime > 0 && timeRange > 0) {
                        long timeDiff = targetTime - f.lastModified();
                        if (timeDiff < 0 || timeDiff > timeRange)
                            continue;
                        else
                            return f;
                    }

                }
            } catch (Exception e) {
                Log.e(TAG, "error in checking tombstone files", e);
            }
        }

        return null;
    }
}
