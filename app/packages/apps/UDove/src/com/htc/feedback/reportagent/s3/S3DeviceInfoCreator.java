package com.htc.feedback.reportagent.s3;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.pbdata.DeviceInfoHelper;
import com.htc.feedback.reportagent.util.DeviceInfoPreference;
import com.htc.feedback.reportagent.util.EngineeringPreference;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.util.PackageInfoUtil;
import com.htc.feedback.reportagent.util.ReflectionUtil;
import com.htc.feedback.reportagent.util.ReportConfig;
import com.htc.feedback.reportagent.util.SignatureUtil;
import com.htc.feedback.reportagent.util.Utils;
import com.htc.lib2.weather.WeatherLocation;
import com.htc.lib2.weather.WeatherUtility;

public class S3DeviceInfoCreator {
	
	private static final String TAG = "S3DeviceInfoCreator";
	private static final int FOTA_UPGRADE_SEND = 3; //rex, Uploading triggered by FOTA upgrade. 2012/1/18
	private Context mContext;
	private Properties mProperties;
	private static S3DeviceInfoCreator mS3DeviceInfoCreator; 
	
	//===== member functions =====
	private S3DeviceInfoCreator(Context ctx) {
		mContext = ctx;
	}
	
	public Properties createDeviceInfoProperties (String tag, String message, String position, String uniqueMessage, int type, String packageName, String processName) {
		mProperties = new Properties();
		TelephonyManager telmgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		String sn = Utils.getSN();
		if(type == FOTA_UPGRADE_SEND) {
			Log.d(TAG, "Send UP/UB log, triggered by FOTA first boot-up. Get device info from share preference.");
			mProperties.setProperty("rom", DeviceInfoPreference.getDeviceInfoString(mContext, DeviceInfoPreference.ROM_VERSION, Common.STR_UNKNOWN));
			mProperties.setProperty("changelist", DeviceInfoPreference.getDeviceInfoString(mContext, DeviceInfoPreference.CHANGE_LIST, "-1"));
			mProperties.setProperty("aid", DeviceInfoPreference.getDeviceInfoString(mContext, DeviceInfoPreference.ANDRIOD_ID, Common.STR_UNKNOWN));
			mProperties.setProperty("fingerprint", DeviceInfoPreference.getDeviceInfoString(mContext, DeviceInfoPreference.FINGERPRINT, Common.STR_UNKNOWN));
			mProperties.setProperty("radio", DeviceInfoPreference.getDeviceInfoString(mContext, DeviceInfoPreference.RADIO, Common.STR_UNKNOWN));
			mProperties.setProperty("revision", DeviceInfoPreference.getDeviceInfoString(mContext, DeviceInfoPreference.REVISION, Common.STR_UNKNOWN));
			mProperties.setProperty("buildtype", DeviceInfoPreference.getDeviceInfoString(mContext, DeviceInfoPreference.BUILD_TYPE, Common.STR_UNKNOWN));
			mProperties.setProperty("frameworkversion", DeviceInfoPreference.getDeviceInfoString(mContext, DeviceInfoPreference.FRAMEWORK_VERSION, Common.STR_UNKNOWN));
			mProperties.setProperty("buildidentify", DeviceInfoPreference.getDeviceInfoString(mContext, DeviceInfoPreference.BUILD_IDENTIFY, Common.STR_UNKNOWN));
			mProperties.setProperty("keyset", DeviceInfoPreference.getDeviceInfoString(mContext, DeviceInfoPreference.KEY_SET, Common.STR_UNKNOWN));
			mProperties.setProperty("sense_version", DeviceInfoPreference.getDeviceInfoString(mContext, DeviceInfoPreference.SENSE_VERSION, Common.STR_UNKNOWN));
			mProperties.setProperty("builddateid", DeviceInfoPreference.getDeviceInfoString(mContext, DeviceInfoPreference.BUILD_DATE_ID, Common.STR_UNKNOWN));
			mProperties.setProperty("unlocked_device", DeviceInfoPreference.getDeviceInfoString(mContext, DeviceInfoPreference.UNLOCKED_DEVICE, Common.STR_UNKNOWN));
			mProperties.setProperty("report_type", DeviceInfoPreference.getDeviceInfoString(mContext, DeviceInfoPreference.REPORT_TYPE, Common.STR_UNKNOWN));
			mProperties.setProperty("schk1", DeviceInfoPreference.getDeviceInfoString(mContext, DeviceInfoPreference.SCHK1, Common.STR_UNKNOWN));
			mProperties.setProperty("schk2", DeviceInfoPreference.getDeviceInfoString(mContext, DeviceInfoPreference.SCHK2, Common.STR_UNKNOWN));
			if(DeviceInfoPreference.checkNeedToUpdate(mContext, tag))
				updateDeviceInfoPreference(mContext);
			
		} else {
			mProperties.setProperty("rom", getRomVersion());
			mProperties.setProperty("changelist", getChangeList());
			mProperties.setProperty("aid", getAndroidId(mContext));
			mProperties.setProperty("fingerprint", getFingerprint());
			mProperties.setProperty("radio", getRadio());
			mProperties.setProperty("revision",getRevision()); //Hardware revision 1=XA, 2=XB
			mProperties.setProperty("buildtype", getBuildType());
			mProperties.setProperty("frameworkversion", getFrameworkVersion()); //framework version, froyo = 2.2
			mProperties.setProperty("buildidentify", getBuildIdentify()); //taskID on AA, project trunk on Dashboard
			mProperties.setProperty("ro.build.buildline", getBuildLine());
			mProperties.setProperty("keyset", getKeySet()); //security key (test-keys or release-keys)
			mProperties.setProperty("sense_version", getSenseVersion());
			mProperties.setProperty("builddateid", getBuildDateId());
			mProperties.setProperty("unlocked_device", getUnlockedDevice());
			mProperties.setProperty("report_type", Utils.getReportType()); // 2011-06-06-pitt, 'com' or 'eng' considered from values of ro.build.type, ro.aa.report and ro.sf
			mProperties.setProperty("schk1", getSchk1()); //2012-11-12-rex_huang, for leak investigation
			mProperties.setProperty("schk2", getSchk2()); //2012-11-16-rex_huang, for leak investigation
		}
		
		mProperties.setProperty("tag", tag.trim()); //can be consider as the log type		
		mProperties.setProperty("message", message.trim()); //the message from the log collector
		mProperties.setProperty("project", ReflectionUtil.get("ro.product.device", "unknown").trim());
		mProperties.setProperty("model", ReflectionUtil.get("ro.product.model", "unknown").trim());
		mProperties.setProperty("product", ReflectionUtil.get("ro.product.name", "unknown").trim());
		mProperties.setProperty("serialno", sn.trim());
		mProperties.setProperty("deviceid", validTelephonyId(telmgr.getDeviceId()).trim());
		mProperties.setProperty("phonetype", String.valueOf(telmgr.getPhoneType()).trim());
		mProperties.setProperty("time", String.valueOf(System.currentTimeMillis()).trim());
		mProperties.setProperty("position", position.trim());
		mProperties.setProperty("free_data_storage", Long.toString(Utils.getFreeSize(Common.STR_DATA_PATH))); // 2011-12-02-pitt, Free storage size of data partition with default value -1
		mProperties.setProperty("unique_msg", TextUtils.isEmpty(uniqueMessage) ? Common.STR_UNKNOWN : uniqueMessage); //2012-01-03-pitt, unique_msg is a ID for linking logs in backend for kernel crash.
		mProperties.setProperty("timezone", Integer.toString(Utils.getTimeZone())); //2013-11-07-rex, add offset of TimeZone, requested by U32
		if (!ReportConfig.isShippingRom()) {
            if (Common.STR_HTC_UB.equals(tag)) {
                // Get all account info for UB
                ArrayList<String> accountList = getAllAccountsWithJSONObjectStyle(mContext);
                mProperties.setProperty("account", accountList.get(0));
                mProperties.setProperty("apaccount", accountList.get(1));
            } else {
                // Only get HTC mail account for error report for prevent RD accessing external user trial user's PII,
                mProperties.setProperty("account", getMailAccountsWithJSONArrayStyle(mContext));
                mProperties.setProperty("apaccount", Common.STR_UNKNOWN); // Don't collect app account and set as unknown for backend server compatibility.
            }
            mProperties.setProperty("city", getCity(mContext, Common.STR_UNKNOWN));
		}
		if (isUPOrUB(tag))
			setLogId(mContext, mProperties, tag); //Rex, New field for tracing UP/UB upload quantity, 2013/02/21
		
		// begin-added-for-recognizing-updated-hms-from-playstore-2014-01-16
		if(!TextUtils.isEmpty(packageName)) {
    		try{ mProperties.setProperty("updatedSystemApp", Boolean.toString(PackageInfoUtil.isUpdatedSystemApp(mContext, packageName))); } catch(Exception e) {Log.e(TAG, "[updatedSystemApp]", e);}
    		try{ mProperties.setProperty("isSystemApp", Boolean.toString(PackageInfoUtil.isSystemApp(mContext, packageName))); } catch(Exception e) {Log.e(TAG, "[isSystemApp]", e);}
    		try{ mProperties.setProperty("installerPackageName", PackageInfoUtil.getInstallerPackageName(mContext, packageName, Common.STR_UNKNOWN)); } catch(Exception e) {Log.e(TAG, "[installerPackageName]", e);}
    		try{ mProperties.setProperty("signedKey", SignatureUtil.getInstance().getSignatureName(mContext, packageName, Common.STR_UNKNOWN)); } catch(Exception e) {Log.e(TAG, "[signedKey]", e);}
    		try{ mProperties.setProperty("isSignedHtcKey", Boolean.toString(SignatureUtil.getInstance().isSignedHtcKey(mContext, packageName))); } catch(Exception e) {Log.e(TAG, "[isSignedHtcKey]", e);}
    		try{ mProperties.setProperty("packageName", !TextUtils.isEmpty(packageName) ? packageName : Common.STR_UNKNOWN); } catch(Exception e) {Log.e(TAG, "[packageName]", e);}
    		try{ mProperties.setProperty("packageVersionName", PackageInfoUtil.getVersionName(mContext, packageName, Common.STR_UNKNOWN)); } catch(Exception e) {Log.e(TAG, "[packageVersionName]", e);}
    		try{ mProperties.setProperty("packageVersionCode", Integer.toString(PackageInfoUtil.getVersionCode(mContext, packageName, -1))); } catch(Exception e) {Log.e(TAG, "[packageVersionCode]", e);}
		}
		if(!TextUtils.isEmpty(processName))
		    try{ mProperties.setProperty("processName", !TextUtils.isEmpty(processName) ? processName : Common.STR_UNKNOWN); } catch(Exception e) {Log.e(TAG, "[processName]", e);}
        // end-added-for-recognizing-updated-hms-from-playstore-2014-01-16
		
		return mProperties;
	}	
	
