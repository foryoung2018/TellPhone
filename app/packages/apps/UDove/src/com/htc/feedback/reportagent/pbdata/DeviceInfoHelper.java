package com.htc.feedback.reportagent.pbdata;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.htc.feedback.policy.UPolicy;
import com.htc.feedback.reportagent.Common;
import com.htc.xps.pomelo.log.DeviceInfo;
import com.htc.xps.pomelo.log.DeviceInfo.Builder;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.util.ReflectionUtil;
import com.htc.feedback.reportagent.util.ReportConfig;
import com.htc.feedback.reportagent.util.Utils;

import org2.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3;
import org2.bouncycastle.jcajce.provider.digest.SHA3.Digest224;

public class DeviceInfoHelper {
	private final static String TAG = "DeviceInfoHelper";
	private final static boolean _DEBUG = Common.DEBUG;
	private final static boolean _SECURITY_DEBUG = Common.SECURITY_DEBUG;
	private final static String DERANGEMENT = "p7zxuhgzq5";
	
	public static DeviceInfo getPolicyDeviceInfo(Context ctx) {
		Builder deviceInfoBuilder = new DeviceInfo.Builder();
		
		deviceInfoBuilder.region(getRegion(ctx));
		deviceInfoBuilder.city(Common.STR_UNKNOWN); // To prevent unnecessary legal problem, fill unknown since policy don't use the field.
		deviceInfoBuilder.time_zone(Utils.getTimeZone());
		deviceInfoBuilder.cid(Common.STR_UNKNOWN); // Abandoned from L50
		deviceInfoBuilder.rom_version(getRomVersion(Common.STR_UNKNOWN));
		deviceInfoBuilder.sense_version(getSenseVersion());
		deviceInfoBuilder.model_id(getModelId(Common.STR_UNKNOWN));
		deviceInfoBuilder.device_id(getHashedSNForCSBI()); //[2016.7.12 Eric Lu]It has been abandoned in M60,and modify to bouncycastle-SHA3 hash SN for CSBI in N70.
		deviceInfoBuilder.device_SN(getSNForGetPolicy()); //set device serial number as device SN for engineering ROM, and set 'unknown' as device SN for shipping ROM to prevent PII concern.
		deviceInfoBuilder.privacy_statement_version(Common.STR_UNKNOWN); // Since policy don't need privacy statement version, just "unknown".

		return deviceInfoBuilder.build();
	}
	
	public static DeviceInfo getLogDeviceInfo(Context ctx, boolean isErrorReport) {
		Builder deviceInfoBuilder = new DeviceInfo.Builder();
		UPolicy policy = new UPolicy(Common.STR_HEADER_APPID, ctx);
		
		deviceInfoBuilder.region(getRegionByPolicy(ctx, policy));
		deviceInfoBuilder.city(Common.STR_UNKNOWN);// Abandoned from M60. Location permission is dangerous level and need user's approval
		deviceInfoBuilder.time_zone(getTimeZoneByPolicy(ctx, policy));
		deviceInfoBuilder.cid(Common.STR_UNKNOWN);// Abandoned from L50. For framework purify
		deviceInfoBuilder.rom_version(getRomVersionByPolicy(ctx, policy));
		deviceInfoBuilder.sense_version(getSenseVersionByPolicy(ctx, policy));
		deviceInfoBuilder.model_id(getModelIdByPolicy(ctx, policy));
		deviceInfoBuilder.device_id(getHashedSNForCSBI());//[2016.7.12 Eric Lu]It has been abandoned in L50, M60, and modify to bouncycastle-SHA3 hash SN for CSBI in N70.
		deviceInfoBuilder.device_SN(getFilteredDeviceSNByPolicy(ctx, policy));
		deviceInfoBuilder.privacy_statement_version(Common.STR_UNKNOWN);// Abandoned from sense70. Provide privacy statement on web page from sense70

		return deviceInfoBuilder.build();		
	}
	
	// begin-region ==
	public static String getRegion(Context ctx) {
		TelephonyManager telmgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		String country = telmgr.getNetworkCountryIso();
		if(TextUtils.isEmpty(country))
			country = Common.STR_UNKNOWN;
		return country;
	}
	
	public static String getRegionByPolicy(Context ctx, final UPolicy policy) {
		if(Common.STR_VALUE_ENABLED.equals(policy.getValue(Common.STR_CATEGORY_REGION, Common.STR_KEY_ENABLE)))
			return getRegion(ctx);
		else
			return Common.STR_DISABLED;
	}
	// end-region ==
    
    // begin-timezone ==
	private static int getTimeZoneByPolicy(Context ctx, UPolicy policy) {
		if(Common.STR_VALUE_ENABLED.equals(policy.getValue(Common.STR_CATEGORY_TIMEZONE, Common.STR_KEY_ENABLE)))
			return Utils.getTimeZone();
		else
			return Integer.MIN_VALUE;		
	}
	// end-timezone ==
	
	// begin-rom-version ==
	public static String getRomVersion(String defaultValue) {
		return ReflectionUtil.get("ro.build.description", defaultValue);
	}

	private static String getRomVersionByPolicy(Context ctx, UPolicy policy) {
		if(Common.STR_VALUE_ENABLED.equals(policy.getValue(Common.STR_CATEGORY_ROM_VERSION, Common.STR_KEY_ENABLE)))
			return getRomVersion(Common.STR_UNKNOWN);
		else
			return Common.STR_DISABLED;
	}
	// end-vom-version ==
	
	// begin-sense-version ==
	private static String getSenseVersion() {
		return Utils.getSenseVersionByCustomizationManager();
	}
	
