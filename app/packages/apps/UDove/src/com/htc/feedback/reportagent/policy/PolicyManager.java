package com.htc.feedback.reportagent.policy;

import java.util.List;
import com.htc.feedback.policy.PolicyUtils;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.Utils;
import com.htc.feedback.reportagent.budget.BudgetManager;
import com.htc.feedback.reportagent.policy.RemotePolicyAccessor.AckResult;
import com.htc.feedback.reportagent.policy.RemotePolicyAccessor.ResponseResult;
import com.htc.xps.pomelo.log.HandsetPolicyAcknowledgeItem;
import com.htc.xps.pomelo.log.HandsetPolicyItem;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PolicyManager {
	
	private enum DownloadResult {
		FAIL_GENERIC,
		FAIL_ALL_NO_BUDGET,
		FAIL_CURRENT_NETWORK_NO_BUDGET,
		SUCC_AND_APPLY,
		SUCC_BUT_NOT_APPLY;
	}
	
	private final static String TAG = "PolicyManager";
	private final static boolean _DEBUG = Common.DEBUG;
	private static PolicyManager sInstance;
	public static PolicyManager getInstance(Context context, BudgetManager budgetManager) {
	    if (sInstance == null)
	        sInstance = new PolicyManager(context, budgetManager);
	    return sInstance;
	}

	private Context mContext;
	private BudgetManager mBudgetManager;
	private RemotePolicyAccessor mRemotePolicyAccessor;
	
	private PolicyManager(Context context, BudgetManager budgetManager) {
		mContext = context;
		mBudgetManager = budgetManager;
		mRemotePolicyAccessor = new RemotePolicyAccessor(mContext);
	}
	
	public void downloadPolicy() {
	    if ( PolicyUtils.isPolicyEnabled(mContext) && Utils.isNetworkAllowed(mContext)) {
    	    DownloadResult result = downloadPolicyInner();
    	    if (DownloadResult.SUCC_AND_APPLY == result || DownloadResult.SUCC_BUT_NOT_APPLY == result) {
    	        /*Intent intent = new Intent("com.htc.intent.action.UDOVE_POLICY_DOWNLOAD_RESULT");
    	        intent.putExtra("result", true); // succeed to get policy from Server
    	        mContext.sendBroadcast(intent, "com.htc.permission.UBLS_PROTECTED_USAGE");  // protectionLevel="signatureOrSystem"*/
    	        Log.d(TAG, "Succeed to get policy");
    	    }
	    }
	    else {
	        Log.d(TAG, "Cannot connect to policy server due to setting is disabled");
	    }
	    PolicyScheduler.getInstance(mContext).resetAlarm(System.currentTimeMillis());
	}
	
	/**
	 * Get new policy from remote server and update it into local policy provider
	 * 
	 * @return true if policy provider changes
	 */
	private DownloadResult downloadPolicyInner() {
		
		Log.i(TAG, "downloadPolicy()", "Start to download policy");

		int retryTimes = Utils.getRetryTimes(mContext);
		
		/* Check available budget */
		{
			long expectedULSize = mRemotePolicyAccessor.getExpectedULSize(); 
			long expectedDLSize = mRemotePolicyAccessor.getExpectedDLSize(); 
		
			if ( !mBudgetManager.isAvailableByCurrentNetwork(expectedDLSize, expectedULSize) ) {
				if ( !mBudgetManager.isAvailableByNoncurrentNetwork(expectedDLSize, expectedULSize))
					return DownloadResult.FAIL_ALL_NO_BUDGET;
				else
					return DownloadResult.FAIL_CURRENT_NETWORK_NO_BUDGET;
			}
		}

		// Do nothing if we do not have any policy server data
		if ( !mRemotePolicyAccessor.hasPolicyServerHost() ) {
			Log.e(TAG, "no policy server");
			return DownloadResult.FAIL_GENERIC; // not change
		}

		/* 1. Update Policy from Policy Server */
		{
			ResponseResult result = null;
			for ( int i=0; i<=retryTimes; ++i ) {
				if (!Utils.isNetworkAllowed(mContext)) {
					Log.d(TAG, "Cannot update policy due to no proper network.");
				} else {
					result = mRemotePolicyAccessor.updatePolicyFromServer();
					
					if ( null == result ) {
						Log.e(TAG, "downloadPolicy result is null");
						return DownloadResult.FAIL_GENERIC; // not change
					}
					
					mBudgetManager.updateAppUsage(result.DLSize, result.ULSize, "PolicyDownload");
					
					if ( ResponseResult.Status.NEW_POLICY == result.status
					  || ResponseResult.Status.UP_TO_DATE == result.status )
						break;
				}
			}
			
			// [CQG #211]fixed by Ricky,2012.10.02
			if (result == null)
			return DownloadResult.FAIL_GENERIC;
			
			// Do nothing if we can't get new policy from server
			if ( ResponseResult.Status.FAILURE == result.status ) {
				Log.d(TAG, "can't get policy from server for " + retryTimes + " times");
				return DownloadResult.FAIL_GENERIC; // not change
			}
			
			// Send null back to server if our policy is up-to-date.
			if (ResponseResult.Status.UP_TO_DATE == result.status ) {
				replyPolicyResult2Server(true, null);
				Log.i(TAG, "downloadPolicy()", "Policy is UP-TO-DATE");
				return DownloadResult.SUCC_BUT_NOT_APPLY; // not change
		 }
		}
		
		/* 2. Set new policy into provider and reply ack into Policy Server */
		{
			List<HandsetPolicyItem> policyItems = mRemotePolicyAccessor.getPolicy();
			
			// Send null back to server if no any policy items
			if ( null == policyItems || 0 == policyItems.size() ) {
				replyPolicyResult2Server(true, null);
				return DownloadResult.SUCC_BUT_NOT_APPLY; // not change
			}
			
			boolean isApply = true;
			HandsetPolicyUpdater updater = new HandsetPolicyUpdater(mContext);
			Bundle policyBundle = PolicyConverter.handsetPolicyItem2Bundle(policyItems);
			updater.applyPolicy2Provider(policyBundle);
			
			List<HandsetPolicyAcknowledgeItem> ackItems = PolicyConverter.handsetPolicyItem2Acks(policyItems, isApply);
			replyPolicyResult2Server(true, ackItems);
			return DownloadResult.SUCC_AND_APPLY;
		}
		
	}
	
	private boolean replyPolicyResult2Server(boolean isUpdateSucc, List<HandsetPolicyAcknowledgeItem> items) {
		
		int retryTimes = Utils.getRetryTimes(mContext);
		
		AckResult result = null;
		for ( int i=0; i<=retryTimes; ++i ) {
			if ( !Utils.isNetworkAllowed(mContext) ) {
				Log.d(TAG, "Cannot reply policy ack to server due to no proper network.");
			} else {
				//[CQG #96]modified by Ricky,2012.09.29
				if( mRemotePolicyAccessor != null)
				result = mRemotePolicyAccessor.replyPolicyResult2Server(isUpdateSucc, items);
				
				if ( null == result ) {
					Log.e(TAG, "replyPolicyResult2Server result is null");
					return false; // not change
				}
	
				mBudgetManager.updateAppUsage(result.DLSize, result.ULSize, "PolicyDownload");
				
				if ( result.success )
					break;
			}
		}
		
			// [CQG #213]fixed by Ricky,2012.10.02
			if (result == null) 
			return false;
			
			if (false == result.success) {
				Log.d(TAG, "Can't reply policy ack to server");
			} else {
				if (_DEBUG)
					Log.i(TAG, "onReplyPolicyResult2Server()", "Success reply policy ack to server");
			}
			return result.success;
	}
}
