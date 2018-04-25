package com.htc.feedback.reportagent.budget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.ReportService;
import com.htc.feedback.policy.ReportPolicy;
//import com.htc.feedback.reportagent.ReportUPolicy;
import com.htc.feedback.reportagent.util.Utils;
import com.htc.feedback.reportagent.budget.network.Network;
import com.htc.feedback.reportagent.budget.network.MobileNetwork;
import com.htc.feedback.reportagent.budget.network.OtherNetwork;
import com.htc.feedback.reportagent.budget.network.AllNetwork;
import com.htc.feedback.policy.UPolicy;

public class BudgetManager {
	
	public enum NetworkType {
		TYPE_MOBILE,
		TYPE_OTHER,
		TYPE_NONE;
		
		public static String toString(NetworkType type) {
			switch(type) {
			case TYPE_MOBILE: return "MOBILE";
			case TYPE_OTHER:  return "OTHER";
			case TYPE_NONE:   return "NONE";
			}
			return null;
		}
	}

	private final static String TAG = "BudgetManager";
	private final static boolean _DEBUG = Common.DEBUG;
	
	private Context mContext;
	private UPolicy mPolicy;
	private BudgetPreference mPref;
	private Network mMobileNetwork, mOtherNetwork, mTotalNetwork;
	
	public BudgetManager(Context context) {
		mContext = context;
		mPolicy = ReportPolicy.getInstance(mContext);
		mPref = new BudgetPreference(mContext);
		mMobileNetwork = new MobileNetwork(mPref);
		mOtherNetwork = new OtherNetwork(mPref);
		mTotalNetwork = new AllNetwork(mPref);
	}

	public void updateAppUsage(long DLSize, long ULSize, String logcatTag) {
		
		updateAppUsage(getCurrentNetworkType(), DLSize, ULSize, logcatTag);
	}
	
	private void updateAppUsage(NetworkType type, long DLSize, long ULSize, String logcatTag) {
		
		if ( NetworkType.TYPE_NONE == type ) return;
		
		mTotalNetwork.appUsageUpdated(DLSize, ULSize);
		if ( NetworkType.TYPE_MOBILE == type ) {
			mMobileNetwork.appUsageUpdated(DLSize, ULSize);
		} else {
			mOtherNetwork.appUsageUpdated(DLSize, ULSize);
		}

		mPref.flush();
	}

	public boolean isAvailableByCurrentNetwork(long expectedDLSize, long expectedULSize) {

		NetworkType type = getCurrentNetworkType();
		
		if (_DEBUG) Log.i(TAG, "isAvailableByCurrentNetwork()", "is " + NetworkType.toString(type));
		
		return isAvailable(type, expectedDLSize, expectedULSize);
	}

	public boolean isAvailableByNoncurrentNetwork(long expectedDLSize, long expectedULSize) {
		
		NetworkType type = getCurrentNetworkType();
		
		if ( NetworkType.TYPE_NONE == type ) {
			boolean result = isAvailable(NetworkType.TYPE_MOBILE, expectedDLSize, expectedULSize);
			result |= isAvailable(NetworkType.TYPE_OTHER, expectedDLSize, expectedULSize);
			return result;
		}
		
		if ( NetworkType.TYPE_MOBILE == type )
			type = NetworkType.TYPE_OTHER;
		else
			type = NetworkType.TYPE_MOBILE;

		if (_DEBUG) Log.i(TAG, "isAvailableByNoncurrentNetwork()", "is " + NetworkType.toString(type));
		
		return isAvailable(type, expectedDLSize, expectedULSize);
	}
	
