package com.htc.feedback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

import android.app.Activity;
import android.app.ApplicationErrorReport;
import android.content.Intent;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.htc.feedback.reportagent.util.LogStream;
import com.htc.feedback.reportagent.Common;
import com.htc.lib1.theme.NavBarColorUtil;

public class PreviewInfoActivity extends Activity {
    private static final String TAG = "PreviewInfoActivity";
    private static final boolean LOG = Common.DEBUG;

    private WebView mWebView;
    String tag = null;
    long time = -1;
    boolean destory = false;
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LOG)
            Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.specific_feedback_preview);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //navigation bar color change feature(Only for SW navigation bar and 18:9 device)
        NavBarColorUtil.setNavBarBkg(getWindow());

        // sy_wang, 20131230, Since use TextView to preview large amount of texts such as Logfile may cause ANR from JB43, we use WebView instead
        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient() {
        	@Override
        	public void onPageFinished(WebView view, String url) {
            	setProgressBarIndeterminateVisibility(false);
            }
        });

        setProgressBarIndeterminateVisibility(true);
        Intent previewIntent = getIntent();
        String title = previewIntent.getStringExtra("title");
        String text = previewIntent.getStringExtra("text");
        
        if (text != null) {
            runOnUiThread(new Runner(text));
        } else {
            ApplicationErrorReport report = (ApplicationErrorReport) previewIntent
                    .getParcelableExtra(Intent.EXTRA_BUG_REPORT);
            if (report == null) {
            	tag = previewIntent.getStringExtra("tag");
        		time = previewIntent.getLongExtra("time", System.currentTimeMillis());
            } else {
            	String tombstoneName = previewIntent.getStringExtra("tombstoneName");
            	if (tombstoneName != null && title != null && title.equals(Utils.getLog2Title(this)))
            		tag = tombstoneName;
            	else
            		tag = previewIntent.getStringExtra("dropboxTag");
                time = report.time;
            }
        
            final byte[] errorReportKey = previewIntent.getByteArrayExtra("errorReportKey");
            final byte[] errorReportIv = previewIntent.getByteArrayExtra("errorReportIv");

            final DropBoxManager dbox = (DropBoxManager) getSystemService("dropbox");
            new Thread(new Runnable() {
                public void run() {
                    DropBoxManager.Entry entry = null;
                    int count = 0;
                    while ((entry = dbox.getNextEntry(tag, time)) == null) {
                        if (destory)
                            return;

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Interrupt " + e.getMessage());
                        }

                        if (++count == 12)
                            break;
                    }

                    if (destory)
                        return;

                    if (entry == null) {
                        // try last time
                        entry = dbox.getNextEntry(tag, time);
                        if (entry == null) {
                            Log.e(TAG, "cannot get entry from dbox, skip. tag:" + tag + ", time:" + time);
                            return;
                        }
                    }

                    BufferedReader br = null;
                    try {
                        InputStream is = LogStream.concatenateInputStream(entry.getInputStream(), errorReportKey, errorReportIv);
                        if (is != null) {
                            br = new BufferedReader(new InputStreamReader(is));
                        } else {
                            entry = dbox.getNextEntry(tag, time);
                            br = new BufferedReader(new InputStreamReader(entry.getInputStream()));
                        }
                        StringBuilder builder = new StringBuilder(5000);
                        for (String line = br.readLine(); line != null; line = br.readLine()) {
                            if (destory)
                                return;
                            builder.append(line);
                            builder.append('\n');
                        }
                        if (destory)
                            return;
                        runOnUiThread(new Runner(builder.toString()));
                    } catch (IOException e) {
                        Log.e(TAG, "error loading log file. tag:" + tag + ", time:" + time, e);
                    } catch (GeneralSecurityException e) {
                        Log.e(TAG, "Security error. tag:" + tag + ", time:" + time, e);
                    } finally {
                        if (entry != null) {
                            entry.close();
                            entry = null;
                        }
                        if (br != null) {
                            try {
                                br.close();
                            } catch (IOException e) {
                                Log.e(TAG, "error loading log file. tag:" + tag + ", time:" + time, e);
                            }
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destory = true;
    }
    
	@Override
    public void onResume() {
        if (LOG)
            Log.d(TAG, "onResume()");
        super.onResume();
    }
    
    // sy_wang, 20131230, Load text content by WebView
    private class Runner implements Runnable{
    	private String mText;
    	public Runner(String text_in){
    		if (text_in != null)
    			mText = text_in;
    		else
    			mText = "";
		}
    	
		public void run() {
			mWebView.loadDataWithBaseURL(null, mText, "text/plain", "utf-8", null);
		}
    }

}
