package com.htc.feedback.reportagent.budget.flow;

import android.net.TrafficStats;

import com.htc.feedback.reportagent.budget.BudgetPreference;

public class AllFlowDL extends Flow {
	
	public AllFlowDL(BudgetPreference pref) {
		super( pref, BudgetPreference.KEY_ALL_DL_APP_USAGE );
	}

	@Override
	public String getTAG() {
		return "AllFlowDL";
	}
	
}