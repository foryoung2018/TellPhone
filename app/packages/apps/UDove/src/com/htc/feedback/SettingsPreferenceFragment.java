package com.htc.feedback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.htc.feedback.reportagent.Common;

public class SettingsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{
	private static final String TAG = "SettingsPreferenceFragment";
	private static final boolean LOG = Common.DEBUG;
    private static final String KEY_ERROR_REPORT_ENABLE = "error_report_enable";
    private static final String KEY_AUTO_SEND = "auto_send_preference";
    private static final String KEY_APPLICATION_LOG_ENABLE = "application_log_enable";
    private static final String KEY_PREFER_NETWORK = "network_preference";
    private static final int REQUEST_CODE_NETWORK = 1001;
    private static final int REQUEST_CODE_REPORT = 1002;

    private CheckBoxPreference mErrorReportPreference; // SEND_HTC_ERROR_REPORT
    private ListPreference mAutoSendPreference; // HTC_ERROR_REPORT_AUTO_SEND
    private CheckBoxPreference mUsageReportPreference; // SEND_HTC_APPLICATION_LOG
    private ListPreference mPreferNetworkPreference; // HTC_ERROR_REPORT_PREFER_NETWORK
    private Preference mWifiOnlyPreference;
    private MyPreference mPrivacyLinkPreference;
    private int mshowUsage;
    private Activity mActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (LOG)
            Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.feedback_settings);
		
		mActivity = getActivity();
		
		mErrorReportPreference = (CheckBoxPreference) getPreferenceScreen().findPreference(KEY_ERROR_REPORT_ENABLE);
		mAutoSendPreference = (ListPreference) getPreferenceScreen().findPreference(KEY_AUTO_SEND);
        mPreferNetworkPreference = (ListPreference) getPreferenceScreen().findPreference(KEY_PREFER_NETWORK);
        mUsageReportPreference = (CheckBoxPreference) getPreferenceScreen().findPreference(KEY_APPLICATION_LOG_ENABLE);
        	        
        mshowUsage = Settings.Secure.getInt(mActivity.getContentResolver(), Common.SHOW_HTC_APPLICATION_LOG, 1);
        if(mshowUsage != 1) {
        	getPreferenceScreen().removePreference(getPreferenceScreen().findPreference("application_log_category"));
        	mPreferNetworkPreference.setTitle(R.string.feedback_network_setting_error_only);
        }

        mPrivacyLinkPreference = new MyPreference(mActivity);
        mPrivacyLinkPreference.setLayoutResource(R.layout.specific_feedback_toslink);
        mPrivacyLinkPreference.setSelectable(false);
        getPreferenceScreen().addPreference(mPrivacyLinkPreference);
        
        if(Settings.Secure.getInt(mActivity.getContentResolver(), Common.BOTH_NETWORK_OPTION, 1) != 1){
        	PreferenceCategory parent = (PreferenceCategory) getPreferenceScreen().findPreference("network_preference_category");
        	if(parent != null){
        		parent.removePreference(mPreferNetworkPreference);
        		String[] entries = getResources().getStringArray(R.array.network_preference_entries);
                mWifiOnlyPreference = new Preference(mActivity);
                mWifiOnlyPreference.setTitle(mshowUsage!=1 ? R.string.feedback_network_setting_error_only : R.string.feedback_network_setting);
                mWifiOnlyPreference.setSummary(entries[1]);
                mWifiOnlyPreference.setSelectable(false);
        		parent.addPreference(mWifiOnlyPreference);
        	}
        }
		initPreference();
	}
	
	private boolean setValue(String setting, String value) {
        try {
            if (Settings.Secure.putString(mActivity.getContentResolver(), setting, value))
                return true;
            else {
                Log.e(TAG, "can't set value in setting");
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "error in setting preference", e);
            return false;
        }
    }
	
	private DialogInterface.OnClickListener mErrorReporteDialogClickListener = new DialogInterface.OnClickListener() {
        // 0: disable, 1: enable
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) { // Turn off
                setValue(Common.SEND_HTC_ERROR_REPORT, "0");
            } else if (which == DialogInterface.BUTTON_NEGATIVE) { // Turn on
                setValue(Common.SEND_HTC_ERROR_REPORT, "1");
                mErrorReportPreference.setChecked(true);
            }
            lockUI();
        }
    };

    private DialogInterface.OnClickListener mPreferNetworkDialogClickListener = new DialogInterface.OnClickListener() {
        // 0: all, 1: Wifi only
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) { // 0
                setValue(Common.HTC_ERROR_REPORT_PREFER_NETWORK, "0");
                mPreferNetworkPreference.setValue("0");
                mPreferNetworkPreference.setSummary(mPreferNetworkPreference.getEntry());
            }
        }
    };
    
    private View.OnClickListener mPrivacyLinkClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent privacyIntent = new Intent(mActivity, PrivacyPageActivity.class);
	        startActivity(privacyIntent);
		}
	};
    
	private void showDialogWithID(int id) {
    	if (LOG)
            Log.d(TAG, "showDialogWithID "+ id);
    	DialogFragment newFragment = MyDialogFragment.newInstance(id, mPreferNetworkDialogClickListener,mErrorReporteDialogClickListener);
    	newFragment.show(getFragmentManager(), "Dialog");
    }
    
    private void initPreference() {
    	if (LOG)
            Log.d(TAG, "initPreference");
        ContentResolver cr = mActivity.getContentResolver();

        boolean enableReport = false;
        String preferNetwork = "0";
        String autoSend = "0";
        boolean enableUsageReport = false;
        try {
            enableReport = Settings.Secure.getInt(cr, Common.SEND_HTC_ERROR_REPORT, 0) == 1;
            preferNetwork = Settings.Secure.getString(cr, Common.HTC_ERROR_REPORT_PREFER_NETWORK);
            autoSend = Settings.Secure.getString(cr, Common.HTC_ERROR_REPORT_AUTO_SEND);
            enableUsageReport = Settings.Secure.getInt(cr, Common.SEND_HTC_APPLICATION_LOG, 0) == 1;

            preferNetwork = (preferNetwork == null) ? "0" : preferNetwork;
            autoSend = (autoSend == null) ? "0" : autoSend;
        } catch (Exception e) {
            Log.e(TAG, "error in initPreference", e);
            enableReport = false;
            enableUsageReport = false;
            preferNetwork = "0";
            autoSend = "0";
        }
        if (LOG)
            Log.d(TAG, "reportEnabled=" + enableReport + " prefNetwork=" + preferNetwork + " autoSend=" + autoSend);

        mErrorReportPreference.setChecked(enableReport);
        mUsageReportPreference.setChecked(enableUsageReport);

        mAutoSendPreference.setValue(autoSend);
        mAutoSendPreference.setSummary(mAutoSendPreference.getEntry());
        mAutoSendPreference.setOnPreferenceChangeListener(this);

        mPreferNetworkPreference.setValue(preferNetwork);
        mPreferNetworkPreference.setSummary(mPreferNetworkPreference.getEntry());
        mPreferNetworkPreference.setOnPreferenceChangeListener(this);

        lockUI();
    }
    
    private void lockUI() {
        boolean report = mErrorReportPreference.isChecked();
        boolean usageReport = (mUsageReportPreference == null) ? false : mUsageReportPreference.isChecked();
        mAutoSendPreference.setEnabled(report);
        mAutoSendPreference.setSelectable(report);
        mPreferNetworkPreference.setEnabled(report || usageReport);
        mPreferNetworkPreference.setSelectable(report || usageReport);
    }
    
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int value = Integer.parseInt((String) newValue);
        if (LOG)
            Log.d(TAG, "onPreferenceChange newValue = " + value);
        ListPreference listPreference = (ListPreference) preference;
        // do nothing if the value is not changed.
        if (listPreference.getValue().equals((String) newValue)) {
            return false;
        }
        // check the value and pop up warning dialog
        if (preference == mPreferNetworkPreference) { // 0: all, 1: Wifi only
            // not need to show data warning message for TMUS projects (SEPC
            // 0.73 update to still show data warning for TMUS)
            if (value == 0) {
            	showDialogWithID(REQUEST_CODE_NETWORK);
                return false;
            } else {
                setValue(Common.HTC_ERROR_REPORT_PREFER_NETWORK, (String) newValue);
                CharSequence[] entries = mPreferNetworkPreference.getEntries();
                mPreferNetworkPreference.setSummary(entries[mPreferNetworkPreference
                        .findIndexOfValue((String) newValue)]);
                return true;
            }
        } else if (preference == mAutoSendPreference) { // 1: enable, 0: disable
            if (value == 0) {
                setValue(Common.HTC_ERROR_REPORT_AUTO_SEND, "0");
                CharSequence[] entries = mAutoSendPreference.getEntries();
                mAutoSendPreference.setSummary(entries[mAutoSendPreference.findIndexOfValue("0")]);
                return true;
            } else {
                setValue(Common.HTC_ERROR_REPORT_AUTO_SEND, "1");
                mAutoSendPreference.setValue("1");
                mAutoSendPreference.setSummary(mAutoSendPreference.getEntry());
            }
        }
        return false;
    }
    
    @Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		// TODO Auto-generated method stub
		if (LOG)
            Log.d(TAG, "onPreferenceTreeClick");
        if (preference == mErrorReportPreference) {
            if (mErrorReportPreference.isChecked()) {
                setValue(Common.SEND_HTC_ERROR_REPORT, "1");
            } else {
            	showDialogWithID(REQUEST_CODE_REPORT);
            }
        } else if (preference == mUsageReportPreference) {
            if (mUsageReportPreference.isChecked()) {
                setValue(Common.SEND_HTC_APPLICATION_LOG, "1");
            } else {
                setValue(Common.SEND_HTC_APPLICATION_LOG, "0");
            }
        }
        lockUI();
        return false;
	}
    
    public static class MyDialogFragment extends DialogFragment{
    	
    	DialogInterface.OnClickListener preferNetworkDialogClickListener;
    	DialogInterface.OnClickListener errorReporteDialogClickListener;
    	 	
    	public static MyDialogFragment newInstance(int title, 
    										       DialogInterface.OnClickListener preferNetworkListener,
    			                                   DialogInterface.OnClickListener errorReporteListener) {
    		if (LOG)
	            Log.d(TAG, "[MyDialogFragment] newInstance");
            MyDialogFragment myDialogFragment = new MyDialogFragment();
            myDialogFragment.setConfiguration(preferNetworkListener, errorReporteListener);
            Bundle bundle = new Bundle();  
            bundle.putInt("title", title);
            myDialogFragment.setArguments(bundle);
            return myDialogFragment;  
        }
    	
    	private void setConfiguration(DialogInterface.OnClickListener preferNetworkListener,
    								  DialogInterface.OnClickListener errorReporteListener){
    		preferNetworkDialogClickListener = preferNetworkListener;
    		errorReporteDialogClickListener = errorReporteListener;
    	}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			int id = getArguments().getInt("title");
			if (LOG)
	            Log.d(TAG, "[MyDialogFragment] onCreateDialog " + id);
			
			if(getActivity() == null){
				if (LOG)
		            Log.d(TAG, "[MyDialogFragment] onCreateDialog: activity is null!");
			}
			if(preferNetworkDialogClickListener == null || errorReporteDialogClickListener == null){
				if (LOG)
					Log.d(TAG, "[MyDialogFragment] onCreateDialog: listener is null!");
			}
			
	        if (id == REQUEST_CODE_NETWORK) {
	            AlertDialog dialog = new AlertDialog.Builder(getActivity()).setCancelable(false)
	                    .setMessage(getResources().getString(R.string.network_preference_message))
	                    .setTitle(R.string.data_fee_warning)
	                    .setPositiveButton(R.string.btn_yes, preferNetworkDialogClickListener)
	                    .setNegativeButton(R.string.btn_no, preferNetworkDialogClickListener).create();
	            return dialog;
	        } else if (id == REQUEST_CODE_REPORT) {
	            AlertDialog dialog = new AlertDialog.Builder(getActivity()).setCancelable(false)
	                    .setMessage(getResources().getString(R.string.report_message_turn_off))
	                    .setTitle(R.string.feedback_error_report_title)
	                    .setPositiveButton(R.string.report_btn_turn_off, errorReporteDialogClickListener)
	                    .setNegativeButton(R.string.btn_feedback_cancel, errorReporteDialogClickListener).create();
	            return dialog;
	        } else {
	            return null;
	        }		
		}      	    	
    }
    
    private class MyPreference extends Preference{

        public MyPreference(Context context) {
            super(context);
        }

        protected void onBindView(View view) {
        	TextView note = (TextView) view.findViewById(R.id.setting_note);
        	note.setText(getNoteSpanStr(mActivity, getContext()));
        	note.setMovementMethod(LinkMovementMethod.getInstance());
            
            //Warning text in red, only be shown on testing device.
            if (ReportConfig.isShippingRom()) {
            	TextView textView = (TextView) view.findViewById(R.id.warning_text);
                if (textView != null) {
                    if (textView.getVisibility() != View.GONE) {
                        textView.setVisibility(View.GONE);
                    }
                }
            }
        }
    } 
    
    private static class PrivacyClickableSpan extends ClickableSpan {
    	private Activity activity;
    	private Context context;
    	private String privacyType;

        public PrivacyClickableSpan(Activity act, Context ctx, String type) {
            if (act == null || ctx == null)
                throw new IllegalArgumentException("activity or context is null.");
            
            activity = act;
            context = ctx;
            privacyType = type;
        }

        /**
         * Remove underline but ensure link color.
         */
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ds.linkColor);// Should still set android:textColorLink="@color/all_hyperlink_color" in layout or color is wrong
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
        	if(LOG)
            	Log.d(TAG, "PrivacyClickableSpan onClick");

            // Show privacy policy
        	Intent policyIntent = new Intent();
        	if (PrivacyPageActivity.TYPE_LEARN_MORE.equals(privacyType))
        		policyIntent.setComponent(new ComponentName("com.htc.feedback", "com.htc.feedback.LearnMoreActivity"));
        	else
        		policyIntent.setComponent(new ComponentName("com.htc.feedback", "com.htc.feedback.PrivacyPageActivity"));
        	policyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	context.startActivity(policyIntent);
        }
    }
    
    public static SpannableString getNoteSpanStr(Activity activity, Context context) {
    	// Compose string.
        String privacyLink = context.getString(R.string.privacy_policy);
        String learnMoreLink = context.getString(R.string.learn_more);
        String noteStr = String.format(context.getString(R.string.setting_note), privacyLink, learnMoreLink);
        SpannableString spanStr = new SpannableString(noteStr);

        // Find privacy link string start/end positions.
        int privacyStartPos = noteStr.indexOf(privacyLink);
        int privacyEndPos = privacyStartPos + privacyLink.length();

        // Set ClickableSpan.
        if (privacyStartPos > 0 && context != null)
            spanStr.setSpan(new PrivacyClickableSpan(activity, context, PrivacyPageActivity.TYPE_PRIVACY_POLICY), privacyStartPos,
                    privacyEndPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        // Find learn more link string start/end positions.
        int learnMoreStartPos = noteStr.indexOf(learnMoreLink);
        int learnMoreEndPos = learnMoreStartPos + learnMoreLink.length();
        // Set ClickableSpan.
        if (learnMoreStartPos > 0 && context != null)
            spanStr.setSpan(new PrivacyClickableSpan(activity, context, PrivacyPageActivity.TYPE_LEARN_MORE), learnMoreStartPos,
            		learnMoreEndPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        return spanStr;
    }
        
}
