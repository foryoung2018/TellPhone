package com.htc.feedback.reportagent.budget.flow;

import android.net.TrafficStats;

import com.htc.feedback.reportagent.budget.BudgetPreference;

public class MobileFlowTotal extends Flow {
	
	public MobileFlowTotal(BudgetPreference pref) {
		super( pref, BudgetPreference.KEY_MOBILE_TOTAL_APP_USAGE );
	}
	
	@Override
	public String getTAG() {
		return "MobileFlowTotal";
	}
	
}