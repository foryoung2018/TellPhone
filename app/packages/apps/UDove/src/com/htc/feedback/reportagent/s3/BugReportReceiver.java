package com.htc.feedback.reportagent.s3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
// sy_temp
import com.htc.feedback.reportagent.util.ReflectionUtil;
//import com.htc.wrap.android.os.HtcWrapSystemProperties;
import android.provider.Settings;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.Common;

public class BugReportReceiver extends BroadcastReceiver 
{
	private static final String TAG = "BugReportReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		new Thread(new Runner(context,intent)).start();
		return;
	}

	private class Runner implements Runnable{
		private Intent intent;
		private Context context;
		
		public Runner(Context context_in, Intent intent_in){
			intent=intent_in;
			context=context_in;
		}

		public void run() {
			Log.v(TAG, "Recv Intent: "+intent);
			
			String sBuildType=ReflectionUtil.get("ro.build.type").trim();
			//Log.d(TAG, "ro.build.type="+sBuildType);
			if (sBuildType.equalsIgnoreCase("userdebug")){
				//enable bugreport
			}else if (sBuildType.equalsIgnoreCase("user")){
				int iBugReportSetting = Settings.Secure.getInt(context.getContentResolver(), Common.SEND_HTC_ERROR_REPORT,0);
			    if(iBugReportSetting ==1 ){
					//enable
				}else if (iBugReportSetting ==0 ){
					//disable bugreport
					// TODO: delete the files if needs.
					Log.v(TAG, "BugReport Disableed by Settings.Secure:Intent drop");
					return;
				}else{
					//disable bugreport
					Log.e(TAG, "Strange setting from Settings.Secure, Check customization,BugReport Disableed");
					return;
				}
			}
			
			String action = intent.getAction();
			intent.setClass(context, BugReportService.class);
			
			if (action.equals("com.htc.intent.action.BUGREPORT")){
				context.startService(intent);
			}else if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")){
				context.startService(intent);
			}else if (action.equals("android.provider.Telephony.SECRET_CODE")){
			    Log.v(TAG, "Recv REPORT request by secret code");
			}
		}
	}
}
