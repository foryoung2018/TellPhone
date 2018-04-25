package com.htc.feedback.reportagent;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import com.htc.feedback.reportagent.util.Log;

public class ReportService extends IntentService {

	private final static String TAG = "ReportService";
	private final static boolean _DEBUG = Common.DEBUG;
	
	// upload log
	public final static String ACTION_UPLOAD_ERROR_LOG = "com.htc.intent.action.BUGREPORT";
	public final static String ACTION_UPLOAD_UB_LOG = "com.htc.intent.action.UDOVE_UPLOAD_UB";
	
	// Common
	public final static String ACTION_BOOT_COMPLETE = "com.htc.reportagent.action.BOOT_COMPLETE";
	
	// Broadcast from UBLS
	public final static String ACTION_TELLHTC_SETTING_CHANGE = "com.htc.intent.action.TELLHTC_SETTING_CHANGE";
    
    // setup wizard finish
    public final static String ACTION_SETUP_WIZARD_FINISHED = "com.htc.intent.action.SETUP_WIZARD_FINISHED";

	// for modem crash, it only can pass the broadcast without permission (Pitt, 2012/05/21) 
	public final static String ACTION_DEVELOPING_REPORT = "com.htc.intent.action.DEVELOPING_REPORT";
	
	// for enable send log of engineering ROM to NC-POMELO (Default is disable)
	public final static String ACTION_ENABLE_NCPOMELO_REPORT = "com.htc.intent.action.ENABLE_NCPOMELO_REPORT";
	
	/**
     * Action for policy alarm comes. 
     */
    public static final String ACTION_POLICY_ALARM = "com.htc.feedback.action.POLICY_ALARM";
	
	private ReportServiceWorker mReportServiceWorker;
	
    public ReportService() {
        super("ReportService");
        this.setIntentRedelivery(false);
    }
    
    @Override
    protected void onHandleIntent(Intent intent) {
        try {
			if(mReportServiceWorker == null)
			    mReportServiceWorker = new ReportServiceWorker(this);
			mReportServiceWorker.onHandleIntent(intent);
		} catch (Exception e) {
			Log.e(TAG, "Exception in onHandleIntent", e);
		}
    }
    
	public static boolean perform(Context context, String action) {

		if ( null == action || 0 == action.length() )
			return false;
		
		Intent intent = new Intent(action);
		intent.setClass(context, ReportService.class);
		context.startService(intent);
		return true;
	}

}

