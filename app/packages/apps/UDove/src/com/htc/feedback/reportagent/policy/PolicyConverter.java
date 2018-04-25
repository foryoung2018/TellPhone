package com.htc.feedback.reportagent.policy;

import java.util.LinkedList;
import java.util.List;

import com.htc.feedback.policy.PolicyUtils;
import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.Log;
import com.htc.xps.pomelo.log.DataPair;
import com.htc.xps.pomelo.log.HandsetAppCategoryItem;
import com.htc.xps.pomelo.log.HandsetAppPolicyItem;
import com.htc.xps.pomelo.log.HandsetPolicyAcknowledgeItem;
import com.htc.xps.pomelo.log.HandsetPolicyAcknowledgeItem.AckStatus;
import com.htc.xps.pomelo.log.HandsetPolicyItem;

import android.os.Bundle;

public class PolicyConverter {
	
	private final static String TAG = "PolicyConverter";
	private final static boolean _DEBUG = Common.DEBUG;
	private final static int MAX_APPID_STRING_LENGTH = 128;
	private final static int MAX_CATEGORY_STRING_LENGTH = 128;
	private final static int MAX_KEY_STRING_LENGTH = 128;
	private final static int MAX_VALUE_STRING_LENGTH = 128;
	
	public static Bundle handsetPolicyItem2Bundle( HandsetPolicyItem policy ) {
		
		Bundle bundle = _handsetPolicyItem2Bundle(policy, new Bundle());

		if ( null == bundle || bundle.isEmpty() )
			return null;
		return bundle;
	}

	public static Bundle handsetPolicyItem2Bundle( List<HandsetPolicyItem> policies ) {
		
		if ( null == policies ) return null;
		int count = policies.size();
		if ( 0 == count ) return null;
		
		Bundle bundle = new Bundle();
		for( int i=0; i<count; ++i )
			_handsetPolicyItem2Bundle(policies.get(i), bundle);
		
		if ( bundle.isEmpty() ) return null;
		
		return bundle;
	}
	
	/**
	 * Current maximum length of appid is 45(com.android.internal.telephony.dataconnection).<br>
	 * Current maximum length of category is 28(WHDMI_WirelessDisplayService).<br>
	 * Maximum length of a string in sqlite is 1 billion (http://www.sqlite.org/limits.html).<br>
	 * For avoiding DoS attacking and better compatibility, we set maximum length of appid/category as 128/64, and maximum length of key/value as 32/16.   
	 * @return true means the policy can be added into policy table
	 */
	private static boolean checkStingLenghtForPolicy(String appid, String category, String key, String value) {
	    
	    if (appid == null || category == null || key == null || value == null)
	        return false;
	    
	    if (appid.length() == 0 || appid.length() > MAX_APPID_STRING_LENGTH)
	        return false;
	    
	    if (category.length() == 0 || category.length() > MAX_CATEGORY_STRING_LENGTH)
            return false;
	    
	    if (key.length() == 0 || key.length() > MAX_KEY_STRING_LENGTH)
            return false;
	    
	    if (value.length() == 0 || value.length() > MAX_VALUE_STRING_LENGTH)
            return false;
	    
	    return true;
	}
	
	private static Bundle _handsetPolicyItem2Bundle( HandsetPolicyItem policy, Bundle bundle ) {
		
		if ( null == policy || policy.app_policy_item == null) 
		    return null;
		
		int appCount = policy.app_policy_item.size();
		if (_DEBUG) Log.i(TAG, "_handsetPolicyItem2Bundle()", "Total " + appCount + " apps");
		if ( 0 == appCount ) return null;
		long endTime = -1;
		for( int i=0; i<appCount; ++i ) {
			HandsetAppPolicyItem appItem = policy.app_policy_item.get(i);
			if(appItem == null)
				continue;
			
			String appId = appItem.app_id;
			if(appItem.end_time != null && appItem.end_time > 0) //TODO: new HandsetAppPolicyItem doesn't have hasEndtime() function.
				endTime = appItem.end_time;
			else 
				endTime = -1;
			if (_DEBUG) Log.i(TAG, "appid: "+appId+", end time: "+endTime);
			
			if (appItem.category_item == null)
			    continue;
			
			int categoryCount = appItem.category_item.size();
			if (_DEBUG) Log.i(TAG, "_handsetPolicyItem2Bundle()", "appItem:" + i + ":" + appId + ":" + categoryCount);
			for ( int j=0; j<categoryCount; ++j ) {
				HandsetAppCategoryItem categoryItem = appItem.category_item.get(j);
				if(categoryItem == null)
					continue;
				
				String category = categoryItem.category;
				if (categoryItem.item == null)
				    continue;
				
				int itemCount = categoryItem.item.size();
				if (_DEBUG) Log.i(TAG, "_handsetPolicyItem2Bundle()", "categoryItem:" + j + ":" + appId + ":" + categoryCount + ":" + itemCount);
				for ( int k=0; k<itemCount; ++k ) {
					DataPair item = categoryItem.item.get(k);
					if(item == null)
						continue;
					
					String key = item.key;
					String value = item.value;
					if (checkStingLenghtForPolicy(appId, category, key, value)) {
					    PolicyUtils.putPolicy(bundle, appId, category, key, value, endTime);
	                    if (_DEBUG) Log.i(TAG, "_handsetPolicyItem2Bundle()", "" + appId + ":" + category + ":" + key + ":" + value + ", end time: " + endTime);
					} else
					    Log.i(TAG, "_handsetPolicyItem2Bundle()", "[Warning] The policy isn't added into policy table due to string length is abnormal, " + appId + ":" + category + ":" + key + ":" + value + ", end time: " + endTime);
				}
			}
		}
		return bundle;
	}

