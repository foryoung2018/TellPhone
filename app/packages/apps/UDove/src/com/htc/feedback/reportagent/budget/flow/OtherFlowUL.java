package com.htc.feedback.reportagent.budget.flow;

import android.net.TrafficStats;

import com.htc.feedback.reportagent.budget.BudgetPreference;

public class OtherFlowUL extends Flow {
	
	public OtherFlowUL(BudgetPreference pref) {
		super( pref, BudgetPreference.KEY_OTHER_UL_APP_USAGE );
	}
	
	@Override
	public String getTAG() {
		return "OtherFlowUL";
	}
	
}