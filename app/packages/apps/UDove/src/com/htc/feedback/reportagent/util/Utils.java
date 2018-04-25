package com.htc.feedback.reportagent.util;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import android.app.ApplicationErrorReport;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Parcelable;
import android.os.StatFs;

import com.htc.feedback.reportagent.util.ReflectionUtil;
import android.provider.Settings;
import com.htc.feedback.policy.UPolicy;
import com.htc.feedback.reportagent.Common;

import android.text.TextUtils;

import com.htc.feedback.policy.ReportPolicy;
import com.htc.feedback.reportagent.util.Log;
import com.htc.lib0.customization.HtcWrapCustomizationManager;
import com.htc.lib0.customization.HtcWrapCustomizationReader;

public final class Utils {
	
	private final static String TAG = "Utils";
	private final static boolean _DEBUG = Common.DEBUG;
	private final static int FILE_SIZE = 100 * 1024;
	private static HtcWrapCustomizationManager mCustomizeManager = null;
	private static String SENSE_VERSION = "5.5";
    
    public static int getRetryTimes(Context ctx) {
    	
    	UPolicy policy = ReportPolicy.getInstance(ctx);
		String retryStr = policy.getValue(Common.CATEGORY_COMMON, Common.KEY_COMMON_RETRY);
		int retry = 1;
		
		if(!TextUtils.isEmpty(retryStr)) {
			try {
				int retryValue = Integer.parseInt(retryStr);
				retry = retryValue > 3 ? 3 : retryValue < 0 ? 0 : retryValue;
			} catch (Exception e) {
				Log.e(TAG, "Exception happen during paring RetryTimes");
				e.printStackTrace();
			}
		}
		if (_DEBUG) Log.i(TAG, "getRetryTimes()", "retry string in policy: "+retryStr+", adjusted retry is " + retry);
		
		return retry;
    }
    
    
    public static int getUploadLogFreq(Context ctx) {
    	final int DEFAULT_FREQ_OF_USER_ROM = 24; // hours
    	final int DEFAULT_FREQ_OF_DEBUG_ROM = 6; // hours
    	int nFreq = 0;
    	UPolicy policy = ReportPolicy.getInstance(ctx);
    	String strFreq = policy.getValue(Common.CATEGORY_LOG,Common.KEY_LOG_FREQ);

    	if(!TextUtils.isEmpty(strFreq)) {
	    	try{
	    		nFreq = Integer.parseInt(strFreq);
	    	} catch (NumberFormatException e) {
	    		e.printStackTrace();
	    	}
    	}
    	
    	// if no frequency, use default 24 hours instead.
    	if(nFreq <= 0) 
    		nFreq = DEFAULT_FREQ_OF_USER_ROM;
    	
    	// if debugging rom, use default 6 hours.
    	if(nFreq > DEFAULT_FREQ_OF_DEBUG_ROM && isDebuggingPolicyEnabled()) {
    		nFreq = DEFAULT_FREQ_OF_DEBUG_ROM;
    		if (_DEBUG) Log.d(TAG,"[getUploadLogFreq] debugging rom without system properties, frequency is "+nFreq);
    	}
    	
    	if (_DEBUG) Log.d(TAG,"[getUploadLogFreq] policy freq : "+strFreq+", returned freq : "+nFreq);
    	return nFreq * 3600 * 1000;
    }

    public static boolean isPolicyEnabled(Context context) {	
        if(!ReportConfig.isShippingRom()) {
            if(_DEBUG) Log.v(TAG,"[isPolicyEnabled(engineering)] force enabled");
            return true;  // Force enabling policy channel because up is forced enabled
        }
        
		if(isErrorReportSettingEnabled(context) || isUserProfilingSettingEnabled(context)){
			if(_DEBUG) Log.v(TAG,"[isPolicyEnabled(shipping)] enabled");
			return true;
		}
		else
			return false;
    }
	
    public static boolean isErrorReportSettingEnabled(Context context) {
    	return getTellHTCUI(context) == 1 && getErrorReportCheckBox(context) == 1;
    }
    
    public static boolean isUserProfilingSettingEnabled(Context context) {
    	return getTellHTCUI(context) == 1 && getUserProfilingCheckBox(context) == 1;
    }
    
	public static int getTellHTCUI(Context context) {
        return Settings.Secure.getInt( context.getContentResolver(), Common.HTC_ERROR_REPORT_SETTING, 0 );
	}   
	