	public static List<HandsetPolicyAcknowledgeItem> handsetPolicyItem2Acks( List<HandsetPolicyItem> policies, boolean result ) {
		
		if ( null == policies ) return null;
		int count = policies.size();
		if ( 0 == count ) return null;
		
		List<HandsetPolicyAcknowledgeItem> items = new LinkedList<HandsetPolicyAcknowledgeItem>();
		for ( int i=0; i<count; ++i ) {
			HandsetPolicyItem policy = policies.get(i);
			items.add(handsetPolicyItem2Ack(policy,result));
		}
		return items;
		
	}
	
	public static HandsetPolicyAcknowledgeItem handsetPolicyItem2Ack( HandsetPolicyItem policy, boolean result ) {
		
		HandsetPolicyAcknowledgeItem.Builder ackBuilder = _handsetPolicyItem2AckBuilder(policy, result);

		if ( null == ackBuilder ) return null;

		return ackBuilder.build();
	}

	private static HandsetPolicyAcknowledgeItem.Builder _handsetPolicyItem2AckBuilder( HandsetPolicyItem policy, boolean result ) {

		if ( null == policy ) return null;
		
		HandsetPolicyAcknowledgeItem.Builder ackBuilder = new HandsetPolicyAcknowledgeItem.Builder();
		ackBuilder.mgmt_group_id(policy.mgmt_group_id)
		                .policy_group_id(policy.policy_group_id)
		                .status(result ? AckStatus.POLICY_DONE : AckStatus.POLICY_FAILED);
		
		/*HandsetPolicyAcknowledgeItem.Builder ackBuilder = HandsetPolicyAcknowledgeItem.newBuilder()
			.setMgmtGroupId(policy.getMgmtGroupId())
			.setPolicyGroupId(policy.getPolicyGroupId())
			.setStatus( result ? AckStatus.POLICY_DONE : AckStatus.POLICY_FAILED );*/
		
		return ackBuilder;
		
	}
	
	public static String getFromHandsetPolicyItems(List<HandsetPolicyItem> policies, String appId, String category, String key) {

		if ( null == policies ) return null;
		int count = policies.size();
		if ( 0 == count ) return null;
		
		if ( null == appId || 0 == appId.length() ) return null;
		if ( null == category || 0 == category.length() ) return null;
		if ( null == key || 0 == key.length() ) return null;
		
		for( int i=0; i<count; ++i ) {
			String value = _getFromHandsetPolicyItem(policies.get(i), appId, category, key);
			if ( null != value && 0 != value.length() )
				return value;
		}
		return null;
	}

	private static String _getFromHandsetPolicyItem(HandsetPolicyItem policy, String appId, String category, String key) {
		
		if ( policy == null || policy.app_policy_item == null) 
		    return null;
		
		int appCount = policy.app_policy_item.size();
		if ( 0 == appCount ) 
		    return null;
		
		for( int i=0; i<appCount; ++i ) {
			HandsetAppPolicyItem appItem = policy.app_policy_item.get(i);
			if (appItem == null || !appId.equals(appItem.app_id) || appItem.category_item == null)
				continue;
			
			int categoryCount = appItem.category_item.size();
			for ( int j=0; j<categoryCount; ++j ) {
				HandsetAppCategoryItem categoryItem = appItem.category_item.get(j);
				if (categoryItem==null || !category.equals(categoryItem.category) || categoryItem.item == null)
					continue;
				
				int itemCount = categoryItem.item.size();
				for ( int k=0; k<itemCount; ++k ) {
					DataPair item = categoryItem.item.get(k);
					if (item != null && key.equals(item.key) )
						return item.value;
				}
			}
		}
		
		return null;
	}

}