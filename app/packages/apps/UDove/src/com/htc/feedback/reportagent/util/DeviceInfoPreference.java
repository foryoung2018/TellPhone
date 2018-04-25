package com.htc.feedback.reportagent.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.htc.feedback.reportagent.Common;

public class DeviceInfoPreference {
	private final static String TAG = "DeviceInfoPreference";
	private final static String preferenceName = "DeviceInformation";
	private final static String CHECK_COUNT = "checkCount";
	private final static String SENTINEL = "Sentinel";

	//--------------Fields which need to be record in preference-----------
	public final static String ROM_VERSION = "RomVersion";
	public final static String CHANGE_LIST = "ChangeList";
	public final static String ANDRIOD_ID = "Aid";
	public final static String FINGERPRINT = "Fingerprint";
	public final static String RADIO = "Radio";
	public final static String REVISION = "Revision";
	public final static String BUILD_TYPE = "BuildType";
	public final static String FRAMEWORK_VERSION = "FrameworkVersion";
	public final static String BUILD_IDENTIFY = "BuildIdentify";
	public final static String BUILD_LINE = "ro.build.buildline";
	public final static String KEY_SET = "KeySet";
	public final static String SENSE_VERSION = "SenseVersion";
	public final static String BUILD_DATE_ID = "BuildDateId";
	public final static String UNLOCKED_DEVICE = "unlockedDevice";
	public final static String REPORT_TYPE = "ReportType";
	public final static String SCHK1 = "Schk1";
	public final static String SCHK2 = "Schk2";
	//----------------------------------------------------------------------
	
	public static boolean checkNeedToUpdate(Context context, String tag){
		if(Common.STR_HTC_UB.equals(tag) || Common.STR_HTC_UP.equals(tag)){
			SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
			SharedPreferences.Editor editor = preferences.edit();
			int checkCount = preferences.getInt(CHECK_COUNT, 0);
			Log.v(TAG,"[checkNeedToUpdate] tag" + tag + " check count" + checkCount);
			checkCount ++;
			if(checkCount == 2){
				checkCount = 0;
				editor.putInt(CHECK_COUNT, checkCount);
				editor.commit();
				return true;
			}else{
				editor.putInt(CHECK_COUNT, checkCount);
				editor.commit();
				return false;
			}
		}
		return false;
	}
	
	public static boolean getSentinelValue(Context context){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		boolean value = preferences.getBoolean(SENTINEL, false);
		if (Common.SECURITY_DEBUG) Log.v(TAG,"[SENTINEL] value:" + value);
		return value;
	}
	
	public static void setSentinelValue(Context context, boolean value){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(SENTINEL, value);
		editor.commit();
		if (Common.SECURITY_DEBUG) Log.v(TAG,"[SENTINEL] update value:" + value);
	}
	
	//------------------------Getter------------------------//
	public static String getDeviceInfoString(Context context, String field, String defaultString){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		String result = preferences.getString(field, defaultString);
		if (Common.SECURITY_DEBUG) Log.v(TAG,"[getDeviceInfoString] field: " + field + " result:" + result);
		return result;
	}

	//------------------------Setter------------------------//
	public static void setDeviceInfoString(Context context, String field, String value){
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(field, value);
		editor.commit();
		if (Common.SECURITY_DEBUG) Log.v(TAG,"[setDeviceInfoString] field: " + field + " value:" + value);
	}
}
