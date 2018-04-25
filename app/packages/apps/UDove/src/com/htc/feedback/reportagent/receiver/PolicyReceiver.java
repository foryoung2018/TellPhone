package com.htc.feedback.reportagent.receiver;

import java.util.ArrayList;

import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.ReportService;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.util.ReportConfig;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class PolicyReceiver extends BroadcastReceiver {

	private final static String TAG = "PolicyReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {

        if ( null == intent ) return;

        String action = intent.getAction();
        Log.d(TAG, " receieve: " + action);
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            ReportService.perform(context, ReportService.ACTION_BOOT_COMPLETE);
        }
        // Manually set policy for verification
        else if (!ReportConfig.isShippingRom() && "com.htc.intent.action.SET_POLICY".equals(action)) {
            String appid = intent.getStringExtra("appid");
            String category = intent.getStringExtra("category");
            String key = intent.getStringExtra("key");
            key = TextUtils.isEmpty(key) ? "enable" : key;
            String value = intent.getStringExtra("value");
            long dueDate = intent.getLongExtra("dueDate", 7263223678000L);
            Log.d(TAG, "appid = "+appid+", category = "+category+", key = "+key+", value = "+value+", dueDate="+dueDate);
            android.os.Bundle bundle = new android.os.Bundle();
            com.htc.feedback.policy.PolicyUtils.putPolicy(bundle, appid, category, key, value, dueDate);
            com.htc.feedback.reportagent.policy.HandsetPolicyUpdater updater = new com.htc.feedback.reportagent.policy.HandsetPolicyUpdater(context);
            updater.applyPolicy2Provider(bundle);
        }
		
		// comment out for this action is listened by ReportAgentReceiver and both start ReportService
//			else if ( ConnectivityManager.CONNECTIVITY_ACTION.equals(action) ) {
//
//			ReportService.perform(context, ReportService.ACTION_CONNECTION_CHANGE);
//
//		} 
//		else if ( "com.htc.intent.action.TELLHTC_SETTING_CHANGE".equals(action) ) {
//			
//			String type = intent.getStringExtra("type");
//			if ( null == type || 0 == type.length() )
//				return;
//			
//			if ( "error_report".equals(type) || "application_log".equals(type)) {
//				ReportService.perform(context, ReportService.ACTION_POLICY_SETTING_CHANGE);
//			} else if ( "prefer_network".equals(type) ) {
//				ReportService.perform(context, ReportService.ACTION_CONNECTION_CHANGE);
//			}
//			
//		} else if ( "com.htc.intent.action.ULOG_POLICY_CHANGED".equals(action) ) {
//
//			boolean isSIE = intent.getBooleanExtra("isSIE", false);
//			if (isSIE) {
//				//set TypePreference .added by Ricky,2012.07.26
//				String mode = intent.getStringExtra("ui_Mode");
//				if(mode != null){
//					TypePreference.setTypeStatus(context, mode);
//					Log.d(TAG,"set ui mode="+mode);
//				}
//				
//				ReportService.perform(context, ReportService.ACTION_SETUP_WIZARD_FINISHED);
//			} else {
//				ReportService.perform(context, ReportService.ACTION_ULOG_POLICY_CHANGED);
//			}
//			
//		} else if ( Intent.ACTION_TIME_CHANGED.equals(action) ) {
//			
//			ReportService.perform(context, ReportService.ACTION_TIME_SET);
//			
//		}
	}
	
}