	public static int getErrorReportCheckBox(Context context) {
        return Settings.Secure.getInt( context.getContentResolver(), Common.SEND_HTC_ERROR_REPORT, 0 );
	}   
	
	public static int getUserProfilingCheckBox(Context context) {
        return Settings.Secure.getInt( context.getContentResolver(), Common.SEND_HTC_APPLICATION_LOG, 0 );
	}
	
	public static int getPreferNetwork(Context context) {
        return Settings.Secure.getInt( context.getContentResolver(), Common.HTC_ERROR_REPORT_PREFER_NETWORK, 0 );
	}
	
	public static int getUploadPreference(Context context) {
		return Settings.Secure.getInt(context.getContentResolver(), Common.HTC_ERROR_REPORT_AUTO_SEND, 0);
	}
	
	public static boolean getForceDisableULog() {
        return ReflectionUtil.getBoolean("profiler.force_disable_ulog", false);
    }
	
	/**
	 * UP and UB have to be uploaded with all network regardless of user's choice on settings since sense 4.0
	 * @param context
	 * @param isUPOrUB Except UP or UB logs, other caller such PolicyManager and UpdateManager should set the parameter as false
	 *                 If true, it would return true in most cases in engineering ROM and follow settings to decide returned value
	 *                 in commercial ROM.
	 * @return
	 */
	
	public static boolean isNetworkAllowed(Context context) {
		int preferNetwork = Utils.getPreferNetwork(context);
		boolean isMobileAcceptable = ( 0 == preferNetwork ) ? true : false;
		byte type = isMobileAcceptable ? N_TYPE_ALL_NETWORK_WITHOUT_2G : N_TYPE_USBNET_AND_WIFI; 
		return isNetworkAllowed(context, type);			
	}
	
	/**
	 * 
	 * @param context
	 * @param tag
	 * @param romDesc
	 * @param fileSize size of file with unit KBytes
	 * @return
	 */
	public static boolean isNetworkAllowed(Context context, String tag, String romDesc, long fileSize) {
		boolean isDebuggingRom = !ReportConfig.isShippingRom();
		boolean isMobileAcceptableByUser = ( 0 == Utils.getPreferNetwork(context) ) ? true : false;
		byte acceptedNetworkTypes = 0;
		
		if(_DEBUG) Log.d("[isNetworkAllowed] tag: "+tag+", rom desc: "+romDesc+", file size: "+fileSize+
							", isDebuggingRom: "+isDebuggingRom+", isMobileAcceptableByUser: "+isMobileAcceptableByUser);		
		if(isDebuggingRom && isMobileAcceptableByUser && isUPOrUBOrUserTrialFeedback(tag) && 
				fileSize <= FILE_SIZE && Security.isRom999(romDesc)) {
			// UP/UB/USER_TRIAL_FEEDBACK have to be uploaded with all network(with 2G) if all network option is checked by users 
			acceptedNetworkTypes = N_TYPE_ALL_NETWORK_WITH_2G;
			if(_DEBUG) Log.d("[isNetworkAllowed][engineering][999] UP/UB/UserTrialFeedback 'with' all network option");
		}
		else if(isDebuggingRom && isUPOrUB(tag)) {
			// UP and UB have to be uploaded with all network(no 2G) regardless of user's choice on settings since sense 4.0
			if(_DEBUG) Log.d("[isNetworkAllowed][engineering] UP/UB case");
			acceptedNetworkTypes = N_TYPE_ALL_NETWORK_WITHOUT_2G;
		}
		else {
			if(_DEBUG) Log.d("[isNetworkAllowed] normal case: "+(isMobileAcceptableByUser ? "ALL_NETWORK_WITHOUT_2G" : "USBNET_AND_WIFI"));
			acceptedNetworkTypes = isMobileAcceptableByUser ? N_TYPE_ALL_NETWORK_WITHOUT_2G : N_TYPE_USBNET_AND_WIFI; 
		}
		
		return isNetworkAllowed(context, acceptedNetworkTypes);
	}
	
	private static boolean isUPOrUBOrUserTrialFeedback(String tag) {
		return Common.STR_HTC_UP.equals(tag) || Common.STR_HTC_UB.equals(tag) || Common.STR_USER_TRIAL_FEEDBACK.equals(tag);
	}
	
	private static boolean isUPOrUB(String tag) {
		return Common.STR_HTC_UP.equals(tag) || Common.STR_HTC_UB.equals(tag);
	}
	
