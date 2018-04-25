package com.htc.feedback.reportagent.s3;

import com.htc.feedback.policy.UPolicy;
import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.Log;
import android.content.Context;

public final class S3Policy {
	private S3Policy() {}

	private final static String TAG = "BugReportService";
	private final static boolean _DEBUG = Common.DEBUG;

	public static boolean canLogToS3(Context context, String tag) {
	    if(Common.STR_HTC_UB.equals(tag)) {
            boolean canLogUB = UPolicy.canLogUB("com.htc.feedback", tag, context);
            Log.v(TAG,"[canLogToS3][UB] " + tag + " pass: " + canLogUB);
            return canLogUB;
	    }
	    else {
        	boolean canLogErrorReport = UPolicy.canLogErrorReport(UPolicy.APPID_ERROR_REPORT, tag, context);
            Log.v(TAG,"[canLogToS3] " + tag + " pass: " + canLogErrorReport);
            return canLogErrorReport;
	    }
	}
}
