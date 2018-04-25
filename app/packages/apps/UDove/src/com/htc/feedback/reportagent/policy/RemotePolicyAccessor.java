package com.htc.feedback.reportagent.policy;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import com.htc.feedback.reportagent.pbdata.DeviceInfoHelper;
import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.util.Utils;
import com.htc.feedback.reportagent.util.ReportConfig;
import com.htc.xps.pomelo.andrlib.LogLib;
import com.htc.xps.pomelo.andrlib.LogLib.PolicyResponse;
import com.htc.xps.pomelo.log.HandsetPolicyAcknowledgeItem;
import com.htc.xps.pomelo.log.HandsetPolicyAcknowledgePKT;
import com.htc.xps.pomelo.log.HandsetPolicyItem;
import com.htc.xps.pomelo.log.HandsetPolicyRequestPKT;
import com.htc.xps.pomelo.log.HandsetPolicyResponsePKT;
import com.htc.xps.pomelo.log.HandsetPolicyResponsePKT.PolicyStatus;

public class RemotePolicyAccessor {
	
	public static class ResponseResult {
		
		public enum Status {
			FAILURE,
			UP_TO_DATE,
			NEW_POLICY,
		}
		
		public Status status = Status.FAILURE;
		public long ULSize = 0L;
		public long DLSize = 0L;
	}
	
	public static class AckResult {
		
		public boolean success = false;
		public long ULSize = 0L;
		public long DLSize = 0L;
	}
	
	private final static String TAG = "RemotePolicyAccessor";
	private final static boolean _DEBUG = Common.DEBUG;
	
	private Context mContext;
	
	private HandsetPolicyRequestPKT.Builder mRequest;
	private PolicyResponse mResponse;
	
	public RemotePolicyAccessor(Context context) {
		mContext = context;
	}
	
	public boolean hasPolicyServerHost() {
		
		if ( TextUtils.isEmpty(new LogLib(mContext, _DEBUG, !ReportConfig.isShippingRom()).getPolicyServerHost()) )
			return false;
		
		return true;
		
	}
	
	public long getExpectedULSize() {
		
		// TODO Using real size
		
		long size1 = new HandsetPolicyRequestPKT.Builder()
			.device_info(DeviceInfoHelper.getPolicyDeviceInfo(mContext))
			.last_update(PolicyPreference.getPolicyLastUpdate(mContext))
			.build().toByteArray().length;
		
		long size2 = new HandsetPolicyAcknowledgePKT.Builder()
			.device_info(DeviceInfoHelper.getPolicyDeviceInfo(mContext))
			.build().toByteArray().length;
		
		return size1 + size2;
	}

