package com.htc.feedback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.htc.feedback.reportagent.Common;

public class FeedbackReceiver extends BroadcastReceiver {
    private static final boolean LOG = Common.DEBUG;
    private static final String TAG = "FeedbackReceiver";

    public void onReceive(Context context, Intent intent) {
    	if(intent != null) {
	        if (LOG)
	            Log.d(TAG, "onReceive " + intent.getAction());
			
	    	startReceiverService(context, intent);
    	} else {
    		if (LOG)
	            Log.d(TAG, "Received intent is null");
    	}
    }
    
	public static void startReceiverService(Context context, Intent intent) {
		if(intent != null) {
			String action = intent.getAction();
	        Context appContext = context.getApplicationContext();
	        Intent receiverIntent = new Intent(appContext, ReceiverService.class);
	        
	        receiverIntent.setAction(action);
	        receiverIntent.putExtras(intent);
	        appContext.startService(receiverIntent);
		}
	}
}
