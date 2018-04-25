package com.htc.feedback.reportagent.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.htc.feedback.reportagent.ReportService;
import com.htc.feedback.reportagent.util.Log;

public class SetupWizardReceiver extends BroadcastReceiver {
    private static final String TAG = "SetupWizardReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        // test begin // TODO: remove them
        boolean launchedBySystem = "LaunchedBySystem".equals(intent.getStringExtra("SetupWizardLaunchedBy"));
        boolean launchedByIcon = "LaunchedByIcon".equals(intent.getStringExtra("SetupWizardLaunchedBy"));
        Log.d(TAG, "LaunchedBySystem="+launchedBySystem+", LaunchedByIcon="+launchedByIcon+", intent ="+intent);
        Log.d(TAG, "launchec by = "+intent.getStringExtra("SetupWizardLaunchedBy")+", intent ="+intent);
        // test end
        
        if(intent == null)
            return;
        if(ReportService.ACTION_SETUP_WIZARD_FINISHED.equals(intent.getAction()) && 
                "LaunchedBySystem".equals(intent.getStringExtra("SetupWizardLaunchedBy"))) {
            ReportService.perform(context, ReportService.ACTION_SETUP_WIZARD_FINISHED);
        }
    }

}
