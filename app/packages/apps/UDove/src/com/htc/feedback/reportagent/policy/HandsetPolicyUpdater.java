package com.htc.feedback.reportagent.policy;

import android.content.Context;
import android.os.Bundle;

import com.htc.feedback.policy.PolicyStore;
import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.Log;

public class HandsetPolicyUpdater {
	
	private final static String TAG = "HandsetPolicyUpdater";
	private final static boolean _DEBUG = Common.DEBUG;

	private PolicyStore mPolicyStore;
	
	public HandsetPolicyUpdater(Context ctx) {
		mPolicyStore = PolicyStore.getInstance(ctx);
	}
	
	public boolean applyPolicy2Provider(Bundle policy) {
		boolean result = false;
		
		if (mPolicyStore == null || policy == null)
			return result;
		
		try {
			result = mPolicyStore.setPolicy(policy);
			if(_DEBUG) Log.d(TAG, "[applyPolicy2Provider] result: "+result);
		} catch (Exception e) {
			Log.e(TAG, "applyPolicy2Provider()", "Exception happen during setPolicy");
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			Log.e(TAG, "applyPolicy2Provider()", "OutOfMemoryError happen during setPolicy");
			e.printStackTrace();
		}
		
		return result;
	}
}