	//===== static functions =====
	public static S3DeviceInfoCreator getInstance(Context ctx) {
		if (mS3DeviceInfoCreator == null)
			mS3DeviceInfoCreator = new S3DeviceInfoCreator(ctx);
		return mS3DeviceInfoCreator;
	}
	
	public static void updateDeviceInfoPreference(Context context){
		/*
		 * setSentinelValue() is implemented for error handling.
		 * it can prevent device info was update incompletely.
		 * Rex, 2013/01/17
		 */
		DeviceInfoPreference.setSentinelValue(context, false);
    	DeviceInfoPreference.setDeviceInfoString(context, DeviceInfoPreference.ROM_VERSION, getRomVersion());
    	DeviceInfoPreference.setDeviceInfoString(context, DeviceInfoPreference.CHANGE_LIST, getChangeList());
    	DeviceInfoPreference.setDeviceInfoString(context, DeviceInfoPreference.ANDRIOD_ID, getAndroidId(context));
    	DeviceInfoPreference.setDeviceInfoString(context, DeviceInfoPreference.FINGERPRINT, getFingerprint());
    	DeviceInfoPreference.setDeviceInfoString(context, DeviceInfoPreference.RADIO, getRadio());
    	DeviceInfoPreference.setDeviceInfoString(context, DeviceInfoPreference.REVISION, getRevision());
    	DeviceInfoPreference.setDeviceInfoString(context, DeviceInfoPreference.BUILD_TYPE, getBuildType());
    	DeviceInfoPreference.setDeviceInfoString(context, DeviceInfoPreference.FRAMEWORK_VERSION, getFrameworkVersion());
    	DeviceInfoPreference.setDeviceInfoString(context, DeviceInfoPreference.BUILD_IDENTIFY, getBuildIdentify());
    	DeviceInfoPreference.setDeviceInfoString(context, DeviceInfoPreference.BUILD_LINE, getBuildLine());
    	DeviceInfoPreference.setDeviceInfoString(context, DeviceInfoPreference.KEY_SET, getKeySet());
    	DeviceInfoPreference.setDeviceInfoString(context, DeviceInfoPreference.SENSE_VERSION, getSenseVersion());
    	DeviceInfoPreference.setDeviceInfoString(context, DeviceInfoPreference.BUILD_DATE_ID, getBuildDateId());
    	DeviceInfoPreference.setDeviceInfoString(context, DeviceInfoPreference.UNLOCKED_DEVICE, getUnlockedDevice());
    	DeviceInfoPreference.setDeviceInfoString(context, DeviceInfoPreference.REPORT_TYPE, Utils.getReportType());
    	DeviceInfoPreference.setDeviceInfoString(context, DeviceInfoPreference.SCHK1, getSchk1());
    	DeviceInfoPreference.setDeviceInfoString(context, DeviceInfoPreference.SCHK2, getSchk2());
    	DeviceInfoPreference.setSentinelValue(context, true);
    }
	
