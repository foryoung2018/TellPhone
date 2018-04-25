package com.htc.feedback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.htc.feedback.reportagent.Common;

public class SystemCrashActivity extends Activity {
    private static final String TAG = "SystemCrashActivity";
    private static final boolean LOG = Common.DEBUG;
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (LOG) {
            Log.d(TAG, "onCreate");
        	logIsKernelCrash();
        }        
        
        AlertDialog.Builder builder = new AlertDialog.Builder(SystemCrashActivity.this);
        builder.setCancelable(false);
        builder.setMessage(R.string.msg_system_crash);
        builder.setNegativeButton(R.string.btn_send_report, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	Intent reportIntent = new Intent(SystemCrashActivity.this, ReportActivity.class);
                reportIntent.putExtras(getIntent());
                reportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(reportIntent);
                finish();
            }
        });
        builder.setPositiveButton(R.string.btn_dont_send, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	dialog.cancel();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

    private void logIsKernelCrash() {
    	Intent comingIntent = getIntent();
    	if (comingIntent != null) {
    		Parcelable report = comingIntent.getParcelableExtra(Intent.EXTRA_BUG_REPORT);
    		Log.d(TAG, "isKernelCrash: " + (report == null ? "true" : "false"));
    	}
    }

}
