package com.htc.feedback.reportagent.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

public class PackageInfoUtil {
    private static final String TAG = "PackageInfoUtil";
    
    private PackageInfoUtil() {}
    
    public static boolean isUpdatedSystemApp(Context ctx, String packageName) {
        return (getAppFlags(ctx, packageName) & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
    }
    
    public static boolean isSystemApp(Context ctx, String packageName) {
        return (getAppFlags(ctx, packageName) & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
    
    private static int getAppFlags(Context ctx, String packageName) {
        if(ctx == null || TextUtils.isEmpty(packageName))
            return 0;
        
        PackageManager pm = ctx.getPackageManager();
        if(pm != null) {
            ApplicationInfo appInfo = null;
            try {
                appInfo = pm.getApplicationInfo(packageName, 0);
            } catch (NameNotFoundException e) {
                Log.e(TAG, "can't find info of package : "+packageName, e);
            }
            if(appInfo != null)
                return appInfo.flags;
        }
        return 0;
    }
    
    public static String getInstallerPackageName(Context ctx, String packageName, String defaultInstallerName) {
        if(ctx == null || TextUtils.isEmpty(packageName))
            return defaultInstallerName;
        
        String installer = null;
        PackageManager pm = ctx.getPackageManager();
        if(pm != null) {
            try {
                installer = pm.getInstallerPackageName(packageName);
            } catch(Exception e) {
                Log.e(TAG, "fail to call getInstallerPackageName with parameter package name: "+packageName, e);
            }
        }

        return TextUtils.isEmpty(installer) ? defaultInstallerName : installer;
    }
    
    public static String getVersionName(Context ctx, String packageName, String defaultVersionName) {
        PackageInfo packageInfo = getPackageInfo(ctx, packageName);
        if(packageInfo != null) {
            String versionName = packageInfo.versionName;
            return TextUtils.isEmpty(versionName) ? defaultVersionName : versionName;
        }
        else
            return defaultVersionName;
    }
    
    public static int getVersionCode(Context ctx, String packageName, int defaultVersionName) {
        PackageInfo packageInfo = getPackageInfo(ctx, packageName);
        if(packageInfo != null)
            return packageInfo.versionCode;
        else
            return defaultVersionName;
    }
    
    private static PackageInfo getPackageInfo(Context ctx, String packageName) {
        if(ctx == null || TextUtils.isEmpty(packageName))
            return null;
        
        PackageInfo packageInfo = null;
        PackageManager pm = ctx.getPackageManager();
        if(pm != null) {
            try {
                packageInfo = pm.getPackageInfo(packageName, 0);
            } catch (NameNotFoundException e) {
                Log.e(TAG, "can't find info of package : "+packageName, e);
            }
        }
        return packageInfo;     
    }
}
