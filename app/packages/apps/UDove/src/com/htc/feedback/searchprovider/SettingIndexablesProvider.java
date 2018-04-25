package com.htc.feedback.searchprovider;

import com.htc.feedback.Log;
import com.htc.feedback.R;
import com.htc.feedback.reportagent.Common;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.Settings;

import static com.htc.feedback.searchprovider.SearchIndexablesContract.INDEXABLES_RAW_COLUMNS;
import static com.htc.feedback.searchprovider.SearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS;
import static com.htc.feedback.searchprovider.SearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS;

public class SettingIndexablesProvider extends SearchIndexablesProvider {
	private static final String TAG = "SettingIndexablesProvider";
	private Context mContext;

	@Override
	public boolean onCreate() {
		mContext = this.getContext();
		return true;
	}
	
	@Override
	public Cursor queryXmlResources(String[] projection) {
		MatrixCursor cursor = new MatrixCursor(INDEXABLES_XML_RES_COLUMNS);
        return cursor;
	}

	@Override
	public Cursor queryRawData(String[] projection) {
		MatrixCursor cursor = new MatrixCursor(INDEXABLES_RAW_COLUMNS);
		
		String reportSetting = Settings.Secure.getString(mContext.getContentResolver(), Common.HTC_ERROR_REPORT_SETTING);
		String showUsage = Settings.Secure.getString(mContext.getContentResolver(), Common.SHOW_HTC_APPLICATION_LOG);
		int bothNetwork = Settings.Secure.getInt(mContext.getContentResolver(), Common.BOTH_NETWORK_OPTION, 1);
		
		if ("1".equals(reportSetting)) {
			if(Common.DEBUG)
				Log.d(TAG, "Report setting is enable, add raw data");
			
			// Error report checkbox
			cursor.addRow(createRawData("error_report_category", mContext.getString(R.string.feedback_error_report_title)));
			cursor.addRow(createRawData("error_report_enable", mContext.getString(R.string.feedback_error_report_setting)));
			cursor.addRow(createRawData("error_report_enable_summary", mContext.getString(R.string.feedback_setting_summary)));
			
			// Error report preference
			cursor.addRow(createRawData("auto_send_preference", mContext.getString(R.string.auto_send_preference_title)));
			String[] autoSendEntries = mContext.getResources().getStringArray(R.array.auto_send_preference_entries);
			if (autoSendEntries != null && autoSendEntries.length >= 2) {
				cursor.addRow(createRawData("auto_send_preference_entry_auto_send", autoSendEntries[0]));
				cursor.addRow(createRawData("auto_send_preference_entry_ask_me", autoSendEntries[1]));
			}
			
			// Usage report checkbox
			if ("1".equals(showUsage)) {
				cursor.addRow(createRawData("application_log_category", mContext.getString(R.string.feedback_application_log_title)));
				cursor.addRow(createRawData("application_log_enable", mContext.getString(R.string.feedback_application_log_setting)));
				cursor.addRow(createRawData("application_log_enable_summary", mContext.getString(R.string.feedback_application_log_summary)));
			}
			
			// Network preference
			cursor.addRow(createRawData("network_preference_category", mContext.getString(R.string.network_preference_title)));
			if ("1".equals(showUsage))
				cursor.addRow(createRawData("network_preference", mContext.getString(R.string.feedback_network_setting)));
			else
				cursor.addRow(createRawData("network_preference", mContext.getString(R.string.feedback_network_setting_error_only)));
			String[] netWorkEntries = mContext.getResources().getStringArray(R.array.network_preference_entries);
			if (netWorkEntries != null && netWorkEntries.length >= 2) {
				if (bothNetwork == 1)
					cursor.addRow(createRawData("network_preference_entry_wifi_or_mobile", netWorkEntries[0]));
				cursor.addRow(createRawData("network_preference_entry_wifi_only", netWorkEntries[1]));
			}
			
			// Note and privacy/learn more link
			String privacyLink = mContext.getString(R.string.privacy_policy);
			String learnMoreLink = mContext.getString(R.string.learn_more);
			String noteStr = String.format(mContext.getString(R.string.setting_note), privacyLink, learnMoreLink);
			cursor.addRow(createRawData("setting_note", noteStr));
		}
		
		return cursor;
	}

	@Override
	public Cursor queryNonIndexableKeys(String[] projection) {
		MatrixCursor cursor = new MatrixCursor(NON_INDEXABLES_KEYS_COLUMNS);
		return cursor;
	}
	
	private Object[] createRawData(String key, String title) {
		SearchIndexableRaw record = new SearchIndexableRaw(this.getContext());
		
		// "un-searchable" keyword
		record.key = key;
		
		// searchable keyword
		record.keywords = record.key;
		
		// searchable keyword
		record.title = title;
		
		// launch the target via the action
		record.intentAction = "com.htc.feedback.REPORT_SETTINGS";
		
		// Tell the SearchEngine to use Settings package to retrieve the Settings about icon
		try {
			Context settingsContext = this.getContext().createPackageContext("com.android.settings", 0);
			if (settingsContext != null) {
				record.intentTargetPackage = settingsContext.getPackageName();
				record.iconResId = settingsContext.getResources().getIdentifier( 
		                "htc_icon_entry_about", "drawable", settingsContext.getPackageName());
			} else {
				Log.d(TAG, "settingsContext is null");
			}
		} catch (Exception e) {
			Log.e(TAG, "Exception when access settingsContext", e);
		}
		
		// mapping [object fields] into [array]
		Object [] data = SearchIndexableRaw.toCursorData(record);
		
		return data;
	}
	
}
