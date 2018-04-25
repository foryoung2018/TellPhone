package com.htc.feedback;

import com.htc.feedback.framework.ErrorReport;
import com.htc.feedback.policy.UPolicy;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.app.ApplicationErrorReport;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.ReflectionUtil;

import android.os.Parcelable;

// create this to prevent ANR ...
public class ReceiverService extends IntentService {
    private static final boolean LOG = Common.DEBUG;
    private static final String TAG = "ReceiverService";
    private static final String SHOW_HTC_APPLICATION_LOG = Common.SHOW_HTC_APPLICATION_LOG;
    private static final String BOTH_NETWORK_OPTION = Common.BOTH_NETWORK_OPTION;

    private static final String ACTION_NOTIFY_KERNEL_CRASH = "com.htc.updater.NOTIFY_KERNEL_CRASH";// [sy_wang, 20130603, For ResetNotifyService.java]
    private static final String ACTION_NOTIFY_SYSTEM_CRASH = "com.htc.updater.NOTIFY_SYSTEM_CRASH";
    private static final String ACTION_CUSTOMIZATION_FORCE = "com.htc.intent.action.CUSTOMIZATION_FORCE_CHANGE";
    private static final String ACTION_CUSTOMIZATION_FORCE_USER = "com.htc.intent.action.CUSTOMIZATION_FORCE_CHANGE_USER";
    private static final String ACTION_AUTO_SEND_REPORT = Intent.ACTION_APP_ERROR;

    private static final String KEY_CUSTOMIZED_REASON = "com.htc.CUSTOMIZED_REASON";
    private static final String KEY_DEVICE_INFO_DUMP_PATH = "com.htc.INFO_PATH";

    private boolean reportSetting = false;
    private boolean reportEnable = false;
    private boolean autoSend = false;

    private static final String ENABLE = "1";
    private static final String DISABLE = "0";
    private static final String STR_YES = "yes";

    public ReceiverService() {
        super("ReceiverService");
    }

