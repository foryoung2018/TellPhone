package com.htc.feedback.reportagent.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class SignatureUtil {
    private static final String TAG = "SignatureUtil";
    private final HashMap<String,Signature> mHtcSignature;
    private boolean mInit;
    private static final String [][] SIGNATURE_APP_NAME_PAIR = {
            {"platform", "android"},
            {"shared","com.android.providers.contacts"},
            {"media", "com.android.providers.media"},
            {"testkey","com.htc.provider.CustomizationSettings"},
            {"testkey","com.android.providers.calendar"},
            {"hms","com.htc.sense.hsp"},                
            {"hms","com.htc.launcher"}
    };
    private static SignatureUtil sInstance;
    
    private SignatureUtil() {
        mHtcSignature = new HashMap<String,Signature>();
    }
    
    public static synchronized SignatureUtil getInstance() {
        if(sInstance == null) {
            sInstance = new SignatureUtil();
        }
        return sInstance;
    }
    
    private synchronized void initialize(Context ctx) {
        if(ctx == null) 
            return;
        
        PackageManager pm = ctx.getPackageManager();
        if(pm == null) 
            return;
        
        if(!mInit) {
            mHtcSignature.clear();
            for(String [] predefinedPair : SIGNATURE_APP_NAME_PAIR) {
                // Prevent signature to be overridden by next signature which has same name. The prior signature
                // is better choice to us. Ex. The first priority of signature hms is com.htc.sense.hsp. The 
                // second is com.htc.launcher.
                if(!mHtcSignature.containsKey(predefinedPair[0])) {
                    PackageInfo pi = null;
                    try {
                        pi = pm.getPackageInfo(predefinedPair[1], PackageManager.GET_SIGNATURES);
                    } catch (NameNotFoundException e) {
                        Log.d(TAG, "[initialize] can't find package info of "+predefinedPair[1]);
                    }
                    
                    if(pi != null) {
                        if(pi.signatures != null)
                            mHtcSignature.put(predefinedPair[0], pi.signatures[0]);
                        else
                            Log.d(TAG, "[initialize] Signature of "+predefinedPair[1]+" is null");
                    }
                }
                else {
                    Log.d(TAG, "[initialize] Not add " + predefinedPair[1] + " because short key name " + predefinedPair[0] + " has been added before");
                }
            }
            mInit = true;
        }
        return;
    }
    
    public String getSignatureName(Context ctx, String packageName, String defaultSignatureName) {
        if(ctx == null || TextUtils.isEmpty(packageName)) 
            return defaultSignatureName;
        
        PackageManager pm = ctx.getPackageManager();
        if(pm == null) 
            return defaultSignatureName;
        
        if(!mInit)
            initialize(ctx);
        
        if(mInit) {
            PackageInfo pi = null;
            try {
                pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            } catch (NameNotFoundException e) {
                Log.d(TAG, "[getSignatureName] can't find package info of "+packageName);
            }
            
            if(pi != null) {
                if(pi.signatures != null) {
                    Signature targetAppSignature = pi.signatures[0];
                    if(targetAppSignature != null) {
                        Set<Entry<String,Signature>> entrySet = mHtcSignature.entrySet();
                        for(Map.Entry<String,Signature> entry: entrySet) {
                            Signature predefinedSignature = entry.getValue();
                            if(targetAppSignature.equals(predefinedSignature)) {
                                return entry.getKey();
                            }
                        }
                    }
                    else{
                        Log.d(TAG, "[getSignatureName] Signature of "+packageName+"[0] is null");
                    }
                }
                else {
                    Log.d(TAG, "[getSignatureName] Signature of "+packageName+" is null");
                }
            }
        }
        return defaultSignatureName;
    }
    
    public boolean isSignedHtcKey(Context ctx, String packageName) {
        return !TextUtils.isEmpty(getSignatureName(ctx, packageName, ""));
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Signature array:\n");
        int count = 0;
        if(mHtcSignature != null) {
            for(Map.Entry<String,Signature> entry : mHtcSignature.entrySet()) {
                sb.append("  ").append(Integer.toString(count)).append(" ").append(entry.getKey()).append(", ").append(entry.getValue()).append("\n");
                ++count;
            }
        }
        return sb.toString();
    }
}
