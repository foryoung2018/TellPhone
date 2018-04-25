package com.htc.feedback.policy;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.util.ReportConfig;

public class UPolicy {
    private static final String TAG = "UPolicy";
    public static final String KEY_SECOND_SWITCH_OF_ERROR_REPORT = "second_switch_of_error_report";
    private static final boolean IS_DEBUG_ULOG = Common.DEBUG;
    
    public static final String APPID_ERROR_REPORT = ReportConfig.isShippingRom()? "com.htc.feedback" : "com.htc.feedback.debug";
    
    private String mAppId = "";
    private PolicyStore mPolicyStore;
    public UPolicy(String appid, Context context) {
        if(!TextUtils.isEmpty(appid) ) { 
            mAppId = appid;
        } else
            throw new IllegalArgumentException("App ID cannot be null");
        mPolicyStore = PolicyStore.getInstance(context);
    }
    
    /**
     * only get value by category and key without check settings UI
     * @param category
     * @param key
     * @return
     */
    public String getValue(String category, String key){
        if (mPolicyStore.getPolicy() == null)
            return "";
        if(!TextUtils.isEmpty(category)) {
            Bundle appPolicy = mPolicyStore.getPolicy().getBundle(mAppId);
            if(appPolicy != null) {
                Bundle cateBundle = appPolicy.getBundle(category);
                if(cateBundle != null) {
                    Bundle keyBundle = cateBundle.getBundle(key);
                    if(keyBundle != null) {
                        String value = keyBundle.getString(key);
                        
                        // When the policy is expired, use the default value if it has
                        long dueDate = keyBundle.getLong(PolicyStore.KEY_DUE_DATE);
                        String defaultValue = null;
                        if(dueDate != -1 && dueDate < System.currentTimeMillis()) { //Expired policy,reset to default
                            if((defaultValue = keyBundle.getString(PolicyStore.KEY_DEFAULT_VALUE)) != null)
                                value = defaultValue;
                        }
                        if(IS_DEBUG_ULOG) Log.d(TAG,"categroy: " + category + ", key: "+key+", value: "+value+", due date: "+dueDate
                                +", current time: "+System.currentTimeMillis()+", default value: "+defaultValue);
                        
                        // If the policy is expired without default value, then return its original set value, else return null
                        return value == null ? "" : value;
                    }
                }
            }
        }
        return "";      
    }
    
    /**
	 * check error report UI of setting first. Then, check if the value of key "enable"
	 * is 1.
	 */
	public static boolean canLogErrorReport(String appid, String category, Context context) {
		Bundle allPolicy = PolicyStore.getInstance(context).getPolicy();
    	if(isSecondSwitchOfErrorReportEnabled(allPolicy)){
			if(isErrorReportCheckBoxEnabled(context) && isTellhtcUiEnable(context)){
				return canLog(allPolicy, appid, category, true);// set defaultValueIfItemNotFound to true for blacklist
			}
    	}
		return false;	
	}
	
    public static boolean canLogUB(String appid, String category, Context context) {
        Bundle allPolicy = PolicyStore.getInstance(context).getPolicy();
        return canLog(allPolicy, appid, category, true);// set defaultValueIfItemNotFound to true for blacklist
    }
    
    private static boolean isErrorReportCheckBoxEnabled(Context context) {
		return Settings.Secure.getInt(context.getContentResolver(), Common.SEND_HTC_ERROR_REPORT, 0) == 1;
	}
    
    private static boolean isTellhtcUiEnable(Context context){
    	return Settings.Secure.getInt(context.getContentResolver(), Common.HTC_ERROR_REPORT_SETTING, 0) == 1;
	}
    
    /**
	 * This doesn't mean the check box on the UI. It is the second-layer switch to turn on/off error report. 
	 * The checking sequence is to check check box UI first and then check this policy.
	 * @return
	 */
	private static boolean isSecondSwitchOfErrorReportEnabled(Bundle policy) {
		Bundle allPolicy = policy;
		if(allPolicy != null) {
			boolean b = allPolicy.getBoolean(KEY_SECOND_SWITCH_OF_ERROR_REPORT, true);
			if(!b)
                Log.d(TAG, "[isSecondSwitchOfErrorReportEnabled] "+KEY_SECOND_SWITCH_OF_ERROR_REPORT+" value: "+b);
			return b;
		}
		
		Log.d(TAG, "[isSecondSwitchOfErrorReportEnabled] policy is null, turned on by default");
		return true;
	}
    
    private static boolean canLog(Bundle allPolicy, String appid, String category, boolean defaultValueIfItemNotFound) {
        // Empty appid and category are abnormal and just return false (2014-04-09)
        if(TextUtils.isEmpty(appid) || TextUtils.isEmpty(category))  
            return false;
        
        if(allPolicy != null) {
            Bundle appBundle = allPolicy.getBundle(appid);
            return canLog(appBundle, category, defaultValueIfItemNotFound);
        }
        return true;  // Return true even policy is null which means policy init is not ready
    }
    
    private static boolean canLog(Bundle appBundle, String category, boolean defaultValueIfItemNotFound) {
        if(appBundle != null) {
            Bundle cateBundle = appBundle.getBundle(category);
            if(cateBundle != null) {
                Bundle keyBundle = cateBundle.getBundle("enable");
                if(keyBundle != null) {
                    long dueDate = keyBundle.getLong(PolicyStore.KEY_DUE_DATE);
                    boolean expired = dueDate == -1 ? false : dueDate < System.currentTimeMillis();
                    boolean isEnabled = "1".equals(keyBundle.getString("enable"));
                    String defaultValue = keyBundle.getString(PolicyStore.KEY_DEFAULT_VALUE);
                    if(expired && defaultValue != null)
                        isEnabled="1".equals(defaultValue);
                    else if(expired && defaultValue == null)
                    	isEnabled=true;
                    Log.d(TAG,"[canLog] category: "+category+", enable: "+isEnabled
                            +", due date: "+dueDate+", current time: "+System.currentTimeMillis()
                            +", default value: "+defaultValue);
                    return isEnabled;
                }
            }
        }
        
        // Because we don't maintain the default value of UP policies any more
        // and the data point policies are empty now, the default
        // returned value of this method passes checking for all UP
        // policies. For error report, return false if no item found.
        // (2014-04-09)
        Log.d(TAG, "[canLog (default)] category: " + category + ", enable: "
                + defaultValueIfItemNotFound);
        
        return defaultValueIfItemNotFound;
    }
    
}
