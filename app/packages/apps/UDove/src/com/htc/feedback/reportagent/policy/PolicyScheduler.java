package com.htc.feedback.reportagent.policy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.ReportService;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.policy.ReportPolicy;
import com.htc.feedback.policy.UPolicy;

public class PolicyScheduler {

    private static final String TAG = "PolicyScheduler";
    private static final boolean _DEBUG = Common.DEBUG;
    private static PolicyScheduler sInstance;
    public static PolicyScheduler getInstance(Context context) {
        if (sInstance == null)
            sInstance = new PolicyScheduler(context);
        return sInstance;
    }
    
    private Context mContext;
    private PolicyScheduler(Context context) {
        mContext = context;
    }
    
    public void scheduleUpdatePolicy(boolean isBootComplete) {
        long alarmBaseline = PolicyPreference.getAlarmBaseline(mContext);
        if ( alarmBaseline <= 0L) { //First launch
            resetAlarm(System.currentTimeMillis());
        } else {
        	if (isBootComplete)
        		resetAlarm(alarmBaseline);
        }
    }
    
    public void resetAlarm(long timeBase) {
        
        long freq = getPolicyUpdateFrequency();
        
        if ( Common.MINUTE_TO_MILLISECONDS >= freq  ) {
            if (_DEBUG) Log.i(TAG, "resetAlarm()", "do not reset alarm due to freq is " + freq);
            return;
        }
        //[REX] Move PendingIntent from constructor to here in order to avoid getting PendingIntent when event occurs. 2012/12/7
        Intent intent = new Intent(ReportService.ACTION_POLICY_ALARM);
        intent.setClass(mContext, ReportService.class);
        PendingIntent alarmIntent = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);//Use FLAG_UPDATE_CURRENT for getting original PendingIntent if it exist.
        
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        am.cancel(alarmIntent);
        am.set(AlarmManager.RTC, timeBase+freq, alarmIntent);
        
        if (_DEBUG) Log.i(TAG, "resetAlarm()", "reset alarm to " + ((timeBase+freq-System.currentTimeMillis())/Common.SECOND_TO_MILLISECONDS) + " seconds later as timebase "+ timeBase);
        
        PolicyPreference.setAlarmBaseline(mContext, timeBase);
    }
    
    private long getPolicyUpdateFrequency() {
        
        UPolicy policy = ReportPolicy.getInstance(mContext);
        String freqStr = policy.getValue(Common.CATEGORY_POLICY, Common.KEY_POLICY_FREQ);
        long freq = -1L;
        
        try {
            freq = Long.parseLong(freqStr) * Common.HOUR_TO_MILLISECONDS;
            /*if ( HtcBuildFlag.Htc_DEBUG_flag && Common.FAKE_POLICY_ALARM ) {
                freq = Long.parseLong(freqStr) * Common.SECOND_TO_MILLISECONDS;
            }*/
            if (_DEBUG) Log.i(TAG, "getPolicyUpdateFrequency()", "freq is " + (freq/Common.SECOND_TO_MILLISECONDS) + " seconds");
        } catch (Exception e) {
            Log.e(TAG, "Exception happen during parsing updateFrequency, freqStr=" + freqStr);
        }
        
        return freq;
    }
}
