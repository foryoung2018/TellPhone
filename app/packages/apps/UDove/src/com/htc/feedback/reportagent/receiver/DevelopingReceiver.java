package com.htc.feedback.reportagent.receiver;

import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.ReportService;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.util.ReportConfig;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class DevelopingReceiver extends BroadcastReceiver {
	private static final String TAG = "DevelopingReceiver"; 

	@Override
	public void onReceive(Context context, Intent intent) {
		if(Common.DEBUG && intent != null) 
			Log.d(TAG, "[DevelopingReceiver] intent action:"+intent.getAction());
		
		if(!ReportConfig.isShippingRom() && context != null && intent != null 
				&& ReportService.ACTION_UPLOAD_UB_LOG.equals(intent.getAction())) {
			intent.setClass(context, ReportService.class);
			intent.setAction(ReportService.ACTION_UPLOAD_UB_LOG);
			context.startService(intent);
		} else if (!ReportConfig.isShippingRom() && context != null && intent != null
						&& "com.htc.intent.action.ENABLE_NCPOMELO_REPORT".equals(intent.getAction())){
			intent.setClass(context, ReportService.class);
			intent.setAction(ReportService.ACTION_ENABLE_NCPOMELO_REPORT);
			context.startService(intent);
		}
	}

}
