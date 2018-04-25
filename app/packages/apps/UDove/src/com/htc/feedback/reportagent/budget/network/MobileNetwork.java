package com.htc.feedback.reportagent.budget.network;

import com.htc.feedback.reportagent.budget.BudgetPreference;
import com.htc.feedback.reportagent.budget.flow.Flow;
import com.htc.feedback.reportagent.budget.flow.MobileFlowDL;
import com.htc.feedback.reportagent.budget.flow.MobileFlowTotal;
import com.htc.feedback.reportagent.budget.flow.MobileFlowUL;

public class MobileNetwork extends Network {
	
	private final static int DL = 0;
	private final static int UL = 1;
	private final static int TOTAL = 2;
	
	protected Flow[] mFlow;
	
	public MobileNetwork(BudgetPreference pref) {
		mFlow = new Flow[3];
		mFlow[DL] = new MobileFlowDL(pref);
		mFlow[UL] = new MobileFlowUL(pref);
		mFlow[TOTAL] = new MobileFlowTotal(pref);
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
		return "MobileNetwork";
	}
}