	public long getExpectedDLSize() {

		// TODO Using real size

		long size = new HandsetPolicyResponsePKT.Builder()
			.status(PolicyStatus.UPDATE_TO_DATE)
			.update_timestamp(Long.MAX_VALUE)
			.build().toByteArray().length;
		
		return size;
	}
	
	
	public ResponseResult updatePolicyFromServer() {

		ResponseResult result = new ResponseResult();
		
		if ( !hasPolicyServerHost() ) {
			result.status = ResponseResult.Status.FAILURE;
			Log.d(TAG, "no policy url for accessing");
			return result;
		}
		
		mRequest = new HandsetPolicyRequestPKT.Builder();
		mRequest.version("1.0")
		               .device_info(DeviceInfoHelper.getPolicyDeviceInfo(mContext))
		               .last_update(PolicyPreference.getPolicyLastUpdate(mContext));
		/*mRequest = HandsetPolicyRequestPKT
			.setDeviceInfo(DeviceInfoHelper.getPolicyDeviceInfo(mContext))
			.setLastUpdate(PolicyPreference.getPolicyLastUpdate(mContext))
			.build();*/

		if ( _DEBUG && Common.FAKE_POLICY_SERVER ) {
			mResponse = FakePolicyServer.getLogPolicy(mRequest.build());
			result.ULSize += mRequest.build().toByteArray().length;
			if ( null != mResponse && null != mResponse.policyResponse)
				result.DLSize += mResponse.policyResponse.toByteArray().length;
		} else {
			LogLib logLib = new LogLib(mContext, _DEBUG, !ReportConfig.isShippingRom());
			mResponse = logLib.getLogPolicy(mRequest.build());
			result.ULSize += logLib.getTotalUploadSize();
			result.DLSize += logLib.getTotalDownloadSize();
		}

		if ( null == mResponse ) {
			if (_DEBUG) Log.i(TAG, "getPolicyFromServer()", "response is null");
			result.status = ResponseResult.Status.FAILURE; 
			return result;
		}

		if ( null == mResponse.policyResponse ) {
			if (_DEBUG) Log.i(TAG, "getPolicyFromServer()", "policyResponse is null");
			result.status = ResponseResult.Status.FAILURE;
			return result;
		}

		if ( LogLib.SendResult.SUCCESS.getValue() != mResponse.statusCode ) {
			if (_DEBUG) Log.i(TAG, "getPolicyFromServer()", "statusCode is not SUCCESS");
			result.status = ResponseResult.Status.FAILURE;
			return result;
		}

		PolicyPreference.setPolicyLastUpdate(mContext, mResponse.policyResponse.update_timestamp);
		
		if ( PolicyStatus.UPDATE_TO_DATE == mResponse.policyResponse.status ) {
			result.status = ResponseResult.Status.UP_TO_DATE;
			return result;
		}

		if ( mResponse.policyResponse.policy == null || mResponse.policyResponse.policy.size() == 0) {
			if (_DEBUG) Log.i(TAG, "getPolicyFromServer()", "PolicyCount is 0");
			result.status = ResponseResult.Status.FAILURE;
			return result;
		}
		
		result.status = ResponseResult.Status.NEW_POLICY;
		return result;

	}
	
	public List<HandsetPolicyItem> getPolicy() {
		
		if ( null == mResponse )
			return null;
		
		if ( null == mResponse.policyResponse )
			return null;
		
		if (null == mResponse.policyResponse.policy || 0 == mResponse.policyResponse.policy.size() )
			return null;
		
		return mResponse.policyResponse.policy;
	}
	
	public AckResult replyPolicyResult2Server(boolean isUpdateSucc, List<HandsetPolicyAcknowledgeItem> items) {
		
		AckResult result = new AckResult();
		HandsetPolicyAcknowledgePKT.Builder ack;
		
		if ( null != items && 0 != items.size() ) {
		    ack = new HandsetPolicyAcknowledgePKT.Builder();
		    ack.device_info(DeviceInfoHelper.getPolicyDeviceInfo(mContext)).ack(items);
			/*ack = HandsetPolicyAcknowledgePKT.newBuilder()
				.setDeviceInfo(DeviceInfoHelper.getPolicyDeviceInfo(mContext))
				.addAllAck(items)
				.build();*/
		} else {
		    ack = new HandsetPolicyAcknowledgePKT.Builder();
		    ack.device_info(DeviceInfoHelper.getPolicyDeviceInfo(mContext));
			/*ack = HandsetPolicyAcknowledgePKT.newBuilder()
				.setDeviceInfo(DeviceInfoHelper.getPolicyDeviceInfo(mContext))
				.build();*/
		}
		
		int status = 0;
		if ( _DEBUG && Common.FAKE_POLICY_SERVER ) {
			status = FakePolicyServer.sendPolicyAck(ack.build());
			result.ULSize += ack.build().toByteArray().length;
			result.DLSize += 1L;
		} else {
			LogLib logLib = new LogLib(mContext, _DEBUG, !ReportConfig.isShippingRom());
			status = logLib.sendPolicyAck(ack.build());
			result.ULSize += logLib.getTotalUploadSize();
			result.DLSize += logLib.getTotalDownloadSize();
		}
		
		if ( LogLib.SendResult.SUCCESS.getValue() == status ) {
			
			if ( isUpdateSucc )
				PolicyPreference.setPolicyLastUpdate(mContext, mResponse.policyResponse.update_timestamp);
			
			mResponse = null;
			result.success = true;
			return result;
		}
		
		result.success = false;
		return result;
		
	}
}