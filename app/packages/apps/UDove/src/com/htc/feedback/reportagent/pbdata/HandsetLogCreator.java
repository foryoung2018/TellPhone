package com.htc.feedback.reportagent.pbdata;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import okio.ByteString;
import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.io.Buffer;
import com.htc.feedback.reportagent.pbdata.DeviceInfoHelper;
import com.htc.xps.pomelo.log.HandsetLogPKT;
import com.htc.xps.pomelo.log.LogPayload;
import com.htc.xps.pomelo.util.PacketUtil;
import com.htc.feedback.reportagent.pomelo.ReportPreference;
import com.htc.feedback.reportagent.util.Log;

//public class HandsetLogCreator implements WrapPayloadChainCreator {
public class HandsetLogCreator {
	private static final String TAG = "HandsetLogCreator";
	private Context mContext;
	private HandsetLogPKT.Builder mBuilder = new HandsetLogPKT.Builder();
	public HandsetLogCreator(Context context) {
		mContext = context;
        mBuilder.version("1.0");
        mBuilder.payload = new ArrayList<LogPayload>();
	}
	
	// for user profiling
//	public void add(String appId, String category, long timestamp, String data) {
//		mBuilder.addPayload(LogPayload.newBuilder()
//			.setAppId(appId)
//			.setCategory(category)
//			.setTimestamp(timestamp)
//			.setData(data));
//	}
	
	// for report with attachment
	public void add(String appId, String category, long timestamp, String data, Buffer attachment) {
		LogPayload.Builder logPayloadBuilder = new LogPayload.Builder();
		if(!TextUtils.isEmpty(appId))
			logPayloadBuilder.app_id(appId);
		if(!TextUtils.isEmpty(category))
			logPayloadBuilder.category(category);
		logPayloadBuilder.timestamp(timestamp);
		if(!TextUtils.isEmpty(data))
			logPayloadBuilder.data(data);
		if(attachment != null)
			logPayloadBuilder.attachment(ByteString.of(attachment.getBuffer(), attachment.getOffset(), attachment.getLength()));
		mBuilder.payload.add(logPayloadBuilder.build());
	}
	
	public HandsetLogPKT toHandsetLog(boolean isErrorReport){
		//mBuilder.setRegion(Utils.getRegion(mContext));
		mBuilder.device_info(DeviceInfoHelper.getLogDeviceInfo(mContext, isErrorReport));

		try {
			JSONObject info = new JSONObject(); 
			String jsonString="";
			long envelopeSN = ReportPreference.getNewSN(mContext);
			Log.d(TAG, "envelope SN: "+envelopeSN);
			info.put(Common.KEY_INFO_SN,envelopeSN);
			jsonString=info.toString();
			mBuilder.payload.add(
					new LogPayload.Builder()
					.app_id(Common.APPID_REPORT_AGENT)
					.category(Common.CATEGORY_INFO)
					.timestamp(System.currentTimeMillis())
					.data(jsonString)
					.build()
			);
		} catch (JSONException e) {
			Log.e(TAG, "toHandsetLog() JSON Object Exception");
			e.printStackTrace();
		}//Fix for runTime error, JsonStringer indexoutofboundsException (may be caused by wrong charset or unrecognized character)
    	// Icon_G#1763 
		catch(Exception e){
			Log.e(TAG,"toHandsetLog() JSON Object Exception");
			e.printStackTrace();
		} //End of fix

		// TODO: it is redundant to make HandsetLogPKT twice. 
		// HandsetLogPKT.Builder.build() => HandsetLogPKT => HandsetLogPKT.Builder.build() => HandsetLogPKT  
		return PacketUtil.calcLogPacketCheckSum(mBuilder.build());
		//return mBuilder.build();
	}
}
