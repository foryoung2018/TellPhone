package com.htc.feedback.reportagent.policy;

import java.util.ArrayList;
import java.util.Random;

import android.os.SystemClock;

import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.Log;
import com.htc.xps.pomelo.andrlib.LogLib.PolicyResponse;
import com.htc.xps.pomelo.andrlib.LogLib.SendResult;
import com.htc.xps.pomelo.log.DataPair;
import com.htc.xps.pomelo.log.HandsetAppCategoryItem;
import com.htc.xps.pomelo.log.HandsetAppPolicyItem;
import com.htc.xps.pomelo.log.HandsetPolicyAcknowledgePKT;
import com.htc.xps.pomelo.log.HandsetPolicyItem;
import com.htc.xps.pomelo.log.HandsetPolicyRequestPKT;
import com.htc.xps.pomelo.log.HandsetPolicyResponsePKT;
import com.htc.xps.pomelo.log.HandsetPolicyResponsePKT.PolicyStatus;

class FakePolicyServer {

	private final static String TAG = "FakePolicyServer";
	private final static boolean _DEBUG = Common.DEBUG;
	
	private final static int FAIL_FACTOR = 3;
	
	private volatile static int countGetPolicy = 0;
	private volatile static int countPolicySucc = 0;

	private volatile static int countAck = 0;
	
	static PolicyResponse getLogPolicy(HandsetPolicyRequestPKT request) {
		
		PolicyResponse response = new PolicyResponse();
		
		final Random random = new Random(SystemClock.elapsedRealtime());
		
		// Failure case
		++countGetPolicy;
		if (_DEBUG) Log.i(TAG, "getLogPolicy()", "countGetPolicy = " + countGetPolicy);
		if ( 0 != (countGetPolicy%FAIL_FACTOR) ) {

			response.statusCode = SendResult.NOTFOUND.getValue();
			response.policyResponse = null;
			return response;
			
		}

		// Success case
		response.statusCode = SendResult.SUCCESS.getValue();
		++countPolicySucc;
		if (_DEBUG) Log.i(TAG, "getLogPolicy()", "countPolicySucc = " + countPolicySucc);
		switch( countPolicySucc%3 ) {
			case 0: {
				response.policyResponse = new HandsetPolicyResponsePKT.Builder()
					.update_timestamp(SystemClock.elapsedRealtime())
					.status(PolicyStatus.UPDATE_TO_DATE)
					.build();
				break;
			}
			case 1: {
				response.policyResponse = new HandsetPolicyResponsePKT.Builder()
					.update_timestamp(SystemClock.elapsedRealtime())
					.status(PolicyStatus.NEW_POLICIES)
					.build();
				break;
			}
			case 2: {
				response.policyResponse = new HandsetPolicyResponsePKT.Builder()
					.update_timestamp(SystemClock.elapsedRealtime())
					.status(PolicyStatus.NEW_POLICIES)
					.policy(
						new ArrayList<HandsetPolicyItem>() {{
							add(new HandsetPolicyItem.Builder()
									.mgmt_group_id("MgmtGroupId" + random.nextInt())
									.policy_group_id("PolicyGroupId" + random.nextInt())
									.app_policy_item(
										new ArrayList<HandsetAppPolicyItem>() {{
											add(new HandsetAppPolicyItem.Builder()
													.app_id(Common.APPID_REPORT_AGENT)
													.category_item(
														new ArrayList<HandsetAppCategoryItem>() {{
															add(new HandsetAppCategoryItem.Builder()
																	.category(Common.CATEGORY_POLICY)
																	.item(new ArrayList<DataPair>() {{
																			add(new DataPair.Builder()
																					.key(Common.KEY_POLICY_FREQ)
																					.value(""+(61+random.nextInt(59)))
																					.build()); }}
																	)
																	.item(new ArrayList<DataPair>() {{
																			add(new DataPair.Builder()
																					.key("key"+random.nextInt())
																					.value("value"+random.nextInt())
																					.build()); }}
																	)
																	.item(new ArrayList<DataPair>() {{
																			add(new DataPair.Builder()
																					.key("key"+random.nextInt())
																					.value("value"+random.nextInt())
																					.build()); }}
																	).build()
															);
														}}
													)
													.category_item(
														new ArrayList<HandsetAppCategoryItem>() {{
															add(new HandsetAppCategoryItem.Builder()
																.category("fixCategory1")
																.item(new ArrayList<DataPair>() {{
																		add (new DataPair.Builder()
																				.key("fixKey")
																				.value("value"+random.nextInt())
																				.build()); }}
																)
																.item(new ArrayList<DataPair>() {{
																		add (new DataPair.Builder()
																				.key("key"+random.nextInt())
																				.value("value"+random.nextInt())
																				.build()); }}
																).build()
															);
														}}
													).build()); }}
									)
									.app_policy_item(
										new ArrayList<HandsetAppPolicyItem>() {{
											add(new HandsetAppPolicyItem.Builder()
													.app_id("com.htc.fakeApp")
													.category_item(
														new ArrayList<HandsetAppCategoryItem>() {{
															add(new HandsetAppCategoryItem.Builder()
																.category("fixCategory1")
																.item(new ArrayList<DataPair>() {{
																		add (new DataPair.Builder()
																				.key("fixKey")
																				.value("value"+random.nextInt())
																				.build()); }}
																)
																.item(new ArrayList<DataPair>() {{
																		add (new DataPair.Builder()
																				.key("key"+random.nextInt())
																				.value("value"+random.nextInt())
																				.build()); }}
																).build()
															);
														}}
													)
													.category_item(
														new ArrayList<HandsetAppCategoryItem>() {{
															add(new HandsetAppCategoryItem.Builder()
																.category("fixCategory2")
																.item(new ArrayList<DataPair>() {{
																		add (new DataPair.Builder()
																				.key("fixKey")
																				.value("value"+random.nextInt())
																				.build()); }}
																)
																.item(new ArrayList<DataPair>() {{
																		add (new DataPair.Builder()
																				.key("key"+random.nextInt())
																				.value("value"+random.nextInt())
																				.build()); }}
																).build()
															);
														}}
													).build()); }}
									).build()); }}
					)
					.build();
				break;
			}
		}
		
		return response;
	}
	
	static int sendPolicyAck(HandsetPolicyAcknowledgePKT ack) {
		
		// Failure case
		++countAck;
		if (_DEBUG) Log.i(TAG, "sendPolicyAck()", "countAck = " + countAck);
		if ( 0 != (countAck%FAIL_FACTOR) ) {
			
			return SendResult.CONNECTFAILED.getValue();
			
		}
		
		// Success case
		return SendResult.SUCCESS.getValue();
	}

}