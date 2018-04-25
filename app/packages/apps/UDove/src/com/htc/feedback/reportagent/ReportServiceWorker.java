package com.htc.feedback.reportagent;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.htc.feedback.policy.PolicyStore;
import com.htc.feedback.reportagent.receiver.PowerConnectedReceiver;
import com.htc.feedback.reportagent.s3.BugReportService;
import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.EngineeringPreference;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.util.Utils;
import com.htc.feedback.reportagent.util.ReportConfig;
import com.htc.feedback.reportagent.budget.BudgetManager;
import com.htc.feedback.reportagent.cache.EntryFile;
import com.htc.feedback.reportagent.cache.LogCacheManager;
import com.htc.feedback.reportagent.policy.PolicyManager;
import com.htc.feedback.reportagent.pomelo.ReportManager;
import com.htc.feedback.reportagent.pomelo.ReportPreference;
import com.htc.feedback.reportagent.util.Security;
import com.htc.feedback.reportagent.policy.PolicyScheduler;
import com.htc.xps.pomelo.andrlib.LogLib;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.ComponentName;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.app.Service;
import java.io.File;

public class ReportServiceWorker {
	
	private final static String TAG = "ReportServiceWorker";
	private final static boolean _DEBUG = Common.DEBUG;
	private final static int MSG_ID_STOPSELF = 101;
	private final static int TIME_DELAY_SHUTDOWN = 5000;
	
	private Context mContext;
	private BudgetManager mBudgetManager;
	private BugReportService mHtcBugReport;
	private Service mService;
	private PolicyStore mPolicyStore;
	
    public ReportServiceWorker(Service service) {
    	mContext = service.getApplicationContext();
    	mService = service;
        mPolicyStore = PolicyStore.getInstance(mService.getApplicationContext());
        installSecurityProvider();
    }

	protected void onHandleIntent(Intent intent) {
		if ( null == intent ) {
			Log.d(TAG, "[onHandleIntent], intent is null");
			return;
		}
		String action = intent.getAction();
		if ( null == action || 0 == action.length() ) {
			Log.d(TAG, "[onHandleIntent], abnormal action");
			return;
		}
		
		Log.d(TAG, "onHandleIntent()", "intent is " + action);
		
		if ( null == mBudgetManager )
			mBudgetManager = new BudgetManager(mContext);

		if( null == mHtcBugReport )
			mHtcBugReport = new BugReportService(mService);

		// check rom version, SN and ro.lb 
		if(!Security.isSecureROM())
			return;
		
		if ( ReportService.ACTION_UPLOAD_ERROR_LOG.equals(action)) {
			
			//if is[Eng],upload it.
			if(Utils.isS3UploaderEnabled()){
				if(_DEBUG){
					Log.d(TAG,"from dropbox,"+intent.getBooleanExtra("fromDropBox",false)+"(if true means from regular upload)");
					Log.d(TAG,"the tag "+intent.getStringExtra("tag"));
				}
				mHtcBugReport.onUploadBugReport(intent); 
			}
			
            //For pomelo
			new ReportManager(mContext, mBudgetManager).onUpload(intent);
			
			//For deleting the original file which is included in the incoming intent.
			//Rex, 2012/11/1
			String fileName = intent.getStringExtra("file");
			if(!TextUtils.isEmpty(fileName)){				
				File file = new File(fileName);
				if(file.exists()){
					Log.d(TAG, "[onHandleIntent] there is a file: "+fileName+" need to be deleted.");
					file.delete();
				}
			}
		}
		else if(ReportService.ACTION_UPLOAD_UB_LOG.equals(action)){
            if(Utils.isS3UploaderEnabled()){
                mHtcBugReport.onUploadBugReport(intent); 
            }
		}
        else if(Intent.ACTION_POWER_CONNECTED.equals(action)){
        	long lastUpload=ReportPreference.getLastUPUpload(mContext);
        	if(System.currentTimeMillis()-lastUpload>=3*Common.HOUR_TO_MINUTES*Common.MINUTE_TO_MILLISECONDS  && Utils.isNetworkAllowed(mContext)) {
        			ReportPreference.setLastUPUpload(mContext);
					Log.d(TAG,"Power is changed from not connected to connected");				
					// Upload to S3
					if(Utils.isS3UploaderEnabled()){
						mHtcBugReport.onConnectivityChange();
					}
					//Upload to Pomelo
					//TODO: Check user profiling cached behaviors
					if(Utils.isUserProfilingSettingEnabled(mContext)|| Utils.isErrorReportSettingEnabled(mContext)) // Check the policy
						// Resume the Pomelo Log by calling onConnectivityChanged method
						new ReportManager(mContext, mBudgetManager).onPowerConnectedChanged(); // If resume is done, no cached file
        	}		
        } else if ( ReportService.ACTION_BOOT_COMPLETE.equals(action) ) {
			Log.d(TAG,"on boot complete event ");
			if(Utils.isS3UploaderEnabled())
				mHtcBugReport.updateDeviceInfoPreference(); //REX, Update device information if device info preference doesn't exist. Only for engineering ROM. 2012/01/08
			PolicyScheduler.getInstance(mService.getApplicationContext()).scheduleUpdatePolicy(true);
		} else if (ReportService.ACTION_POLICY_ALARM.equals(action)) {
            PolicyManager.getInstance(mService.getApplicationContext(), mBudgetManager).downloadPolicy();
		} else if ( ReportService.ACTION_TELLHTC_SETTING_CHANGE.equals(action) ) {
			PolicyManager.getInstance(mService.getApplicationContext(), mBudgetManager).downloadPolicy();
		} else if(ReportService.ACTION_SETUP_WIZARD_FINISHED.equals(action)) {
			PolicyManager.getInstance(mService.getApplicationContext(), mBudgetManager).downloadPolicy();
		}
		
		//check cached status of S3 and Pemelo,1).both no cached:(unregister), 2)one of them has cached(register)
		EntryFile [] fileList = LogCacheManager.getInstance().getFiles(mContext);
		ComponentName receiver = new ComponentName(mContext, PowerConnectedReceiver.class); 
		PackageManager pm = mContext.getPackageManager();
		if(mHtcBugReport.getFilesCount() == 0 && (fileList==null||fileList.length == 0)){
			pm.setComponentEnabledSetting(receiver,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
			Log.v(TAG,"unregister the PowerConnected Listener");
		}else{
			pm.setComponentEnabledSetting(receiver,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
			Log.v(TAG,"register the PowerConnected Listener");
		}
		
        if(Utils.isS3UploaderEnabled())
            mHtcBugReport.updateDeviceInfoPreference(); //REX, Update device information if device info preference doesn't exist. Only for engineering ROM. 2012/01/08
	}
	
    //Install dynamic security provider from google play service.
    private void installSecurityProvider() {
        try {
            ProviderInstaller.installIfNeeded(mService.getApplicationContext());
            if (_DEBUG) 
                Log.d(TAG, "Install dynamic security provider.");
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, "Failed to install dynamic security provider.", e);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "Failed to install dynamic security provider.", e);
        } catch (Exception e) {
            Log.e(TAG, "Failed to install dynamic security provider.", e);
        }
    }

}
