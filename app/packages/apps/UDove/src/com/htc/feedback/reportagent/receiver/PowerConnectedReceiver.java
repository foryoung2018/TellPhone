package com.htc.feedback.reportagent.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.provider.Settings;
import com.htc.feedback.reportagent.util.*;
//change to ReportAgent Log for consistent Log Tag
import com.htc.feedback.reportagent.*;

public class PowerConnectedReceiver extends BroadcastReceiver 
{
	private static final String TAG = "PowerConnectedReceiver";
	private static final boolean _DEBUG = Common.DEBUG;

	@Override
	public void onReceive(Context context, Intent intent) {
		if(_DEBUG)Log.v(TAG, "Recv Intent: "+intent);			
		String action = intent.getAction();
		intent.setClass(context, ReportService.class);
		if (Intent.ACTION_POWER_CONNECTED.equals(action)) {		
			context.startService(intent);
		}
	}
}