	private boolean isAvailable(NetworkType type, long expectedDLSize, long expectedULSize) {

	    // Reset base time to current time if current time is over period of budget (2014-05-08)
	    resetAllNetworkBudgetInNeed();
		
		boolean result = true;

		if ( NetworkType.TYPE_MOBILE == type ) {
			result = isSpecficNetworkAvailable( Common.KEY_BUDGET_PREFIX_MOBILE, mMobileNetwork, expectedDLSize, expectedULSize );
		} else {
			result = isSpecficNetworkAvailable( Common.KEY_BUDGET_PREFIX_OTHER, mOtherNetwork, expectedDLSize, expectedULSize );
		}
		result &= isSpecficNetworkAvailable( Common.KEY_BUDGET_PREFIX_ALL, mTotalNetwork, expectedDLSize, expectedULSize );

		if (_DEBUG) Log.i(TAG, "isAvailable()", "type=" + NetworkType.toString(type) + " result=" + result + " DL=" + expectedDLSize + " UL=" + expectedULSize);
		
		return result;
		
	}
	
	private boolean isSpecficNetworkAvailable( String keyPrefix, Network network, long expectedDLSize, long expectedULSize) {
		
	    if (_DEBUG) Log.i(TAG, "isSpecficNetworkAvailable", "current usage: "+mPref.toString());
	    
		long period = getPolicyLongValue(null, Common.KEY_BUDGET_PERIOD, -1L);
		if ( 0L > period ) {
			if (_DEBUG) Log.i(TAG, "_isAvailable()", keyPrefix + Common.KEY_BUDGET_PERIOD + " unlimited, period is " + period);
			return true; // unlimited
		}

		long totalLimit = getPolicyLongValue(keyPrefix, Common.KEY_BUDGET_SUFFIX_TOTAL, -1L);
		long ULLimit = getPolicyLongValue(keyPrefix, Common.KEY_BUDGET_SUFFIX_UL, -1L);
		long DLLimit = getPolicyLongValue(keyPrefix, Common.KEY_BUDGET_SUFFIX_DL, -1L);

		String str = mPolicy.getValue(Common.CATEGORY_BUDGET, keyPrefix + Common.KEY_BUDGET_SUFFIX_CALC_UNIT);
		if ( null == str || 0 == str.length() ) {
			str = Common.VALUE_BUDGET_TYPE_ABS_MB;
		}
		
		boolean isAvailable = false;
		if ( Common.VALUE_BUDGET_TYPE_PERCENTAGE.equals(str) ) {
		    isAvailable = network.isAvailableByPercentage(totalLimit, expectedDLSize, DLLimit, expectedULSize, ULLimit);
            if (_DEBUG) Log.i(TAG, "isAvailableByPercentage", "isAvailable="+isAvailable+", totalLimit=" + totalLimit + ", DLLimit="
                        + DLLimit + ", ULLimit=" + ULLimit + ", file DL size=" + expectedDLSize + ", file UL size=" + expectedULSize);
		    return isAvailable;
		} else { // default we assume it as Common.VALUE_BUDGET_TYPE_ABS_MB
			if ( _DEBUG && Common.FAKE_BUDGET_SIZE ) {
                if ( -1L != totalLimit ) totalLimit *= Common.KILOBYTE_TO_BYTES;
                if ( -1L != ULLimit ) ULLimit *= Common.KILOBYTE_TO_BYTES;
                if ( -1L != DLLimit ) DLLimit *= Common.KILOBYTE_TO_BYTES;
			} else {
				if ( -1L != totalLimit ) totalLimit *= Common.MEGABYTE_TO_BYTES;
				if ( -1L != ULLimit ) ULLimit *= Common.MEGABYTE_TO_BYTES;
				if ( -1L != DLLimit ) DLLimit *= Common.MEGABYTE_TO_BYTES;
			}
			isAvailable = network.isAvailableByBytes(totalLimit, expectedDLSize, DLLimit, expectedULSize, ULLimit);
            if (_DEBUG) Log.i(TAG, "isAvailableByBytes", "type="+network.getTAG()+", isAvailable="+isAvailable+", totalLimit=" + totalLimit + ", DLLimit="
                    + DLLimit + ", ULLimit=" + ULLimit + ", file DL size=" + expectedDLSize + ", file UL size=" + expectedULSize);
			return isAvailable;
		}
	}
	
