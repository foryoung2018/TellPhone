package com.htc.feedback.reportagent.budget.flow;

import android.net.TrafficStats;

import com.htc.feedback.reportagent.budget.BudgetPreference;

public class OtherFlowTotal extends Flow {
	
	public OtherFlowTotal(BudgetPreference pref) {
		super( pref, BudgetPreference.KEY_OTHER_TOTAL_APP_USAGE	);
	}
	
	@Override
	public String getTAG() {
		return "OtherFlowTotal";
	}
	
}