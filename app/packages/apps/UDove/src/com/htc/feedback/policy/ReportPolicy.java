package com.htc.feedback.policy;

import android.content.Context;

import com.htc.feedback.reportagent.Common;

public class ReportPolicy {
    
    private static UPolicy sPolicy;

    public static UPolicy getInstance(Context context) { 
        if (sPolicy == null)
            sPolicy = new UPolicy(Common.APPID_REPORT_AGENT, context);    
        return sPolicy;
    }
}
