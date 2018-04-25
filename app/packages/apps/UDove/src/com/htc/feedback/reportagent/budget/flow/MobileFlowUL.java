package com.htc.feedback.reportagent.budget.flow;

import android.net.TrafficStats;

import com.htc.feedback.reportagent.budget.BudgetPreference;

public class MobileFlowUL extends Flow {
	
	public MobileFlowUL(BudgetPreference pref) {
		super( pref, BudgetPreference.KEY_MOBILE_UL_APP_USAGE );
	}
	
	@Override
	public String getTAG() {
		return "MobileFlowUL";
	}
	
}