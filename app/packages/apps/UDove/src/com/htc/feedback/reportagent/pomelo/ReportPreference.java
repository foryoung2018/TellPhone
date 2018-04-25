package com.htc.feedback.reportagent.pomelo;

import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.Log;

import android.content.Context;
import android.content.SharedPreferences;

public class ReportPreference {
	
	private final static String TAG = "ReportPreference";
	private final static boolean _DEBUG = Common.DEBUG;
	
	private final static String preferenceName = "Report";
	private final static String KEY_APP_CRASH = "UploadedAppCrashCount";
	private final static String KEY_APP_ANR = "UploadedAppANRCount";
	private final static String KEY_SYSTEM_CRASH = "UploadedSystemCrashCount";
	private final static String KEY_LASTKMSG = "UploadedLastKmsgCount";

	public static void removeAll(Context context){
        SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
	}
	
	public synchronized static long getNewSN(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		long value = preferences.getLong("LatestSerialNumber", -1L);
		value++;

		SharedPreferences.Editor editor = preferences.edit();
	    editor.putLong("LatestSerialNumber", value);       
        editor.commit();
        
		return value;
	}
	
	public static long getLastUPUpload(Context context){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		long value = preferences.getLong("LastUPUpload", -0L);
		return value;
	}
	
	public static void setLastUPUpload(Context context){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		long value=System.currentTimeMillis();
		SharedPreferences.Editor editor = preferences.edit();
	    editor.putLong("LastUPUpload", value);       
        editor.commit();
	}
	
	// The connected state for report agent upload checking, 0 indicates not connected
	public static int getCurrentPowerConnectedState(Context context){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		int value = preferences.getInt("CurrentPowerConnected", 0);
		return value;
	}
	
	// Set this state while Power is connected/disconnected
	public static void setCurrentPowerConnectedState(Context context,int value){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("CurrentPowerConnected", value);
		editor.commit();
	}
	
	// get HTC_APP_CRASH count
	public static long getUploadedAppCrashCount(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		long value = preferences.getLong(KEY_APP_CRASH, 0);
		return value;
	}
	
	// set HTC_APP_CRASH count
	public static void setUploadedAppCrashCount(Context context, long value) {
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(KEY_APP_CRASH, value);
		editor.commit();
	}
	
	// get HTC_APP_ANR count
	public static long getUploadedAppANRCount(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		long value = preferences.getLong(KEY_APP_ANR, 0);
		return value;
	}
	
	// set HTC_APP_ANR count
	public static void setUploadedAppANRCount(Context context, long value) {
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(KEY_APP_ANR, value);
		editor.commit();
	}
	
	// get SYSTEM_CRASH count
	public static long getUploadedSystemCrashCount(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		long value = preferences.getLong(KEY_SYSTEM_CRASH, 0);
		return value;
	}
	
	// set SYSTEM_CRASH count
	public static void setUploadedSystemCrashCount(Context context, long value) {
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(KEY_SYSTEM_CRASH, value);
		editor.commit();
	}
	
	// get LASTKMSG count
	public static long getUploadedLastKmsgCount(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		long value = preferences.getLong(KEY_LASTKMSG, 0);
		return value;
	}
	
	// set LASTKMSG count
	public static void setUploadedLastKmsgCount(Context context, long value) {
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(KEY_LASTKMSG, value);
		editor.commit();
	}
}