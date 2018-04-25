package com.htc.feedback.reportagent.budget.network;

import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.budget.flow.Flow;

public abstract class Network {
	
	private final static boolean _DEBUG = Common.DEBUG;
	
	protected abstract Flow getFlowDL();
	protected abstract Flow getFlowUL();
	protected abstract Flow getFlowTotal();
	public abstract String getTAG();

	public void reset() {
		getFlowTotal().reset();
		getFlowDL().reset();
		getFlowUL().reset();
	}
	
	public void appUsageUpdated(long newDLAppUsage, long newULAppUsage) {
		getFlowTotal().appUsageUpdated(newDLAppUsage+newULAppUsage);
		getFlowDL().appUsageUpdated(newDLAppUsage);
		getFlowUL().appUsageUpdated(newULAppUsage);
	}

	public boolean isAvailableByBytes(long totalLimit, long expectedDLSize, long DLLimit, long expectedULSize, long ULLimit) {

		boolean result1 = getFlowTotal().isAvailableBySize(expectedDLSize + expectedULSize, totalLimit);
		boolean result2 = getFlowDL().isAvailableBySize(expectedDLSize, DLLimit);
		boolean result3 = getFlowUL().isAvailableBySize(expectedULSize, ULLimit);
		if (_DEBUG) Log.i(getTAG(), "isAvailableByBytes()", getFlowTotal().getTAG() + ":" + result1 + " " + getFlowDL().getTAG() + ":" + result2 + " " + getFlowUL().getTAG() + ":" + result3);
		return (result1 && result2 && result3);
	}
	
	public boolean isAvailableByPercentage(long amountPercentage, long expectedDLSize, long DLPercentage, long expectedULSize, long ULPercentage) {
        // This case is abandoned on KK443 (M8_MR) because it isn't realistic. Instead, always return true. (2014-05-08)
        return true;
	}
	
}