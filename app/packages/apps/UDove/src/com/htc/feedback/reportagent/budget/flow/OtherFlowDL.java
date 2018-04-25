package com.htc.feedback.reportagent.budget.flow;

import android.net.TrafficStats;

import com.htc.feedback.reportagent.budget.BudgetPreference;

public class OtherFlowDL extends Flow {
	
	public OtherFlowDL(BudgetPreference pref) {
		super( pref, BudgetPreference.KEY_OTHER_DL_APP_USAGE );
	}
	
	@Override
	public String getTAG() {
		return "OtherFlowDL";
	}
	
}