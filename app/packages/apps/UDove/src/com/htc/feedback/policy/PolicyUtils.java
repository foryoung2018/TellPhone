package com.htc.feedback.policy;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.util.ReportConfig;
import com.htc.feedback.reportagent.util.Utils;

public class PolicyUtils {
    private static final String TAG = "PolicyUtils";
    private static final boolean _DEBUG = Common.DEBUG;
    
    public static boolean isPolicyEnabled(Context context) {
        if(!ReportConfig.isShippingRom()) {
            if(_DEBUG) Log.d(TAG,"[isPolicyEnabled(engineering)] force enabled");
            return true;  // Force enabling policy channel because up is forced enabled
        }
        
        if(Utils.isUserProfilingSettingEnabled(context)){
            if(_DEBUG) Log.d(TAG,"[isPolicyEnabled(shipping)] enabled");
            return true;
        }
        else
            return false;
    }
    
    /** no need of this function due to query view but not real table
    /**
     * for those policies with default value.
     * {@hide}
     */
    public static void putPolicy(Bundle bundle, String appid, String category, String key, String value, long dueDate,String default_value) {
        if(bundle == null || TextUtils.isEmpty(appid) || TextUtils.isEmpty(category)
                || TextUtils.isEmpty(key) || TextUtils.isEmpty(value))
            return;
        
        Bundle appidBundle = null, categoryBundle = null, keyBundle = null; // keyBundle is needed to store due date connected to key/value. 
        appidBundle = bundle.getBundle(appid);
        if(appidBundle == null) {
            appidBundle = new Bundle();
            categoryBundle = new Bundle();
            keyBundle = new Bundle();
            keyBundle.putString(key,value);
            keyBundle.putLong(PolicyStore.KEY_DUE_DATE, dueDate);
            keyBundle.putString(PolicyStore.KEY_DEFAULT_VALUE,default_value);
            categoryBundle.putBundle(key, keyBundle);
            appidBundle.putBundle(category, categoryBundle);
        }
        else {
            categoryBundle = appidBundle.getBundle(category);
            if(categoryBundle == null) {
                categoryBundle = new Bundle();
                keyBundle = new Bundle();
                keyBundle.putString(key,value);
                keyBundle.putLong(PolicyStore.KEY_DUE_DATE, dueDate);
                keyBundle.putString(PolicyStore.KEY_DEFAULT_VALUE,default_value);
                categoryBundle.putBundle(key, keyBundle);
                appidBundle.putBundle(category, categoryBundle);
            }
            else {
                keyBundle = categoryBundle.getBundle(key);
                if(keyBundle == null) {
                    keyBundle = new Bundle();
                    keyBundle.putString(key,value);
                    keyBundle.putLong(PolicyStore.KEY_DUE_DATE, dueDate);
                    keyBundle.putString(PolicyStore.KEY_DEFAULT_VALUE,default_value);
                    categoryBundle.putBundle(key, keyBundle);
                }
                else {
                    String oldValue = keyBundle.getString(key);
                    if(oldValue == null) {
                        keyBundle.putString(key,value);
                        keyBundle.putLong(PolicyStore.KEY_DUE_DATE, dueDate); 
                        keyBundle.putString(PolicyStore.KEY_DEFAULT_VALUE,default_value);
                    }
                    else {
                        keyBundle.putString(key,value);
                        keyBundle.putLong(PolicyStore.KEY_DUE_DATE, dueDate);
                        keyBundle.putString(PolicyStore.KEY_DEFAULT_VALUE,default_value);
                        Log.d(TAG, "[appid :"+appid+"category : "+category+"] Duplicate data key => key : "+key+", value : "+value+", old value : "+oldValue+", due date: "+dueDate);
                    }
                }
            }
        }
        bundle.putBundle(appid, appidBundle);
    }
    
    /**
     * dueDate will be treated for the specific key/value effective date.
     */
    public static void putPolicy(Bundle bundle, String appid, String category, String key, String value, long dueDate) {
        if(bundle == null || TextUtils.isEmpty(appid) || TextUtils.isEmpty(category)
                || TextUtils.isEmpty(key) || TextUtils.isEmpty(value))
            return;
        
        Bundle appidBundle = null, categoryBundle = null, keyBundle = null; // keyBundle is needed to store due date connected to key/value. 
        appidBundle = bundle.getBundle(appid);
        if(appidBundle == null) {
            appidBundle = new Bundle();
            categoryBundle = new Bundle();
            keyBundle = new Bundle();
            keyBundle.putString(key,value);
            keyBundle.putLong(PolicyStore.KEY_DUE_DATE, dueDate);
            categoryBundle.putBundle(key, keyBundle);
            appidBundle.putBundle(category, categoryBundle);
        }
        else {
            categoryBundle = appidBundle.getBundle(category);
            if(categoryBundle == null) {
                categoryBundle = new Bundle();
                keyBundle = new Bundle();
                keyBundle.putString(key,value);
                keyBundle.putLong(PolicyStore.KEY_DUE_DATE, dueDate);
                categoryBundle.putBundle(key, keyBundle);
                appidBundle.putBundle(category, categoryBundle);
            }
            else {
                keyBundle = categoryBundle.getBundle(key);
                if(keyBundle == null) {
                    keyBundle = new Bundle();
                    keyBundle.putString(key,value);
                    keyBundle.putLong(PolicyStore.KEY_DUE_DATE, dueDate);
                    categoryBundle.putBundle(key, keyBundle);
                }
                else {
                    String oldValue = keyBundle.getString(key);
                    if(oldValue == null) {
                        keyBundle.putString(key,value);
                        keyBundle.putLong(PolicyStore.KEY_DUE_DATE, dueDate);                       
                    }
                    else {
                        keyBundle.putString(key,value);
                        keyBundle.putLong(PolicyStore.KEY_DUE_DATE, dueDate);
                        Log.d(TAG, "[appid :"+appid+"category : "+category+"] Duplicate data key => key : "+key+", value : "+value+", old value : "+oldValue+", due date: "+dueDate);
                    }
                }
            }
        }
        bundle.putBundle(appid, appidBundle);
    }
    
}
