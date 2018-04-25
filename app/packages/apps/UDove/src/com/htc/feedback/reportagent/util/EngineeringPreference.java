package com.htc.feedback.reportagent.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.htc.feedback.reportagent.Common;

public class EngineeringPreference {
	private final static String TAG = "EngineeringPreference";
	private final static String preferenceName = "Engineering";
	private final static String SEND_TO_NC_POMELO = "SendToNCP";
	private final static String LATITUDE = "Latitude";
	private final static String LONGITUDE = "Longitude";
	private final static String UPDATE_TIME = "UpdateTime";
	private final static String UP_LOG_ID = "UPLogID";
	private final static String UB_LOG_ID = "UBLogID";
	private final static String S3_SENTINEL = "S3Sentinel";
	
	public static boolean canSendReportToNCPomelo(Context context){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		boolean canSend = preferences.getBoolean(SEND_TO_NC_POMELO, false);
		Log.v(TAG,"[canSendReportToNCPomelo] canSend:"+canSend);
		return canSend;
	}
	
	public static void enableSendReportToNCPomelo(Context context){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		if(canSendReportToNCPomelo(context)){
			Log.v(TAG,"[enableSendReportToNCPomelo] Already enable to send report to NC Pomelo.");
			return;
		}
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(SEND_TO_NC_POMELO, true);       
		editor.commit();
		Log.v(TAG,"[enableSendReportToNCPomelo] Begin to send engineering report to NC Pomelo.");
	}
	
	public static void disableSendReportToNCPomelo(Context context){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		if(!canSendReportToNCPomelo(context)){
			Log.v(TAG,"[enableSendReportToNCPomelo] Already disable to send report to NC Pomelo.");
			return;
		}
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(SEND_TO_NC_POMELO, false);       
		editor.commit();
		Log.v(TAG,"[enableSendReportToNCPomelo] Stop to send engineering report to NC Pomelo.");
	}
	
	//----------------Location related preferences-------------------
	public static long getLocationUpdateTime(Context context){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		long fixTime = preferences.getLong(UPDATE_TIME, 0);
		if (Common.SECURITY_DEBUG) Log.v(TAG,"[getLocationUpdateTime] " + fixTime);
		return fixTime;
	}
	
	public static float getLatitude(Context context){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		float latitude = preferences.getFloat(LATITUDE, 0);
		return latitude;
	}
	
	public static float getLongitude(Context context){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		float longitude = preferences.getFloat(LONGITUDE, 0);
		return longitude;
	}
	
	public static void setLocationUpdateTime(Context context, long fixTime){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(UPDATE_TIME, fixTime);
		editor.commit();
		if (Common.SECURITY_DEBUG) Log.v(TAG,"[setLocationUpdateTime] " + fixTime);
	}
		
	public static void setLatitude(Context context, float latitude){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putFloat(LATITUDE, latitude);
		editor.commit();
	}
	
	public static void setLongitude(Context context, float longitude){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putFloat(LONGITUDE, longitude);
		editor.commit();
	}
	
	//---------------------UP/UB LOG ID------------------------
	public static int getLogID(Context context, String tag){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		int logID = 0;
		if(Common.STR_HTC_UP.equals(tag))
			logID = preferences.getInt(UP_LOG_ID, 0);
		else if(Common.STR_HTC_UB.equals(tag))
			logID = preferences.getInt(UB_LOG_ID, 0);
		if (Common.DEBUG) Log.v(TAG,"[getUploadCount] tag: " + tag + " upload count: " + logID);
		return logID;
	}
	
	public static void setLogID(Context context, String tag, int id){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor editor = preferences.edit();
		if(Common.STR_HTC_UP.equals(tag))
			editor.putInt(UP_LOG_ID, id);
		else if(Common.STR_HTC_UB.equals(tag))
			editor.putInt(UB_LOG_ID, id);
		else
			return;
		editor.commit();
	}
	
	//------------------S3 Log cache mechanism for backward compatible-------------------
	public static boolean getS3SentinelValue(Context context){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		boolean value = preferences.getBoolean(S3_SENTINEL, false);
		if (Common.DEBUG) Log.v(TAG,"[S3_SENTINEL] value:" + value);
		return value;
	}
	
	public static void setS3SentinelValue(Context context, boolean value){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(S3_SENTINEL, value);
		editor.commit();
		if (Common.DEBUG) Log.v(TAG,"[S3_SENTINEL] update value:" + value);
	}
	
}
