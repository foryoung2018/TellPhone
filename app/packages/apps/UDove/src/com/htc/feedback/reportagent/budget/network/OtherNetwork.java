package com.htc.feedback.reportagent.budget.network;

import com.htc.feedback.reportagent.budget.BudgetPreference;
import com.htc.feedback.reportagent.budget.flow.Flow;
import com.htc.feedback.reportagent.budget.flow.OtherFlowDL;
import com.htc.feedback.reportagent.budget.flow.OtherFlowTotal;
import com.htc.feedback.reportagent.budget.flow.OtherFlowUL;

public class OtherNetwork extends Network {
	
	private final static int DL = 0;
	private final static int UL = 1;
	private final static int TOTAL = 2;
	
	protected Flow[] mFlow;
	
	public OtherNetwork(BudgetPreference pref) {
		mFlow = new Flow[3];
		mFlow[DL] = new OtherFlowDL(pref);
		mFlow[UL] = new OtherFlowUL(pref);
		mFlow[TOTAL] = new OtherFlowTotal(pref);
	}

	@Override
	protected Flow getFlowDL() {
		return mFlow[DL];
	}

	@Override
	protected Flow getFlowUL() {
		return mFlow[UL];
	}

	@Override
	protected Flow getFlowTotal() {
		return mFlow[TOTAL];
	}

	@Override
	public String getTAG() {
		return "OtherNetwork";
	}
}