	private final static byte N_MASK_USBNET = (byte) 0x80B;
	private final static byte N_MASK_WIFI = 0x40;
	private final static byte N_MASK_OTHERS = 0x20;
	private final static byte N_MASK_2G = 0x10;
	private final static byte N_TYPE_ALL_NETWORK_WITH_2G = N_MASK_USBNET | N_MASK_WIFI | N_MASK_OTHERS | N_MASK_2G;
	private final static byte N_TYPE_ALL_NETWORK_WITHOUT_2G = N_MASK_USBNET | N_MASK_WIFI | N_MASK_OTHERS;
	private final static byte N_TYPE_USBNET_AND_WIFI = N_MASK_USBNET | N_MASK_WIFI;
	/**
	 * This function always return false in roaming mode. For underneath network types, it return true 
	 * if there is any enabled network type in allowedTypes.Currently, 4 types are defined below. The 
	 * sequence is defined the highest bit as the fist one.
	 * [1st bit(0x80)] 0: disabled in USBNET,   1: enabled in USBNET
	 * [2nd bit(0x40)] 0: disabled in wifi,     1: enabled in wifi
	 * [3rd bit(0x20]  0: disabled in others,   1: enabled in others( excpet USBNET, wifi, 2G)
	 * [4th bit(0x10]  0: disabled in 2G,       1: enabled in 2G
	 * 
	 * @param acceptedTypes
	 * @return
	 */
	private static boolean isNetworkAllowed(Context context, byte acceptedTypes){
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo(); 
		if (networkInfo == null){
			Log.d(TAG, "Upload block due to no active network.");
			return false;
		}
		
		dumpNetworkInfo(networkInfo);
		
		// don't upload in roaming network
		if (networkInfo.isRoaming()){
			Log.v(TAG, "Upload block due to roaming.");
			return false;
		}
		
		int type = networkInfo.getType();
		int subType = networkInfo.getSubtype();
		
		// get all status of network types
		// sy_temp
		boolean isUSBNET = type == 55;
//		boolean isUSBNET = type == HtcWrapConnectivityManager.TYPE_USBNET;
		boolean isWifi = type == ConnectivityManager.TYPE_WIFI;
		boolean is2G = (type == ConnectivityManager.TYPE_MOBILE) 
				       && (subType == TelephonyManager.NETWORK_TYPE_EDGE || subType == TelephonyManager.NETWORK_TYPE_GPRS
                             || subType == TelephonyManager.NETWORK_TYPE_1xRTT || subType == TelephonyManager.NETWORK_TYPE_CDMA);
		boolean isOthers = !(isUSBNET || isWifi || is2G);
		
		// get all accepted types
		boolean isUSBNETTypeAccepted = (acceptedTypes & N_MASK_USBNET) == N_MASK_USBNET;
		boolean isTypeWifiAccepted = (acceptedTypes & N_MASK_WIFI) == N_MASK_WIFI;
		boolean isType2GAccepted = (acceptedTypes & N_MASK_2G) == N_MASK_2G;
		boolean isTypeOthersAccepted = (acceptedTypes & N_MASK_OTHERS) == N_MASK_OTHERS;
		
		// [isXXXAllowed] true means user agrees the XXX type network and XXX type network is available. Otherwise, false.
		boolean isUSBNETAllowed = isUSBNET ? isUSBNETTypeAccepted : false ;
		boolean isWifiAllowed = isWifi ? isTypeWifiAccepted : false ;
		boolean is2GAllowed = is2G ? isType2GAccepted : false ;
		boolean isOthersAllowed = isOthers ? isTypeOthersAccepted : false ; 
		
		// the result
		boolean isNetworkAllowed = isUSBNETAllowed || isWifiAllowed || is2GAllowed || isOthersAllowed;
		Log.d(TAG,"isNetworkAllowed: "+isNetworkAllowed+", isUSBNETTypeAccepted: "+isUSBNETTypeAccepted
					+", isTypeWifiAccepted: "+isTypeWifiAccepted+", isType2GAccepted: "+isType2GAccepted+", isTypeOthersAccepted: "+isTypeOthersAccepted
					+", isUSBNETAllowed: "+isUSBNETAllowed+", isWifiAllowed: "+isWifiAllowed+", is2GAllowed: "+is2GAllowed+", isOthersAllowed: "+isOthersAllowed);
		return isNetworkAllowed;
	}
	
