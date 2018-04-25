package com.htc.feedback.reportagent.policy;


import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.policy.PolicyManager;

import android.content.Context;
import android.content.SharedPreferences;


public class PolicyPreference {
	
	private final static String TAG = "PolicyPreference";
	private final static boolean _DEBUG = Common.DEBUG;
	
	private final static String preferenceName = "Policy";

	static void removeAll(Context context){
        SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
	}
	
	public static long getPolicyLastUpdate(Context context)
	{
		 SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		 long value = preferences.getLong("PolicyLastUpdate", 0L);
		 return value;
	}
	
	static void setPolicyLastUpdate(Context context, long value)
	{
        SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("PolicyLastUpdate", value);       
        editor.commit();
	}

    public static long getAlarmBaseline(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
        long value = preferences.getLong("AlarmBaseline", -1L);
        if (_DEBUG) Log.d(TAG, preferenceName + " alarm timebase: " + value);
        return value;
    }
    
    public static void setAlarmBaseline(Context context, long value) {
        SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("AlarmBaseline", value);       
        editor.commit();
    }

    public static boolean isDefaultPolicyAlreadyLoad(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
        boolean isLoad = preferences.getBoolean("DefaultPolicyAlreadyLoad", false);
        if (_DEBUG) Log.d(TAG, "Is default policy already load: " + isLoad);
        return isLoad;
    }

    public static void setDefaultPolicyAlreadyLoad(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("DefaultPolicyAlreadyLoad", true);
        editor.commit();
    }

}
