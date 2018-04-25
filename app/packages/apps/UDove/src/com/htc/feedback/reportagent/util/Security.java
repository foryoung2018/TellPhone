package com.htc.feedback.reportagent.util;

import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;

import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.pbdata.DeviceInfoHelper;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.util.Utils;

public class Security {
	private static final String TAG = "Security";
	private static final boolean _DEBUG = Common.DEBUG;
	private static final Pattern ROM_VERSION_PATTERN = Pattern.compile("[0-9]+[\\.][0-9]+[\\.][0-9]+[\\.][0-9]+");
	private static final Pattern ROM_999_PATTERN = Pattern.compile("[0-9]+[\\.][0-9]+[\\.]999[\\.][0-9]+"); 
	
	public static boolean isRom999(String buildDesc) {
		if(buildDesc == null)
			return false;

		Matcher matcher = ROM_999_PATTERN.matcher(buildDesc);
		boolean found = matcher.find();
		if(_DEBUG) {
			String foundStr = "";
			if(found) {
				foundStr = matcher.group();
			}
			Log.d("found 999 = "+found + ", str = "+foundStr);
		}
		return found;
	}
	
	public static boolean isRomVersionLegal(String buildDesc) {
		if(buildDesc == null)
			return false;

		Matcher matcher = ROM_VERSION_PATTERN.matcher(buildDesc);
		boolean found = matcher.find();
		if(_DEBUG) {
			String foundStr = "";
			if(found)
				foundStr = matcher.group();
			Log.d("found = "+found + ", str = "+foundStr);
		}
		return found;
	}
	
	public static boolean isSecureROM() {
		String romDesc = DeviceInfoHelper.getRomVersion("");
		boolean isRomVersionLegal = isRomVersionLegal(romDesc);
		boolean isUnlockedDevice = Utils.isUnlockedDevice();
		boolean isHackedSN = DeviceInfoHelper.isHackerDevice();
		boolean isSecure =  isRomVersionLegal && !isUnlockedDevice && !isHackedSN;
		if(_DEBUG || !isSecure)
			Log.d("isRomVersionLegal = "+isRomVersionLegal+", isUnlockedDevice = "+
					isUnlockedDevice+", isHackedSN = "+isHackedSN+" => isSecure = "+isSecure);
		return isSecure;
	}
}