	private static void dumpNetworkInfo(NetworkInfo networkInfo) {
        if(networkInfo != null) {
//            if(_DEBUG) Log.v(TAG, "getTypeName():"+networkInfo.getTypeName());
                       Log.v(TAG, "getType(): "+networkInfo.getType()+", getSubtype: "+networkInfo.getSubtype());
//            if(_DEBUG) Log.v(TAG, "isAvailable():"+networkInfo.isAvailable());
//            if(_DEBUG) Log.v(TAG, "isConnected():"+networkInfo.isConnected());
//            if(_DEBUG) Log.v(TAG, "isConnectedOrConnecting():"+networkInfo.isConnectedOrConnecting());
//            if(_DEBUG) Log.v(TAG, "isRoaming():"+networkInfo.isRoaming());
//            if(_DEBUG) Log.v(TAG, "All:"+networkInfo);
        }
	}

	public static int getCurrentNetworkType(Context context) {
		
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile=cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi=cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        // sy_temp
        NetworkInfo usbNet = cm.getNetworkInfo(55);
//        NetworkInfo usbNet = cm.getNetworkInfo(HtcWrapConnectivityManager.TYPE_USBNET);
        boolean mOn = false;
        boolean wOn = false;
        boolean uOn = false;
        if ( null != mobile ) mOn=mobile.isConnected();
        if ( null != wifi ) wOn=wifi.isConnected();
        if ( null != usbNet ) uOn = usbNet.isConnected();
        
        if ( mOn ) return ConnectivityManager.TYPE_MOBILE;
        if ( wOn ) return ConnectivityManager.TYPE_WIFI;
        // sy_temp
        if ( uOn ) return 55;
//        if ( uOn ) return HtcWrapConnectivityManager.TYPE_USBNET;
        return -1;
    }
	
	public static void showAllocateMemory(String tag) {
		Runtime runtime = Runtime.getRuntime();
		long max = runtime.maxMemory();
		long total = runtime.totalMemory();
		long free = runtime.freeMemory();
		if(_DEBUG) Log.d(TAG,tag+" max: " + max + ", total: " + total + ", free:" + free);
	}
	
	public static boolean isS3UploaderEnabled() {
		boolean isDebuggingRom = !(Utils.isFactoryRom() || ReportConfig.isShippingRom());
		return isDebuggingRom;
	}
	
	/**
	 * The newer device allow user to flash boot, system, recovery partition. In such cases, ro.lb=1
	 * This function is based on the doc "Unlock/Relock Bootloader", SSD Eddic Hsien/Domeracki Yang
	 */
	public static boolean isUnlockedDevice() {
		// [ro.lb]: [0], original protected shipping device, or faked property by unlock device
		// [ro.lb]: [1], unlocked device and relocked device
		// [ro.lb]: [unknown], bootloader unlocking feature is not available (old boot loader and new init executable(inside boot image))
		// [ro.lb]: empty, old boot loader and old init executable(inside boot image)
		return "1".equals(ReflectionUtil.get("ro.lb"));
	}
	
	
	/**
	 * The report type can't be considered as the value of ro.aa.report only. Furthermore, it needs to refer to 
	 * the values of ro.build.type, ro.aa.report and ro.sf.  
	 * @return 'com' in shipping ROM; otherwises 'eng'
	 */
	public static String getReportType() {
		return ReportConfig.isShippingRom() ? "com" : "eng";
	}
	
	public static long getFreeSize(String path) {
		StatFs dataFs = new StatFs(path);
		long freeDataSize = -1;
       	freeDataSize = (long) dataFs.getAvailableBlocks() *
        			dataFs.getBlockSize();
       	return freeDataSize;
	}
	
	public static void logDataSizeForWakeLock(String tag, long dataSize){
		try{
			long result = dataSize/1024;
			if(result < 1)
				result = 1;
			Log.d(Common.WAKELOCK_TAG, tag+"."+result+"KB to transmit,reason=send log to server.");
		}catch(ArithmeticException ae){
			ae.printStackTrace();
		}catch(ClassCastException cce){
			cce.printStackTrace();
		}
	}
	
