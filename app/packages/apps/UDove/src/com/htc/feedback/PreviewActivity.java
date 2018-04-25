package com.htc.feedback;

import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.ApplicationErrorReport;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.content.Context;

import com.htc.feedback.reportagent.Common;
import com.htc.lib1.theme.NavBarColorUtil;

public class PreviewActivity extends Activity {
    private static final String TAG = "PreviewActivity";
    private static final boolean LOG = Common.DEBUG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LOG)
            Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //navigation bar color change feature(Only for SW navigation bar and 18:9 device)
        NavBarColorUtil.setNavBarBkg(getWindow());

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        
        MyPreferenceFragment fragments = new MyPreferenceFragment();
        fragmentTransaction.replace(android.R.id.content, fragments);
        fragmentTransaction.commit();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
    
	@Override
    public void onResume() {
        if (LOG)
            Log.d(TAG, "onResume()");
        super.onResume();
    }
    
    private static String pad(int n) {
        if (n >= 10) {
            return String.valueOf(n);
        } else {
            return "0" + String.valueOf(n);
        }
    }

    private static String timeMilisToString(long time) {
        long t = time / 1000;
        int s = (int) (t % 60);
        int m = (int) ((t / 60) % 60);
        int h = (int) ((t / 3600));
        return h + ":" + pad(m) + ":" + pad(s);
    }
    
    public static class MyPreferenceFragment extends PreferenceFragment{
    	// sy_wang, 20140103, When apply translucent, we need setFitsSystemWindows to let content not be covered by status bar and ActionBar
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	View view = super.onCreateView(inflater, container, savedInstanceState);
        	view.setFitsSystemWindows(true);
        	
        	return view;
        }
        
    	@Override
		public void onCreate(Bundle savedInstanceState) {
    		if (LOG)
	            Log.d(TAG, "[MyPreferenceFragment] onCreate");
    		super.onCreate(savedInstanceState);
    		addPreferencesFromResource(R.xml.feedback_preview);
    		initPreferences();
    	}
    	
    	private void initPreferences() {
            if (LOG)
                Log.d(TAG, "initPreferences()");
            Activity activity = getActivity();
            if (activity == null) {
                Log.d(TAG, "[initPreferences] getActivity is null!");
                return;
            }
            
            PackageManager pm = activity.getPackageManager();
            Intent intent = activity.getIntent();

            ApplicationErrorReport report = (ApplicationErrorReport) intent.getParcelableExtra(Intent.EXTRA_BUG_REPORT);
            PackageInfo pkgInfo = null;
            try {
                String packageName = (report != null) ? report.packageName : "android";
                pkgInfo = pm.getPackageInfo(packageName, 0);
            } catch (Exception e) {
                Log.e(TAG, "no package info", e);
            }

            PreferenceScreen rootPreference = getPreferenceScreen();

            // error report
            PreferenceCategory reportCategory = (PreferenceCategory) rootPreference.findPreference("preview_error");
            if (report != null && pkgInfo != null ///// [CQG #11952512] yunmei_lu, 20120925
                    && (report.type == ApplicationErrorReport.TYPE_CRASH || report.type == ApplicationErrorReport.TYPE_ANR)) {
                if (report.type == ApplicationErrorReport.TYPE_CRASH) {
                    Preference error_type = (Preference) reportCategory.findPreference("error_type");
                    error_type.setSummary(R.string.preview_crash_category);
                } else {
                	Preference error_type = (Preference) reportCategory.findPreference("error_type");
                	error_type.setSummary(R.string.preview_anr_category);
                }
                Preference error_package_name = (Preference) reportCategory.findPreference("error_package_name");
                error_package_name.setSummary(report.packageName);
                
                Preference error_package_version = (Preference) reportCategory.findPreference("error_package_version");
                error_package_version.setSummary(String.valueOf(pkgInfo.versionCode));
                
                Preference error_package_version_name = (Preference) reportCategory.findPreference("error_package_version_name");
                error_package_version_name.setSummary(pkgInfo.versionName);
                
                Preference error_installed_by = (Preference) reportCategory.findPreference("error_installed_by");
                error_installed_by.setSummary(report.installerPackageName);
                
                Preference error_process_name = (Preference) reportCategory.findPreference("error_process_name");
                error_process_name.setSummary(report.processName);
                
                Preference error_time = (Preference) reportCategory.findPreference("error_time");
                String lowerCase = DateFormat.getDateInstance(DateFormat.FULL).format(new Date(report.time));
                error_time.setSummary(lowerCase);
                
                Preference error_up_time = (Preference) reportCategory.findPreference("error_up_time");
                error_up_time.setSummary(timeMilisToString(SystemClock.elapsedRealtime()));
                
                Preference error_awake_time = (Preference) reportCategory.findPreference("error_awake_time");
                error_awake_time.setSummary(timeMilisToString(SystemClock.uptimeMillis()));
                
                Preference error_system_app = (Preference) reportCategory.findPreference("error_system_app");
                error_system_app.setSummary(String.valueOf(report.systemApp));
            } else {
                reportCategory.findPreference("error_type").setSummary(R.string.preview_system_crash_category);
                long time = 0;
                if (report == null)
                    time = intent.getLongExtra("time", -1); // kernel crash
                else
                    time = report.time; // system crash
                reportCategory.findPreference("error_time").setSummary(
                        DateFormat.getDateInstance(DateFormat.FULL).format(new Date(time)));

                reportCategory.removePreference(reportCategory.findPreference("error_package_name"));
                reportCategory.removePreference(reportCategory.findPreference("error_package_version"));
                reportCategory.removePreference(reportCategory.findPreference("error_package_version_name"));
                reportCategory.removePreference(reportCategory.findPreference("error_installed_by"));
                reportCategory.removePreference(reportCategory.findPreference("error_process_name"));
                reportCategory.removePreference(reportCategory.findPreference("error_up_time"));
                reportCategory.removePreference(reportCategory.findPreference("error_awake_time"));
                reportCategory.removePreference(reportCategory.findPreference("error_system_app"));
            }

            // system
            Preference system_device = (Preference) rootPreference.findPreference("system_device");
            system_device.setSummary(Build.DEVICE);
            
            Preference system_build_id = (Preference) rootPreference.findPreference("system_build_id");
            system_build_id.setSummary(Build.DISPLAY);
            
            Preference system_build_type = (Preference) rootPreference.findPreference("system_build_type");
            system_build_type.setSummary(Build.TYPE);
            
            Preference system_model = (Preference) rootPreference.findPreference("system_model");
            system_model.setSummary(Build.MODEL);
            
            Preference system_product = (Preference) rootPreference.findPreference("system_product");
            system_product.setSummary(Build.PRODUCT);
            
            Preference system_sdk_version = (Preference) rootPreference.findPreference("system_sdk_version");
            system_sdk_version.setSummary(String.valueOf(Build.VERSION.SDK_INT));
            
            Preference system_release = (Preference) rootPreference.findPreference("system_release");
            system_release.setSummary(Build.VERSION.RELEASE);
            
            Preference system_incremental_version = (Preference) rootPreference.findPreference("system_incremental_version");
            system_incremental_version.setSummary(Build.VERSION.INCREMENTAL);
            
            Preference system_brand = (Preference) rootPreference.findPreference("system_brand");
            system_brand.setSummary(Build.BRAND);

            // crash/anr/system crash
            PreferenceCategory crashCategory = (PreferenceCategory) rootPreference
                    .findPreference("preview_crash_category");
            PreferenceCategory anrCategory = (PreferenceCategory) rootPreference
                    .findPreference("preview_anr_category");
            PreferenceCategory sysCrashCategory = (PreferenceCategory) rootPreference
                    .findPreference("preview_system_crash_category");
            if (report != null && report.type == ApplicationErrorReport.TYPE_CRASH) {
                rootPreference.removePreference(anrCategory);
                rootPreference.removePreference(sysCrashCategory);

                Preference crash_exception_class = (Preference) crashCategory.findPreference("crash_exception_class");
                crash_exception_class.setSummary(report.crashInfo.exceptionClassName);
                
                Preference crash_source_file = (Preference) crashCategory.findPreference("crash_source_file");
                if(TextUtils.isEmpty(report.crashInfo.throwFileName))
                	crashCategory.removePreference(crash_source_file);
                else
                	crash_source_file.setSummary(report.crashInfo.throwFileName);
                
                Preference crash_source_class = (Preference) crashCategory.findPreference("crash_source_class");
                if(TextUtils.isEmpty(report.crashInfo.throwClassName))
                	crashCategory.removePreference(crash_source_class);
                else
                	crash_source_class.setSummary(report.crashInfo.throwClassName);
                
                Preference crash_source_method = (Preference) crashCategory.findPreference("crash_source_method");
                if(TextUtils.isEmpty(report.crashInfo.throwMethodName))
                	crashCategory.removePreference(crash_source_method);
                else
                	crash_source_method.setSummary(report.crashInfo.throwMethodName);
                
                Preference crash_line_number = (Preference) crashCategory.findPreference("crash_line_number");
                if(report.crashInfo.throwLineNumber <= 0)
                	crashCategory.removePreference(crash_line_number);
                else
                	crash_line_number.setSummary(String.valueOf(report.crashInfo.throwLineNumber));
                
                crashCategory.findPreference("crash_stack_trace").setIntent(
                        genPreviewIntent(activity, getText(R.string.crash_stack_trace).toString(), report.crashInfo.stackTrace));
                
                setLogFilePreferences(activity, crashCategory, "crash_log");
                
            } else if (report != null && report.type == ApplicationErrorReport.TYPE_ANR) {
                rootPreference.removePreference(crashCategory);
                rootPreference.removePreference(sysCrashCategory);
                anrCategory.removePreference(anrCategory.findPreference("anr_stack_traces"));

                Preference anr_activity = (Preference) anrCategory.findPreference("anr_activity");
                anr_activity.setSummary(report.anrInfo.activity);
                
                Preference anr_caused_by = (Preference) anrCategory.findPreference("anr_caused_by");
                anr_caused_by.setSummary(report.anrInfo.cause);
                
                anrCategory.findPreference("anr_additional_info").setIntent(
                        genPreviewIntent(activity, getText(R.string.anr_additional_info).toString(), report.anrInfo.info));
                anrCategory.findPreference("anr_crash_log").setIntent(genPreviewIntent(activity, getText(R.string.system_crash_log).toString(), null));
            } else {
                rootPreference.removePreference(crashCategory);
                rootPreference.removePreference(anrCategory);
                
                setLogFilePreferences(activity, sysCrashCategory, "system_crash_log");
            }

            // radio, location related
            PreferenceCategory radioCategory = (PreferenceCategory) rootPreference
                    .findPreference("preview_radio_category");
                // GSM
                Preference mccPreference = (Preference) radioCategory.findPreference("radio_mcc");
                Preference mncPreference = (Preference) radioCategory.findPreference("radio_mnc");

                AdditionalInfo additionalInfo = new AdditionalInfo(getActivity());
                if (additionalInfo.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
                    mccPreference.setSummary(String.valueOf(additionalInfo.getMCC()));
                    mncPreference.setSummary(String.valueOf(additionalInfo.getMNC()));
                } else {
                    rootPreference.removePreference(radioCategory);
                }
        }
        
        // [sy_wang] 20130905, Change parameter titleId(int) to title(String) for the case that combine 2 items to set title
        private Intent genPreviewIntent(Activity activity, String title, String text) {
            Intent previewInfoIntent = new Intent(activity, PreviewInfoActivity.class);
            previewInfoIntent.putExtras(activity.getIntent());
            previewInfoIntent.putExtra("title", title);
            previewInfoIntent.putExtra("text", text);
            return previewInfoIntent;
        }
    	
        // [sy_wang] 20130909, Set the preference for log file or tombstone when java crash or native crash
        private void setLogFilePreferences(Activity activity, PreferenceCategory category, String key) {
        	Intent intent = activity.getIntent();
        	if(intent == null)
        		return;
        	
        	Preference preference = (Preference) category.findPreference(key);
        	if(preference == null)
        		return;
        	
        	String extraContent = intent.getStringExtra("tombstoneName");
            if(extraContent != null) { // native crash
            	// Change title from "Log file" to "Log file 1"  
            	preference.setTitle(Utils.getLog1Title(activity));
            	preference.setIntent(genPreviewIntent(activity, Utils.getLog1Title(activity), null));
            	
            	// Add a preference "Log file 2" to preview tombstone file
            	Preference tombstonePreference = new Preference(activity);
            	tombstonePreference.setTitle(Utils.getLog2Title(activity));
            	tombstonePreference.setIntent(genPreviewIntent(activity, Utils.getLog2Title(activity), null));
            	category.addPreference(tombstonePreference);
            } else { // java crash
            	preference.setIntent(genPreviewIntent(activity, Utils.getTextString(activity, R.string.system_crash_log), null));
            }
        }
    }
}