	/**
	 * @return the device's assigned Android ID value, or 0 if unknown.
	 */
	private static String getAndroidId(Context ctx) {
        String aid = Settings.Secure.getString(ctx.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return (aid == null) ? "unknown" : aid.trim();
	}
	
	/**
	 * @return unknown if Telephony id is null.
	 */
	private String validTelephonyId(String value) {
        return (value == null) ? "unknown" : value;
	}

    /**
     * This method return mail accounts
     * 
     * @return String, mail accounts
     */
    private static String getMailAccountsWithJSONArrayStyle(Context ctx) {

        String mailAccountStr = Common.STR_UNKNOWN;
        JSONArray jsonArray_mail = new JSONArray();

        try {
            AccountManager accountMgr = AccountManager.get(ctx);
            if (accountMgr != null) {
                Account[] accounts = accountMgr.getAccountsByType(null);
                if (accounts != null) {
                    String accountType = "";
                    for (Account account : accounts) {
                        if (account != null) {
                            accountType = account.type;
                            if ("com.htc.android.mail.eas".equals(accountType) || "com.google".equals(accountType)) {
                                String accountName = account.name;
                                // [2016/1/15][sy_wang]
                                // 1. Only collect HTC mail account, xxx@htc.com, for knowing HTC internal device user.
                                // 2. For avoiding issue false alarm, collect tellhtcxx@gmail.com & tellhtcxx@hotmail.com, xx is number.
                                //    Currently error log with tellhtc01@gmail.com or tellhtc01@hotmail.com won't be assigned as issue by Skynet.
                                if (accountName.endsWith("@htc.com") || Pattern.matches("tellhtc[0-9]{2}@(gmail\\.com||hotmail\\.com)", accountName)) {
                                    jsonArray_mail.put(accountName);
                                }
                            }
                        }
                    }
                }
            }

            if (jsonArray_mail.length() > 0) {
                mailAccountStr = jsonArray_mail.toString();
            }

        } catch (Exception e) {
            Log.e(TAG, "getAccountsWithJSONArrayStyle", e);
        }
        return mailAccountStr;
    }

    /**
     * This method return mail accounts and AP accounts.
     * 
     * @return ArrayList, the first is mail accounts, the second is AP accounts.
     */ 
    private static ArrayList<String> getAllAccountsWithJSONObjectStyle(Context ctx) {

        ArrayList<String> accountList = new ArrayList<String>();
        JSONArray jsonArray_mail = new JSONArray();
        JSONObject jsonObject_ap = new JSONObject();

        try {
            AccountManager accountMgr = AccountManager.get(ctx);
            if (accountMgr != null) {
                Account[] accounts = accountMgr.getAccountsByType(null);
                if (accounts != null) {
                    String accountType = "";
                    for (Account account : accounts) {
                        if (account != null) {
                            accountType = account.type;
                            if ("com.htc.android.mail.eas".equals(accountType) || "com.google".equals(accountType)) {
                                jsonArray_mail.put(account.name);
                            } else if (!isExcludedAccounts(account.type)) {
                                jsonObject_ap.put(accountType, account.name);
                            }
                        }
                    }
                }
            }

        String mailAccountStr = null, apAccountStr = null;
        if (jsonArray_mail.length() <= 0)
            mailAccountStr = Common.STR_UNKNOWN;
        else {
            mailAccountStr = jsonArray_mail.toString();
        }
        if (jsonObject_ap.length() <= 0)
            apAccountStr = Common.STR_UNKNOWN;
        else {
            apAccountStr = jsonObject_ap.toString();
        }
        accountList.add(mailAccountStr);
        accountList.add(apAccountStr);
        
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
        } catch (Exception e) {
            Log.e(TAG, "getAccountsWithJSONObjectStyle", e);
        }
        return accountList;
    }
    
    private static boolean isExcludedAccounts(String accountType) {
		return  "com.htc.showme".equals(accountType) ||
				"com.htc.sync.provider.weather".equals(accountType) || 
				"com.htc.android.Stock".equals(accountType) ||
				"com.htc.stock".equals(accountType) ||
				"com.htc.newsreader".equals(accountType);
	}

	private static String getCity(Context context, String defaultCity) {
		String city = defaultCity; 
		if(context != null) {
			String tmpCity = null;
			WeatherLocation [] weatherLocations = WeatherUtility.loadLocations(context.getContentResolver(), "com.htc.htclocationservice");
			if( weatherLocations != null && weatherLocations.length > 0 && weatherLocations[0] != null) {
				tmpCity = weatherLocations[0].getName();
			}
			if (!TextUtils.isEmpty(tmpCity)) {
				city = tmpCity;
			}
		}
		return city;
	}

    private static String getRomVersion(){
    	return ReflectionUtil.get("ro.build.description", "unknown").trim();
    }
    
    private static String getChangeList(){
    	return ReflectionUtil.get("ro.build.changelist", "-1").trim();
    }
    
    private static String getFingerprint(){
    	return Build.FINGERPRINT.trim();
    }
    
    private static String getRadio(){
    	return ReflectionUtil.get("gsm.version.baseband","unknown").trim();
    }
    
    private static String getRevision(){
    	return ReflectionUtil.get("ro.revision", "unknown").trim();
    }
    
    private static String getBuildType(){
    	return ReflectionUtil.get("ro.build.type", "unknown").trim();
    }
    
    private static String getFrameworkVersion(){
    	return ReflectionUtil.get("ro.build.version.release", "unknown").trim();
    }
    
    private static String getBuildIdentify(){
    	return ReflectionUtil.get("ro.build.project", "unknown").trim();
    }
    
    private static String getBuildLine(){
    	return ReflectionUtil.get("ro.build.buildline", "unknown").trim();
    }
    
    private static String getKeySet(){
    	return ReflectionUtil.get("ro.build.tags", "unknown").trim();
    }
    
    private static String getSenseVersion(){
    	return Utils.getSenseVersionByCustomizationManager();
    }
    
    private static String getBuildDateId(){
    	return ReflectionUtil.get("ro.build.date.utc", "unknown").trim();
    }
    
    private static String getUnlockedDevice(){
    	return ReflectionUtil.get("ro.lb","unknown").trim();
    }
    
    private static String getSchk1(){
    	return ReflectionUtil.get("ro.ur", "unknown");
    }
    
    private static String getSchk2(){
    	return ReflectionUtil.get("ro.di", "unknown");
    }
	
    private static boolean isUPOrUB(String tag) {
		if(Common.DEBUG) Log.d(TAG, "[isUPOrUB]tag: "+tag);
		return "HTC_UP".equals(tag) || "HTC_UB".equals(tag);
	}
    
    private static void setLogId(Context context, Properties propReportData, String tag){
    	int logID = EngineeringPreference.getLogID(context, tag);	
    	if(logID < Integer.MAX_VALUE)
    		logID++;
    	else
    		logID = 1;	
		if(logID >= 1){
			propReportData.setProperty("logid", String.valueOf(logID));
			EngineeringPreference.setLogID(context, tag, logID);
		}else
			Log.d(TAG, "tag: " + tag + " log ID is less than 1!");
    }

}
