package com.htc.feedback.reportagent.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.provider.Settings;

import com.htc.feedback.ReceiverService;
import com.htc.feedback.reportagent.util.*;
//change to ReportAgent Log for consistent Log Tag
import com.htc.feedback.reportagent.*;

public class ReportAgentReceiver extends BroadcastReceiver 
{
	private static final String TAG = "ReportAgentReceiver";
	private static final boolean _DEBUG = Common.DEBUG;

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent != null && intent.getAction() != null)
			Log.i(TAG,"[onReceive] "+intent.getAction());
		ThreadUtil.getInstance().post(new Runner(context,intent));
	}

	private class Runner implements Runnable{
		private Intent intent;
		private Context context;
		
		public Runner(Context context_in, Intent intent_in){
			intent=intent_in;
			context=context_in;
		}

		public void run() {
			if(_DEBUG)Log.v(TAG, "Recv Intent: "+intent);
			
			String action = intent.getAction();
			intent.setClass(context, ReportService.class);
			
			if (ReportService.ACTION_UPLOAD_ERROR_LOG.equals(action)) {
				// sy_wang, From L, LASTKMSG and HTC_HW_RST go action BUGREPORT flow, but we still need to show UI for it
				if(intent.getBooleanExtra("fromDropBox", false) 
						&& ("LASTKMSG".equals(intent.getStringExtra("tag")) || "HTC_HW_RST".equals(intent.getStringExtra("tag")))) {
					intent.setClass(context, ReceiverService.class);
					intent.setAction("com.htc.updater.NOTIFY_KERNEL_CRASH");
				}
				Log.d(action);
				context.startService(intent);
			} 
		}
	}
}
