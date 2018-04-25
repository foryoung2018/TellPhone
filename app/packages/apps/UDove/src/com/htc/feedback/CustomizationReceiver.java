package com.htc.feedback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.htc.feedback.reportagent.Common;

public class CustomizationReceiver extends BroadcastReceiver {
    private static final boolean LOG = Common.DEBUG;
    private static final String TAG = "CustomizationReceiver";

    public void onReceive(Context context, Intent intent) {
    	if(intent != null) {
	        if (LOG)
	            Log.d(TAG, "onReceive " + intent.getAction());
			
	    	FeedbackReceiver.startReceiverService(context, intent);
    	} else {
    		if (LOG)
	            Log.d(TAG, "Received intent is null" );
    	}
    }
}
