package com.htc.feedback.reportagent.budget.flow;

import android.net.TrafficStats;

import com.htc.feedback.reportagent.budget.BudgetPreference;

public class AllFlowUL extends Flow {
	
	public AllFlowUL(BudgetPreference pref) {
		super( pref, BudgetPreference.KEY_ALL_UL_APP_USAGE );
	}
	
	@Override
	public String getTAG() {
		return "AllFlowUL";
	}
	
}