	private static String getSenseVersionByPolicy(Context ctx, UPolicy policy) {
		if(Common.STR_VALUE_ENABLED.equals(policy.getValue(Common.STR_CATEGORY_SENSE_VERSION, Common.STR_KEY_ENABLE)))
			return getSenseVersion();
		else
			return Common.STR_DISABLED;
	}
	// end-sense-version ==
	
	// begin-model-id ==
	private static String getModelId(String defaultValue) {
		String brand = Build.BRAND;
        String model;
		if(Utils.isFactoryRom() || ReportConfig.isShippingRom())
			//If it's commercial rom, then use ro.aa.project to get ModelId
			model = ReflectionUtil.get("ro.aa.project", Common.STR_UNKNOWN);
		else 
			//It's debugging rom, so we use "debug_"+ro.product.device
			model = "debug_"+ReflectionUtil.get("ro.product.device", Common.STR_UNKNOWN);
        
        if (_DEBUG)
            Log.d(TAG, "Model ID: " + brand + ";" + model);
		return brand + ";" + model;
	}
	
	private static String getModelIdByPolicy(Context ctx, UPolicy policy) {
		if(Common.STR_VALUE_ENABLED.equals(policy.getValue(Common.STR_CATEGORY_MODEL_ID, Common.STR_KEY_ENABLE)))
			return getModelId(Common.STR_UNKNOWN);
		else
			return Common.STR_DISABLED;
	}
	// end-model-id ==
	
	// begin-device-sn ==
	/**
	 * For engineering ROM, this function returns device serial number. <br>
	 * For shipping ROM, this function returns 'unknown'.
	 * @param ctx
	 * @return serial number for engineering ROM, or 'unknown' for shipping ROM.
	 */
	private static String getSNForGetPolicy() {
	    if (ReportConfig.isShippingRom()) 
	        return Common.STR_UNKNOWN;
	    else
	        return getDeviceSN();
	}
	
	private static String getFilteredDeviceSNByPolicy(Context ctx, UPolicy policy) {
		if(Common.STR_VALUE_ENABLED.equals(policy.getValue(Common.STR_CATEGORY_DEVICE_SN, Common.STR_KEY_ENABLE))) {
			if(isHackerDevice())
				return HACKER_SN; // use constant words "HackerDevice" 		
			else
				if(Utils.isFactoryRom() || ReportConfig.isShippingRom())
					return getNewHashedID(ctx); //Rex, Use hashed string of connecting android ID and hexadecimal string of random byte with a dot, 2013/07/03
				else
					return getDeviceSN(); // Use Device SN with plain text format
		}
		else {
			return Common.STR_DISABLED;
		}
	}
	
	private static String getDeviceSN() {
        return TextUtils.isEmpty(Build.SERIAL) ? Common.STR_UNKNOWN : Build.SERIAL;
    }
	// end-device-sn ==
	
	//Begin the filtering for hacker's device
	// The serial number 
	private final static String HACKER_SN = "HackerDevice";
	private final static int SMALL_A = 'a' & 0X00FF; 
	private final static int SMALL_Z = 'z' & 0X00FF;
	private final static int BIG_A = 'A' & 0X00FF;
	private final static int BIG_Z = 'Z' & 0X00FF;
	private final static int ZERO = '0' & 0X00FF;
	private final static int NINE = '9' & 0X00FF;
	 
	public static boolean isHackerDevice() {
	    String sn = getDeviceSN();	     
	    if((sn == null) || (sn.length() == 0))    return true;
	    for(int i = 0; i < sn.length(); i++)    {
	        int sting = sn.charAt(i) & 0X0FFFF;
	        
	        if(((sting >= SMALL_A) && (sting <= SMALL_Z)) ||
	           ((sting >= BIG_A) && (sting <= BIG_Z)) ||
	           ((sting >= ZERO) && (sting <= NINE)))
	            continue;
	        
	        return true;
	    }
	    
	    return false;		 
	}
	//end of hacker device's filtering
	
	/**
	 * Use SHA-256 hash algorithm to generate new hashed ID of device.<br>
	 * Rex, 2013/07/03
	 * @param ctx
	 * @param androidID
	 * @return Hex string of new hashed ID or 'unknown' if exception occurs.
	 */
	private static String getNewHashedID(Context ctx) {
		String sn = getDeviceSN();
		String input = String.format("%s.%s.%s", Build.BRAND, sn, DERANGEMENT);
		if (_SECURITY_DEBUG) Log.d(TAG, "[getNewHashedID] ID: " + input);
		String result = Common.STR_UNKNOWN;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] data = input.getBytes();
			md.update(data);
			byte[] hashedBytes = md.digest();
			result = Utils.byteArrayToHexString(hashedBytes);
			if (_SECURITY_DEBUG) Log.d(TAG, "[getNewHashedID] New hashed ID: " + result);
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "[getNewHashedID] No such hashed algorithm: " + e.getMessage());
		}
		return result;
	}

	/***
	 * Use 3rd-party bouncycastle SHA3 algorithm to hash SN for CSBI.<br>
	 * Eric Lu, 2016/07/12
	 * @param DeviceSN
	 * @return If exist DeviceSN, return hashed DeviceSN, or return empty string.
	 */
	private static String getHashedSNForCSBI() {
		try {
			if (!TextUtils.isEmpty(Build.SERIAL)) {
				DigestSHA3 sha3 = new Digest224();
				sha3.update(Build.SERIAL.getBytes("UTF-8"));
				return Utils.byteArrayToHexString(sha3.digest());
			}
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "[getHashedSNForCSBI] Unsupported Encoding Exception: " + e.getMessage());
		} catch (Exception e) {
			Log.e(TAG, "[getHashedSNForCSBI] Fail to get hashed SN: " + e.getMessage());
		}
		return "";
	}

}
