package com.htc.feedback.reportagent.budget.network;

import com.htc.feedback.reportagent.budget.BudgetPreference;
import com.htc.feedback.reportagent.budget.flow.Flow;
import com.htc.feedback.reportagent.budget.flow.AllFlowDL;
import com.htc.feedback.reportagent.budget.flow.AllFlowTotal;
import com.htc.feedback.reportagent.budget.flow.AllFlowUL;

public class AllNetwork extends Network {
	
	private final static int DL = 0;
	private final static int UL = 1;
	private final static int TOTAL = 2;
	
	protected Flow[] mFlow;
	
	public AllNetwork(BudgetPreference pref) {
		mFlow = new Flow[3];
		mFlow[DL] = new AllFlowDL(pref);
		mFlow[UL] = new AllFlowUL(pref);
		mFlow[TOTAL] = new AllFlowTotal(pref);
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
		return "AllNetwork";
	}
}