	public static String getSenseVersionByCustomizationManager() {
		if (mCustomizeManager == null)
			mCustomizeManager = new HtcWrapCustomizationManager();
		HtcWrapCustomizationReader reader = mCustomizeManager.getCustomizationReader("system", HtcWrapCustomizationManager.READER_TYPE_XML, false);
		if (reader != null)
			SENSE_VERSION = reader.readString("sense_version", "5.5");
		if(_DEBUG) Log.d(TAG, "[getSenseVersionByCustomizationManager] Sense Version: " + SENSE_VERSION);
		return SENSE_VERSION;
	}
	
	public static int getTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        boolean daylight = tz.inDaylightTime(new Date());
        return tz.getRawOffset() + (daylight ? tz.getDSTSavings() : 0);
    }
	
	/**
	 * Convert byte array to hex string.
	 *
	 * @param b byte array
	 * @return hex string
	 */
	public static String byteArrayToHexString(byte[] b) {
		String hs = "";
	    String stmp = "";
	    for (int n = 0; n < b.length; n ++) {
	    	stmp = Integer.toHexString(b[n] & 0xFF);
	        if (stmp.length() == 1)
	             hs += ("0" + stmp);
	        else
	             hs += stmp;
	    }
	    return hs;
	}
	
	// TODO: these underneath codes should sync with pbdata.isHackerDevice()  
	//Begin the filtering for hacker's device
	// The serial number 
	private final static String hackerSN = "HackerDevice";
	private final static int smallA = 'a' & 0X00FF; 
	private final static int smallZ = 'z' & 0X00FF;
	private final static int bigA = 'A' & 0X00FF;
	private final static int bigZ = 'Z' & 0X00FF;
	private final static int zero = '0' & 0X00FF;
	private final static int nine = '9' & 0X00FF;
	
	public static String getSN()    {
	    String sn = ReflectionUtil.get("ro.serialno","");	     
	    //Log.d(TAG,"The S/N is,"+sn);
	    if((sn == null) || (sn.length() == 0))    return hackerSN;
	    for(int i = 0; i < sn.length(); i++)    {
	        int sting = sn.charAt(i) & 0X0FFFF;
	        
	        if(((sting >= smallA) && (sting <= smallZ)) ||
	           ((sting >= bigA) && (sting <= bigZ)) ||
	           ((sting >= zero) && (sting <= nine)))
	            continue;
	        
	        return hackerSN;
	    }
	    
	    return sn;
	}
	
	public static ApplicationErrorReport getApplicationErrorReport(Intent intent) {
        Parcelable parcelable = intent.getParcelableExtra(Intent.EXTRA_BUG_REPORT);
        if(parcelable != null) {
            if(parcelable instanceof ApplicationErrorReport) {
                ApplicationErrorReport report = (ApplicationErrorReport) parcelable;
                return report;
            }
            else
                Log.d(TAG, "Fail to cast to ApplicationiErrorReport !");
        }
        else
            Log.d(TAG, "ApplicationiErrorReport is null !");
        return null;
	}
	
	// sy_temp, moved from com.htc.utils.ulog.Util, begin
	public static boolean isFactoryRom() {
		int factoryTest = 0;
        String factoryTestStr = ReflectionUtil.get("ro.factorytest");
        try {
        	factoryTest = "".equals(factoryTestStr) ? 0/*SystemServer.FACTORY_TEST_OFF*/ : Integer.parseInt(factoryTestStr);
        } catch (NumberFormatException e){
        	e.printStackTrace();
        }
		return factoryTest != 0;
	}
	
	/**
	 * Putting ulog.enable_debugging_policy=0 into local.prop can disable debugging policies in debugging rom
	 * @return true if there is no ulog.enable_debugging_policy system property in debugging rom
	 *         false if ulog.enable_debugging_policy=0 exists in debugging rom
	 */
	public static boolean isDebuggingPolicyEnabled() {
		boolean isDebuggingRom = !(isFactoryRom() || ReportConfig.isShippingRom());
		boolean isDebuggingLogEnabled = isDebuggingPolicyEnabledBySystemProperty();
		return isDebuggingRom && isDebuggingLogEnabled;
	}
	
	/** {@hide} */
	public static boolean isDebuggingPolicyEnabledBySystemProperty() {
		boolean isDebuggingLogsEnabled = ReflectionUtil.getBoolean("ulog.enable_debugging_policy", true);
		if(_DEBUG) Log.d(TAG, "ulog.enable_debugging_policy: "+isDebuggingLogsEnabled);
		return isDebuggingLogsEnabled;
	}
	// sy_temp, moved from com.htc.utils.ulog.Util, end

}
