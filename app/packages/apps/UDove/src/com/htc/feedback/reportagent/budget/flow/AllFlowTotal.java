package com.htc.feedback.reportagent.budget.flow;

import android.net.TrafficStats;

import com.htc.feedback.reportagent.budget.BudgetPreference;

public class AllFlowTotal extends Flow {
	
	public AllFlowTotal(BudgetPreference pref) {
		super( pref, BudgetPreference.KEY_ALL_TOTAL_APP_USAGE );
	}

	@Override
	public String getTAG() {
		return "AllFlowTotal";
	}
	
}