    private boolean setValue(ContentResolver cr, String setting, String value) {
        try {
            Settings.Secure.putString(cr, setting, value);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "error in setting preference", e);
            return false;
        }
    }

    private String getValue(ContentResolver cr, String setting) {
        try {
            return Settings.Secure.getString(cr, setting);
        } catch (Exception e) {
            Log.e(TAG, "error in setting preference", e);
            return null;
        }
    }

    private boolean isTellHTCEnabled(ContentResolver cr) {
    	if(!ReportConfig.isShippingRom()) {
	    	boolean profile_force_disable_error_report;
	        String factoryTestStr = ReflectionUtil.get("ro.factorytest");
	        int factoryTest = "".equals(factoryTestStr) ? 0 : Integer.parseInt(factoryTestStr);
	        if (factoryTest != 0) {
	            profile_force_disable_error_report = true;
	        } else {
	            profile_force_disable_error_report = ReflectionUtil.getBoolean("profiler.force_disable_err_rpt", false);
	        }
	        if (profile_force_disable_error_report)
	            return false;
    	}

        try {
            reportSetting = Settings.Secure.getInt(cr, Common.HTC_ERROR_REPORT_SETTING, 0) == 1;
            reportEnable = Settings.Secure.getInt(cr, Common.SEND_HTC_ERROR_REPORT, 0) == 1;
            autoSend = ErrorReport.isAutoSend(cr);

            if (LOG)
                Log.d(TAG, "reportSetting=" + reportSetting + " reportEnable=" + reportEnable + " autoSend=" + autoSend);

           	return (reportSetting & reportEnable);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    	try {
			onHandleIntentInner(intent);
		} catch (Exception e) {
			Log.e(TAG, "Exception in onHandleIntentInner", e);
		}
    }
    
    private void onHandleIntentInner(Intent intent) {
        String action = intent.getAction();
        if (LOG)
            Log.d(TAG, "onHandleIntent " + action);

        if (action == null)
            return;
        ContentResolver cr = getContentResolver();
        if (action.equals(ACTION_NOTIFY_SYSTEM_CRASH) || action.equals(ACTION_NOTIFY_KERNEL_CRASH)) { // system server java crash, native crash, [sy_wang, 20130603, Add ACTION_NOTIFY_KERNEL_CRASH for ResetNotifyService.java]
            if (!isTellHTCEnabled(cr))
                return;

            if (autoSend)
                autoSendReport(intent);
            else
                notifySystemCrash(intent);
        } else if (action.equals(ACTION_AUTO_SEND_REPORT)) { // app java crash, anr, app native crash in auto send mode (include HTC_HOME_RESTART)
            if (!isTellHTCEnabled(cr))
                return;
            
            if (autoSend)
                autoSendReport(intent);
            else {
            	if (LOG)
            		Log.d(TAG, "autoSend is false, ignore the log");
            }
        } else if (ACTION_CUSTOMIZATION_FORCE.equals(action)) { // factory reset and FOTA
            boolean CID = intent.getBooleanExtra("CID", false);
            String customizedReason = intent.getStringExtra(KEY_CUSTOMIZED_REASON);
            if (LOG)
                Log.d(TAG, "CID=" + CID + " reason=" + customizedReason);
            if (CID) {
                handleCustomization(customizedReason);
            }
        } else if (ACTION_CUSTOMIZATION_FORCE_USER.equals(action)) { // sy_wang, 20150506, for non-owner user in multi-user mode
            Log.d(TAG, "The current index of customization is " + intent.getIntExtra("currentSN", -1));

            ArrayList<Bundle> list = intent.getParcelableArrayListExtra("data");
            if (list == null) {
                Log.d(TAG, "Can't get data from " + action);
                return;
            }
            for (Bundle bundle : list) {
                if (bundle == null) {
                    if (LOG)
                        Log.d(TAG, "Got null bundle");
                    continue;
                }
                boolean CID = bundle.getBoolean("CID", false);
                String customizedReason = bundle.getString(KEY_CUSTOMIZED_REASON);
                if (LOG)
                    Log.d(TAG, "CID=" + CID + " reason=" + customizedReason);
                if (CID) {
                    handleCustomization(customizedReason);
                    // Only do customization once, no need to do it each time, because customization DB value is the latest.
                    break;
                }
            }
        }
    }

    private void handleCustomization(String customizedReason) {
        customizeCrashReport(customizedReason);

        // sy_wang, rebuild search index after do customization
        Intent intentSearchRebuild = new Intent("com.htc.settings.action.SEARCH_UPDATE");
        intentSearchRebuild.putExtra("package", "com.htc.feedback");
        intentSearchRebuild.putExtra("rebuild", true);
        getApplicationContext().sendBroadcast(intentSearchRebuild);
    }

    private void autoSendReport(Intent intent) {
        ApplicationErrorReport report = (ApplicationErrorReport) intent.getParcelableExtra(Intent.EXTRA_BUG_REPORT);
        
        UPolicy policy = new UPolicy(UPolicy.APPID_ERROR_REPORT, getApplicationContext());
        String category = (report == null) ? intent.getStringExtra("tag") : intent.getStringExtra("dropboxTag");
        if (!UPolicy.canLogErrorReport(UPolicy.APPID_ERROR_REPORT, category, getApplicationContext())) {
            if (LOG)
                Log.d(TAG, "disable by UPolicy: " + category);
            return;
        }

        Intent autoSendIntent = new Intent();
        autoSendIntent.putExtras(intent);
        autoSendIntent.putExtra("msg", "auto send");
        Context appContext = getApplicationContext();
        // TODO do we need location
        if (report != null && report.type == ApplicationErrorReport.TYPE_ANR
                && !ReportConfig.isShippingRom()) {
            autoSendIntent.setClass(appContext, FeedbackBugReportActivity.class);
            autoSendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(autoSendIntent);
        } else {
            autoSendIntent.setClass(appContext, FeedbackService.class);
            startService(autoSendIntent);
        }
    }

    private void notifySystemCrash(Intent intent) {
        if (LOG)
            Log.d(TAG, "notifySystemCrash intent = " + intent.getExtras());
        
        boolean isKernelCrash = false;
        Parcelable report = intent.getParcelableExtra(Intent.EXTRA_BUG_REPORT);
        if (report == null) {
        	isKernelCrash = true;
        }
        Log.d(TAG, "isKernelCrash: " + isKernelCrash);
        
        Context appContext = getApplicationContext();
        Intent systemCrashIntent = new Intent(appContext, SystemCrashActivity.class);
        systemCrashIntent.putExtras(intent);

        CharSequence title = getText(R.string.preview_error_category);
        CharSequence message = getText(R.string.msg_system_crash_notify);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder nBuilder = new Notification.Builder(appContext);
        nBuilder.setSmallIcon(R.drawable.stat_notify_emergency_mode);
        nBuilder.setWhen(0); // Don't show time on notification
        nBuilder.setTicker(title); // Text show on status bar when notification come
        nBuilder.setOngoing(true); // Can't be removed by user swipe
        nBuilder.setAutoCancel(true); // Will dismiss after user click
        nBuilder.setContentTitle(title);
        nBuilder.setContentText(message);
        nBuilder.setContentIntent(PendingIntent.getActivity(appContext, 0, systemCrashIntent, PendingIntent.FLAG_CANCEL_CURRENT));
        nm.notify(0, nBuilder.build());
    }

    private Bundle loadCustomizeData() {
        Uri uri = Uri.parse("content://customization_settings/SettingTable/application_Crash_Report");
        Bundle customizationBundle = null;
        Cursor cursor = null;
        ContentResolver cr = getContentResolver();
        try {
            cursor = cr.query(uri, null, null, null, null);

            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndexOrThrow("value");
                    byte[] data = cursor.getBlob(columnIndex);

                    if (LOG)
                        Log.d(TAG, "columnIndex=" + columnIndex + " data length=" + data.length);
                    Parcel parcel = Parcel.obtain();
                    parcel.unmarshall(data, 0, data.length);
                    parcel.setDataPosition(0);
                    customizationBundle = new Bundle();
                    customizationBundle.readFromParcel(parcel);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "loadCustomizeData fail", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            cursor = null;
        }
        return customizationBundle;
    }

    private void customizeCrashReport(String customizedReason) {
        if (LOG)
            Log.d(TAG, "start customzeCrashReport");
        ContentResolver cr = getContentResolver();

        Bundle customizationBundle = loadCustomizeData();
        String tellHTCOff = null;
        String errorReportOff = null;
        String hideUsageReport = null;
        String onlyWifiOption = null;
        String autoReportPreference = null;
        String allDataPreference = null;

        if (customizationBundle != null) {
            Bundle setting = customizationBundle.getBundle("setting");
            if (setting != null) {
                tellHTCOff = setting.getString("turn_off_tell_htc");
                errorReportOff = setting.getString("turn_off_report");
                hideUsageReport = setting.getString("hide_usage_report");
                onlyWifiOption = setting.getString("only_wifi_option");
                autoReportPreference = setting.getString("enable_auto_report_preference");
                allDataPreference = setting.getString("all_data_connection_preference");
                  
                Log.i(TAG, "Tell HTC customization flag: turn_off_tell_htc = " + tellHTCOff + ", turn_off_report = "
                        + errorReportOff + ", hide_usage_report = " + hideUsageReport + ", onlyWifiOption = " + onlyWifiOption
                        + ", enable_auto_report_preference = " + autoReportPreference + ", all_data_connection_preference = " + allDataPreference);
            } else {
                Log.w(TAG, "customization setting = null");
            }
        } else {
            Log.w(TAG, "customizationBundle = null");
        }
        // sy_wang, 20150422, We only enable usage report in owner user
        UserManager um = (UserManager) getApplicationContext().getSystemService(Context.USER_SERVICE);
        if (um != null) {
            UserHandle userHandle = android.os.Process.myUserHandle();
            if (um.getSerialNumberForUser(userHandle) != 0) {
                hideUsageReport = STR_YES;
                Log.d(TAG, "This is not owner user so hide and disable usage report");
            }
        }
        if (!ReportConfig.isShippingRom()) {
            Log.d(TAG, "Eng+Normal mode: enable Tell HTC by default");
            setValue(cr, Common.HTC_ERROR_REPORT_SETTING, ENABLE);
            setValue(cr, Common.SEND_HTC_ERROR_REPORT, ENABLE);
            String description = ReflectionUtil.get("ro.build.description");
            boolean isDashboardBuild = false;
            if(description != null && description.startsWith("0.1.0.0"))
                isDashboardBuild = true;
            setValue(cr, Common.HTC_ERROR_REPORT_AUTO_SEND, !isDashboardBuild ? ENABLE : DISABLE);
            setValue(cr, SHOW_HTC_APPLICATION_LOG, ENABLE);
            
            if (hideUsageReport != null && hideUsageReport.equals(STR_YES)) {
                setValue(cr, SHOW_HTC_APPLICATION_LOG, DISABLE);
                setValue(cr, Common.SEND_HTC_APPLICATION_LOG, DISABLE);
            }
            
            // default: all data network
            setValue(cr, BOTH_NETWORK_OPTION, ENABLE);
            setValue(cr, Common.HTC_ERROR_REPORT_PREFER_NETWORK, "0");
            // if set only wifi 
            if (onlyWifiOption != null && onlyWifiOption.equals(STR_YES)) {
                setValue(cr, Common.HTC_ERROR_REPORT_PREFER_NETWORK, "1");
                setValue(cr, BOTH_NETWORK_OPTION, DISABLE);
            }
            return;
        }

        String errorReportSetting = getValue(cr, Common.HTC_ERROR_REPORT_SETTING);
        if (errorReportSetting == null || !ENABLE.equals(errorReportSetting)) {
            // if htc_error_report_setting is NOT set or is set as FALSE
            // preset all setting by default

            // init default value
            setValue(cr, Common.HTC_ERROR_REPORT_SETTING, ENABLE);
            setValue(cr, Common.SEND_HTC_ERROR_REPORT, ENABLE);
            setValue(cr, Common.HTC_ERROR_REPORT_AUTO_SEND, DISABLE);
            setValue(cr, SHOW_HTC_APPLICATION_LOG, ENABLE);
            setValue(cr, BOTH_NETWORK_OPTION, ENABLE);
            
            // config data transfer network
            setValue(cr, Common.HTC_ERROR_REPORT_PREFER_NETWORK, "1");
            
            // very annoy
            if (tellHTCOff != null && tellHTCOff.equals(STR_YES)) {
                setValue(cr, Common.HTC_ERROR_REPORT_SETTING, DISABLE);
                setValue(cr, Common.SEND_HTC_ERROR_REPORT, DISABLE);
                setValue(cr, SHOW_HTC_APPLICATION_LOG, DISABLE);
                setValue(cr, Common.SEND_HTC_APPLICATION_LOG, DISABLE);
                return;
            }

            if (errorReportOff != null && errorReportOff.equals(STR_YES))
                setValue(cr, Common.SEND_HTC_ERROR_REPORT, DISABLE);

            if (hideUsageReport != null && hideUsageReport.equals(STR_YES)) {
                setValue(cr, SHOW_HTC_APPLICATION_LOG, DISABLE);
                setValue(cr, Common.SEND_HTC_APPLICATION_LOG, DISABLE);
            }
            
            if (autoReportPreference != null && autoReportPreference.equals(STR_YES)) {
                setValue(cr, Common.HTC_ERROR_REPORT_AUTO_SEND, ENABLE);
            }
            
            if (allDataPreference != null && allDataPreference.equals(STR_YES)) {
                setValue(cr, Common.HTC_ERROR_REPORT_PREFER_NETWORK, "0");
            }
            
            if (onlyWifiOption != null && onlyWifiOption.equals(STR_YES)) {
                setValue(cr, Common.HTC_ERROR_REPORT_PREFER_NETWORK, "1");
                setValue(cr, BOTH_NETWORK_OPTION, DISABLE);
            }
            
        } else {
            // Tell HTC was already enabled.
            // Only preset HTC_ERROR_REPORT_SETTING and SHOW_HTC_APPLICATION_LOG

            if (tellHTCOff != null && tellHTCOff.equals(STR_YES)) {
                setValue(cr, Common.HTC_ERROR_REPORT_SETTING, DISABLE);
                setValue(cr, Common.SEND_HTC_ERROR_REPORT, DISABLE);
                setValue(cr, SHOW_HTC_APPLICATION_LOG, DISABLE);
                setValue(cr, Common.SEND_HTC_APPLICATION_LOG, DISABLE);
                return;
            }
            
            if (onlyWifiOption != null && onlyWifiOption.equals(STR_YES)) {
                setValue(cr, Common.HTC_ERROR_REPORT_PREFER_NETWORK, "1");
                setValue(cr, BOTH_NETWORK_OPTION, DISABLE);
            }

            if (hideUsageReport != null && hideUsageReport.equals(STR_YES)) {
                setValue(cr, SHOW_HTC_APPLICATION_LOG, DISABLE);
                setValue(cr, Common.SEND_HTC_APPLICATION_LOG, DISABLE);
                return;
            } else {
                setValue(cr, SHOW_HTC_APPLICATION_LOG, ENABLE);
            }
        }
    }
}
