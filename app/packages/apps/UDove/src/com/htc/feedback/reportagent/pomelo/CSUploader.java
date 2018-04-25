package com.htc.feedback.reportagent.pomelo;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import android.content.Context;
import android.text.TextUtils;
//change to ReportAgent log for consistent Log tag
import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.util.Utils;
import com.htc.feedback.reportagent.util.ReportConfig;
import com.htc.feedback.reportagent.budget.BudgetManager;
import com.htc.xps.pomelo.andrlib.LogLib;
import com.htc.xps.pomelo.andrlib.LogLib.SendResult;
import com.htc.xps.pomelo.log.LogPayload;
import com.htc.xps.pomelo.log.HandsetLogPKT;
import com.htc.xps.pomelo.log.DeviceInfo;

public class CSUploader implements UploadHelper {

	private static final String TAG = "CSUploader";
	private static final boolean _DEBUG = Common.DEBUG;
	private static final boolean _SECURITY_DEBUG = Common.SECURITY_DEBUG;
	
	//gits-imac="10.9.9.154:1463", kahlen="10.9.9.160:8182", jan="192.168.0.10:8080/LogLibWeb/LogWeb/"
	//AWS="175.41.155.226:80", AWS testing server(Enpu provided)="175.41.155.52:8000"
	//private static final String CS_ULOG_SERVER = "175.41.164.137:8000";
	
	private BudgetManager mBudgetManager;
	private Context mContext;

	protected CSUploader(Context context, BudgetManager budgetManager) {
		mContext = context;
		mBudgetManager = budgetManager;
	}

	public boolean putReport(String tag, HandsetLogPKT envelope) {
		
		if(_SECURITY_DEBUG){
			try{
				Log.d(TAG,toLimitedStringFrom(envelope));
			} catch(OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
		
		LogLib logLib = new LogLib(mContext, _DEBUG, !ReportConfig.isShippingRom());
		
		if ( TextUtils.isEmpty(logLib.getServerHost()) ) {
			Log.i(TAG,"no log server url");
			return false;
		}
		
		int ret = SendResult.CONNECTFAILED.getValue();
		try {
			ret = logLib.sendLogEnvelope(envelope);
			Log.i(TAG,"returned value : "+ret+" , TAG:"+tag);
		} catch (IllegalArgumentException e) {
			Log.i(TAG,"IllegalArgumentException happend during putReport");
			e.printStackTrace();
		}

		mBudgetManager.updateAppUsage(logLib.getTotalDownloadSize(), logLib.getTotalUploadSize(), "UploadPomeloLog");
		
		return ret == SendResult.SUCCESS.getValue();
	}
	
	public void putReport(byte[] data, Properties prop) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	private String toLimitedStringFrom(HandsetLogPKT envelope) {
		StringBuilder sb = new StringBuilder();
		if(envelope == null)
			return sb.toString();
		
		// dump Device Info first
		sb.append("[envelope header]\n");
		DeviceInfo deviceInfo = envelope.device_info;
		if(deviceInfo != null)
			sb.append(deviceInfo.toString());
		
		// dump payload
		sb.append("[Previous 2 payloads of envelope] Note, data field is ignored.\n");
		List<LogPayload> payloadList = envelope.payload;
		if(payloadList != null) {
			for(int i=0; i<payloadList.size() && i<2; i++) {
				sb.append("=> AppId : ").append(payloadList.get(i).app_id).append("\n");
				sb.append("   Category : ").append(payloadList.get(i).category).append("\n");
				sb.append("   Timestamp : ").append(payloadList.get(i).timestamp).append("\n");
				String data = payloadList.get(i).data;
				if(data != null)
					sb.append("   Data : ").append(data).append("\n");
			}
		}
		return sb.toString();
	}
}
