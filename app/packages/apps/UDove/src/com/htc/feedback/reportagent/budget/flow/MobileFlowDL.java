package com.htc.feedback.reportagent.budget.flow;

import android.net.TrafficStats;

import com.htc.feedback.reportagent.budget.BudgetPreference;

public class MobileFlowDL extends Flow {
	
	public MobileFlowDL(BudgetPreference pref) {
		super( pref, BudgetPreference.KEY_MOBILE_DL_APP_USAGE );
	}
	
	@Override
	public String getTAG() {
		return "MobileFlowDL";
	}
	
}