package com.htc.feedback.framework;

import android.content.ContentResolver;
import android.os.Build;
import android.provider.Settings;

import com.htc.feedback.Log;
import com.htc.feedback.ReportConfig;
import com.htc.feedback.reportagent.util.ReflectionUtil;
import com.htc.feedback.reportagent.Common;

public class ErrorReport {
	private final static String TAG = "ErrorReport";
	private final static boolean DEBUG = Common.DEBUG;
	
	public static boolean isAutoSend(ContentResolver cr) {
		boolean autoSend = Settings.Secure.getInt(cr, Common.HTC_ERROR_REPORT_AUTO_SEND, 0) == 1;
		
		if(autoSend && !ReportConfig.isShippingRom()) {
			// sy_wang, 20140416, If RCMS ROM is rooted, don't let it auto send to prevent from uploading unnecessary error.
	    	boolean isRooted = "0".equals(ReflectionUtil.get("ro.secure", "1"));
	        boolean isDashboardBuild = ReflectionUtil.get("ro.build.description", "unknown").startsWith("0.1.0.0");
	        if (isRooted && !isDashboardBuild) {
	        	if (DEBUG)
	        		Log.d(TAG, "Disable auto send by rooted RCMS ROM");
	        	return false;
	        }
	        
	        // sy_wang, 20140514, If use ART as android runtime in K443 or earlier platform, disable autosend. ART will be default runtime after K443
	        boolean isRuntimeDefaultDalvik = Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
	        boolean isUsingDalvik = "libdvm.so".equals(ReflectionUtil.get("persist.sys.dalvik.vm.lib", "unknown"));
	        if (isRuntimeDefaultDalvik && !isUsingDalvik) {
	        	if (DEBUG)
	        		Log.d(TAG, "Disable auto send by android runtime is not Dalvik");
	        	return false;
	        }
		}
		
		return autoSend;
	}
}