	private long getPolicyLongValue(String keyPrefix, String keySuffix, long defaultValue) {
		
		long value = defaultValue;
		String key = "";
		if ( null != keyPrefix && 0 != keyPrefix.length() ) key += keyPrefix;
		if ( null != keySuffix && 0 != keySuffix.length() ) key += keySuffix;
		
		String str = mPolicy.getValue(Common.CATEGORY_BUDGET, key);
		if ( null != str && 0 != str.length() ) {
			try {
				value = Long.parseLong(str);
			} catch (Exception e) {
				Log.e(TAG, "_getPolicyLongValue()", "exception catched during transfer " + keyPrefix + key + " from string to int");
				e.printStackTrace();
			}
		}
		
		return value;
	}

    /**
     * If current time exceeds the evaluation period, reset all network budget
     * and set current time as base time for next period
     */
	private void resetAllNetworkBudgetInNeed() {
	    
	    // Set current time as base time if it isn't initialized
	    long baseTime = BudgetPreference.getPeriodBaseline(mContext);
	    if(baseTime < 0) {
	        long firstBaseTime = System.currentTimeMillis();
	        BudgetPreference.setPeriodBaseline(mContext, firstBaseTime);
	        baseTime = firstBaseTime;
	        Log.i(TAG, "resetAllNetworkBudgetInNeed", "Set first time baseline of budget period : "+baseTime);
	    }
	    long period = getBudgetPeriodFromPolicy();
        if ( period < 0 )
            return;

        long currentTime = System.currentTimeMillis();
        if(period == 0 || (currentTime - baseTime >= period)) { // period == 0 means check this time only so reset all to make following checking behavior correct
            resetAllNetworkBudget();
            BudgetPreference.setPeriodBaseline(mContext, currentTime);
            Log.i(TAG, "resetAllNetworkBudgetInNeed", "Set budget time baseline of period from "+baseTime+" to "+currentTime);
        }
	}

	private void resetAllNetworkBudget() {
        mMobileNetwork.reset();
        mOtherNetwork.reset();
        mTotalNetwork.reset();
        mPref.flush();
	}
	
	public NetworkType getCurrentNetworkType() {
		
		int networkType = Utils.getCurrentNetworkType(mContext);
		
		if ( -1 == networkType )
			return NetworkType.TYPE_NONE;
		
		if ( ConnectivityManager.TYPE_MOBILE == networkType )
			return NetworkType.TYPE_MOBILE;
		
		return NetworkType.TYPE_OTHER;
    }

	private long getBudgetPeriodFromPolicy() {
		
		UPolicy policy = ReportPolicy.getInstance(mContext);
		String periodStr = policy.getValue(Common.CATEGORY_BUDGET, Common.KEY_BUDGET_PERIOD);
		long period = -1L;
		
		if(!TextUtils.isEmpty(periodStr)) {
			try {
				long periodValue = Long.parseLong(periodStr) * Common.HOUR_TO_MILLISECONDS;
				if ( _DEBUG && Common.FAKE_BUDGET_PPERIOD ) {
					periodValue = Long.parseLong(periodStr) * Common.SECOND_TO_MILLISECONDS;
				}
				period = periodValue < -1 ? Integer.MAX_VALUE : periodValue; // [pitt] period should be integer within -1 ~ 2147483647
			} catch (Exception e) {
				Log.e(TAG, "Exception happen during parsing BudgetPeriod, periodStr=" + periodStr);
				e.printStackTrace();
			}
		}
		if (_DEBUG) Log.i(TAG, "getBudgetPeriodFromPolicy()", "period in settings: "+periodStr+", adjusted period is " + (period/Common.SECOND_TO_MILLISECONDS) + " seconds");
		return period;
	}

}