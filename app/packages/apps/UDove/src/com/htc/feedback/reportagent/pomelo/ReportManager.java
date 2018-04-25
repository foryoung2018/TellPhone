package com.htc.feedback.reportagent.pomelo;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import org.json.JSONException;
import org.json.JSONObject;

import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.ReportService;
import com.htc.feedback.reportagent.budget.BudgetManager;
import com.htc.feedback.reportagent.cache.EntryFile;
import com.htc.feedback.reportagent.cache.LogCacheManager;
import com.htc.feedback.reportagent.io.LogEntry;
import com.htc.feedback.reportagent.io.PositionRetrievingInputStream;
import com.htc.feedback.reportagent.io.ReleasableByteArrayOutputStream;
import com.htc.feedback.policy.UPolicy;
import com.htc.xps.pomelo.log.DeviceInfo;
import com.htc.xps.pomelo.log.LogPayload;
import com.htc.xps.pomelo.log.HandsetLogPKT;

import android.app.ApplicationErrorReport;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.DropBoxManager;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.StatFs;
import android.os.SystemClock;
import com.htc.feedback.reportagent.util.ReflectionUtil;
import android.os.DropBoxManager.Entry;
import android.text.TextUtils;

import com.htc.feedback.reportagent.pbdata.DeviceInfoHelper;
import com.htc.feedback.reportagent.pbdata.HandsetLogCreator;
import com.htc.feedback.reportagent.util.EngineeringPreference;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.util.PackageInfoUtil;
import com.htc.feedback.reportagent.util.ReportConfig;
import com.htc.feedback.reportagent.util.Security;
import com.htc.feedback.reportagent.util.SignatureUtil;
import com.htc.feedback.reportagent.util.Utils;
import com.htc.feedback.reportagent.io.Buffer;
import com.squareup.wire.Wire;

public class ReportManager {
	
	private static final String TAG = "ReportUploader";
	private static final boolean _DEBUG = Common.DEBUG; 
	
	private Context mContext;
	private BudgetManager mBudgetManager;
	private boolean mNetworkConnected = false;
	private UPolicy mFeedbackPolicy;
	private CSUploader mCSUploader;
	private static long sLastTimeTellHtcSettingChanged = -1;
	private static final long MIN_RESEND_INTERVAL = 30 * 60 * 1000; 
	private static final long MAX_INFLATED_LOG_SIZE = 768 * 1024;
	
	public ReportManager(Context ctx, BudgetManager budgetManager) {
		mContext = ctx;
		mBudgetManager = budgetManager;
		mFeedbackPolicy = new UPolicy("com.htc.feedback", ctx); // This may change to UPolicy.APPID_ERROR_REPORT
		if(null == mCSUploader)
			mCSUploader = new CSUploader(ctx, mBudgetManager);
	}
	
	//For Service Handler's calling when it comes connectivity changed
	public boolean onPowerConnectedChanged(){
		if(!ReportConfig.isShippingRom() && !EngineeringPreference.canSendReportToNCPomelo(mContext)){
			Log.d(TAG, "[onPowerConnectedChanged] CAN NOT SEND REPORT TO NC POMELO SERVER!");
			return false;
		}
		resumeCSCachedReport();
		EntryFile [] fileList = LogCacheManager.getInstance().getFiles(mContext);
		if(fileList==null||fileList.length==0)
			return true;  //no cached data 
		else
			return false;  // has cached data still
	}
	
	public void onUpload(Intent intent) {
		if(intent == null)
			return;
		String action = intent.getAction();
		//-Check if the device can send report to NC pomelo 
		if(!ReportConfig.isShippingRom() && !EngineeringPreference.canSendReportToNCPomelo(mContext)){
	        Log.d(TAG, "[POMELO][onUpload] CAN NOT SEND REPORT TO NC POMELO SERVER!");
            return;
		}
		
		if( ReportService.ACTION_UPLOAD_ERROR_LOG.equals(action)) {
			long time = 0;
			String tag = intent.getStringExtra("tag");
			String appId = null;
			byte[] key = null;
			byte[] iv = null;
			
			if(!UPolicy.canLogErrorReport(UPolicy.APPID_ERROR_REPORT, tag, mContext)) {
				Log.d(TAG, "[onUpload] tag: '" + tag + "' isn't in policy list or setting is disabled!");
				return;
			}
			
			String msg = intent.getStringExtra("msg");
			boolean fromDropBox = intent.getBooleanExtra("fromDropBox", false);
			if (fromDropBox) {
				time = intent.getLongExtra("time", -1);
				key = intent.getByteArrayExtra("errorReportKey");
				iv = intent.getByteArrayExtra("errorReportIv");
			}
			String file = intent.getStringExtra("file");
			String position = intent.getStringExtra("radio");
			position = position == null ? "" : position;
			String logStr = null;
			if(Common.SECURITY_DEBUG)
				logStr = "[onUpload] tag: "+tag+", msg: "+msg+", fromDropBox: "+fromDropBox+", time: "+time+", file: "+file+", position: "+position;
			else
				logStr = "[onUpload] tag: "+tag+", fromDropBox: "+fromDropBox+", time: "+time+", file: "+file;
			Log.d(TAG,logStr);
			
			String packageName = null, processName = null;
			ApplicationErrorReport report = Utils.getApplicationErrorReport(intent);
			if(report != null) {
			    packageName = report.packageName;
			    processName = report.processName;
			}
            
			try {
				uploadErrorReport(tag, fromDropBox, time, file, msg, position, packageName, processName, key, iv);
			} catch(OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
		else if (ReportService.ACTION_TELLHTC_SETTING_CHANGE.equals(action))
		{
		    if(!Utils.isErrorReportSettingEnabled(mContext) && !Utils.isUserProfilingSettingEnabled(mContext))
		        return;
		    
			long currentTime = SystemClock.uptimeMillis();
			Log.d("Tell Htc Setting changed, last time: " + sLastTimeTellHtcSettingChanged + 
					", current time: " + currentTime +", min interval: " + MIN_RESEND_INTERVAL);
			if(sLastTimeTellHtcSettingChanged == -1 || (currentTime - sLastTimeTellHtcSettingChanged) > MIN_RESEND_INTERVAL) {
				sLastTimeTellHtcSettingChanged = currentTime;
				resumeCSCachedReport();
			}
		}
	}

	// for error report
	private void uploadErrorReport(String tag, boolean fromDropBox, long time, String file, String msg, String position, String packageName, String processName, byte[] key, byte[] iv) {
		HandsetLogCreator logCreator = new HandsetLogCreator(mContext);
		String deviceInfo = getDeviceInfoStringWithJSONStyle(msg, position, tag, packageName, processName);
		Buffer buf = this.readErrorReportFile(tag, fromDropBox, time, file, key, iv);
		if(buf == null || buf.getLength() <= 0) {
			Log.d(TAG, "There is no external report file found !");
			return;
		}

		logCreator.add("com.htc.feedback", tag, System.currentTimeMillis(), deviceInfo, buf);
		buf.release();
		buf = null;
		deviceInfo = null;	// help VM to release it ASAP
	
		HandsetLogPKT envelope = logCreator.toHandsetLog(true);
		logCreator = null;	// help VM to release it ASAP
		
		Utils.showAllocateMemory("[uploadErrorReport before upload(envelope, tag)]");
		upload(envelope, tag);
		Utils.showAllocateMemory("[uploadErrorReport after upload(envelope, tag)]");
	}
	
	private Buffer readErrorReportFile(String tag, boolean fromDropBox, long time, String file, byte[] key, byte[] iv) {
		LogEntry logEntry = null;
		InputStream is = null;
		BufferedInputStream bis = null;
		ReleasableByteArrayOutputStream os = null;
		int length = -1;
		try {
			if(!TextUtils.isEmpty(file)) {
				File sourceFile = new File(file);
				if(sourceFile != null)
					length = (int) sourceFile.length();
				logEntry = new LogEntry(file, mContext);
			}
			else if(fromDropBox)
				logEntry = new LogEntry(tag, time, mContext, key, iv);
			else
				return null;
			
			is = logEntry.getInputStream();			
			bis = new BufferedInputStream(is);
			
			// pump data from the input stream to the output stream.
			if(length < 0)
				length = 128*1024;
			Log.d(TAG, "initialized OutputStream size: "+length+" Bytes");
			os = new ReleasableByteArrayOutputStream(length);
			int size;
			byte[] buffer = new byte[4096];
			while ((size = bis.read(buffer, 0, 4096)) != -1) {
				os.write(buffer, 0, size);
			}
			
			// TODO: remove this test codes
//			if(os.size() < 200) {
//				String str = new String(os.getInnerBuffer().getBuffer());
//				Log.d(TAG, "attachment: "+str);
//			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null; // The file may be deleted by drop box, so it may cause null pointer exception here
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null; // The file may be deleted by drop box, so it may cause null pointer exception here
		} catch (IOException e) {
			e.printStackTrace();
			return null; // Be careful that IOException will cause this log will be dismissed
		}
		finally {
			/* Some situations won't send log to Pomelo server, here code might not be executed.
			 * Then the file won't be deleted by anyone. So we delete the file in ReportServiceHandler.
			 * Rex, 2012/10/30
			if (!fromDropBox && !TextUtils.isEmpty(file)) {
				File f = new File(file);
				if(f.exists())
					f.delete();
			}
			*/
			if (logEntry != null){
				logEntry.closeEntry();
				logEntry = null;
			}

			try {
				if(os != null)
					os.close();
				if(bis != null) {
					bis.close();
					bis = null;
				}
				if(is != null) {
					is.close();	
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (RuntimeException e) {
				Log.d(TAG, "[readErrorReportFile] Got runtime exception: " + e.getMessage());
			}
		}
		
		Buffer buf = null;
		if(os!=null) {
			Log.d(TAG, "file size: "+os.size());
			buf = os.getInnerBuffer();
			os.release();		// help VM to release it ASAP
			os = null;
		}
		
		return buf;
	}
	
	private String getDeviceInfoStringWithJSONStyle(String msg, String position, String tag, String packageName, String processName) {
		JSONObject jobject = new JSONObject();
		String message = msg == null ? "" : msg;
		try {
			jobject.put("buildtype", ReflectionUtil.get("ro.build.type", "unknown"));
			jobject.put("changelist", ReflectionUtil.get("ro.build.changelist", "-1"));
			jobject.put("message", message);
			jobject.put("keyset", ReflectionUtil.get("ro.build.tags", "unknown"));
			jobject.put("fingerprint", Build.FINGERPRINT);
			jobject.put("builddateid", ReflectionUtil.get("ro.build.date.utc", "unknown"));
			jobject.put("buildidentify", ReflectionUtil.get("ro.build.project", "unknown"));
			jobject.put("ro.build.buildline", ReflectionUtil.get("ro.build.buildline", "unknown"));
			jobject.put("frameworkversion", ReflectionUtil.get("ro.build.version.release", "unknown"));
			jobject.put("radio", ReflectionUtil.get("gsm.version.baseband","unknown"));
			
			//change from ro.product.device to this for sense3.0 provision
			//jobject.put("modelId", HtcWrapSystemProperties.get("ro.aa.project","unknown"));
			// Add the payload for project
			jobject.put("project",ReflectionUtil.get("ro.product.device","unknown"));
			
			jobject.put("position", position);
			jobject.put("unlocked_device", ReflectionUtil.get("ro.lb","unknown").trim());
			
			jobject.put("free_data_storage", Long.toString(Utils.getFreeSize(Common.STR_DATA_PATH))); // 2011-12-02-pitt, Free storage size of data partition with default value -1
			
			jobject.put("report_type", Utils.getReportType()); // 2011-06-06-pitt, 'com' or 'eng' considered from values of ro.build.type, ro.aa.report and ro.sf
			
	        // begin-added-for-recognizing-updated-hms-from-playstore-2014-01-16
			if(!TextUtils.isEmpty(packageName)) {
    			try{ jobject.put("updatedSystemApp", Boolean.toString(PackageInfoUtil.isUpdatedSystemApp(mContext, packageName))); } catch(Exception e) {Log.e(TAG, "(updatedSystemApp)", e);}
    			try{ jobject.put("isSystemApp", Boolean.toString(PackageInfoUtil.isSystemApp(mContext, packageName))); } catch(Exception e) {Log.e(TAG, "(isSystemApp)", e);}
    			try{ jobject.put("installerPackageName", PackageInfoUtil.getInstallerPackageName(mContext, packageName, Common.STR_UNKNOWN)); } catch(Exception e) {Log.e(TAG, "(installerPackageName)", e);}
    			try{ jobject.put("signedKey", SignatureUtil.getInstance().getSignatureName(mContext, packageName, Common.STR_UNKNOWN)); } catch(Exception e) {Log.e(TAG, "(signedKey", e);}
    			try{ jobject.put("isSignedHtcKey", Boolean.toString(SignatureUtil.getInstance().isSignedHtcKey(mContext, packageName))); } catch(Exception e) {Log.e(TAG, "(isSignedHtcKey)", e);}
    			try{ jobject.put("packageName", !TextUtils.isEmpty(packageName) ? packageName : Common.STR_UNKNOWN); } catch(Exception e) {Log.e(TAG, "(packageName)", e);}
    			try{ jobject.put("packageVersionName", PackageInfoUtil.getVersionName(mContext, packageName, Common.STR_UNKNOWN)); } catch(Exception e) {Log.e(TAG, "(packageVersionName)", e);}
    			try{ jobject.put("packageVersionCode", Integer.toString(PackageInfoUtil.getVersionCode(mContext, packageName, -1))); } catch(Exception e) {Log.e(TAG, "(packageVersionCode)", e);}
			}
			if(!TextUtils.isEmpty(processName))
			    try{ jobject.put("processName", !TextUtils.isEmpty(processName) ? processName : Common.STR_UNKNOWN); } catch(Exception e) {Log.e(TAG, "(processName)", e);}
	        // end-added-for-recognizing-updated-hms-from-playstore-2014-01-16
		} catch (JSONException e) {
			e.printStackTrace();
		}
		//Fix for runTime error, JsonStringer indexoutofboundsException (may be caused by wrong charset or unrecognized character)
    	// Icon_G#1763 
		// To prevent the special character cannot be transferred in Json object, set a "try..catch" clause for runtime error
		String jsonString="";
		try{
			jsonString=jobject.toString();
			if(Common.SECURITY_DEBUG) Log.d(TAG,"[getDeviceInfoStringWithJSONStyle]"+jsonString);
		}catch(Exception e){
			Log.e(TAG,"[getDeviceInfoStringWithJSONStyle]"+"Error to transfer to Json String");
			e.printStackTrace();
		}
		//End of fix
		return jsonString;
	}

	private void upload(HandsetLogPKT envelope, String tag) {
		if(Utils.isNetworkAllowed(mContext) && envelope != null){
			
			/*
			 * Check budget limitation before send
			 * 
			 * TODO: Do not calculate http header size
			 */
			long length = envelope.getSerializedSize();
			
			if ( false == mBudgetManager.isAvailableByCurrentNetwork(1L, length) ) {
				if (_DEBUG) Log.i(TAG, "upload() fail due to no budget for current network");
			} else {
				boolean succeedToUploadFirstLog = false;
				
				// total times = retry times + 1 
				// Note 1, it can prevent retry time equals to zero.
				// Note 2, retry times supposedly range 0 ~ 10.
				int retry = Utils.getRetryTimes(mContext);
				
				// Acauire wake lock for first log only
				PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
				PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Common.WAKELOCK_POMELO);
				wakeLock.setReferenceCounted(false);
				//General upload speed for 3G is 0.3 Mb/s .The max size of file to upload is 2.5MB
				//Time cost for uploading = 66.67x2(safety factor) = 133(s)
				wakeLock.acquire(133000);
				Utils.logDataSizeForWakeLock(Common.WAKELOCK_POMELO,length);
					
				try {
					for(int i=0; i<=retry; i++) {
						if(!Utils.isNetworkAllowed(mContext)){
							if (_DEBUG) Log.d(TAG, "[upload] Stop upload to Pomelo server due to no proper network.");
							continue;
						}
						Log.d(TAG,"Uploaded file size: "+length); //must showed it at begin,added by Ricky 2012.06.27
						if (_DEBUG) Log.d(TAG, "[upload] run "+i);
						if(mCSUploader.putReport(tag, envelope)){
							succeedToUploadFirstLog = true;
							break;
						}
					}
				} finally {
					   if(wakeLock !=null && wakeLock.isHeld()){
						   wakeLock.release();
					}					
						
				}
				
				// I don't want to send cache files in suspend mode so that no wake lock is acquired. 
				if(succeedToUploadFirstLog) {
					resumeCSCachedReport();
					return; // must return or the first log will be stored in cache file
				}
			}
		}
		storeCSReport(envelope, tag);		
	}
	
	private void storeCSReport(HandsetLogPKT envelope, String tag) {
		// TODO: deflate it with zip format
		LogCacheManager.getInstance().putFile(mContext, envelope.toByteArray(), tag);
	}
	
	private void resumeCSCachedReport() {
		// No need to check the network status again since it's checked outside??
		// To fulfill Policy changed event to upload, this is needed 
		if (!Utils.isNetworkAllowed(mContext))
			return;
		
		EntryFile [] fileList = LogCacheManager.getInstance().getFiles(mContext);
		Log.d(TAG, "Start upload resuming queue files. file count: "+fileList.length);
		for(EntryFile file : fileList) {
			if (file != null) {
			    
			    if (!canResumeCSCachedReport(file)) {
			        Log.d(TAG,"Stop to resume file: " + file.getName() + " due to user unchecked the checkbox from settings!");
			        continue;
                }
			    
				Log.d(TAG,"resume file: "+file.getName());
				String tag = file.getTagName() ;//Parse tag from fileName. added by Ricky .2012.08.24
				InputStream is;
				try {
					is = file.getFileInputStreamEx(mContext);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					continue;	// keep this file and just leave
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				} catch (GeneralSecurityException e) {
					e.printStackTrace();
					continue;
				}
				HandsetLogPKT envelope = null;
				if(is != null) {
					try {
						Wire wire = new Wire();
						envelope = wire.parseFrom(is, HandsetLogPKT.class);
						is.close();
					} catch (Exception e) {
						// sy_temp, should delete the file?
						e.printStackTrace();
						continue; // keep this file and just leave
					} finally {
						try{
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
							continue;
						}
					}

					if(envelope != null) {
	
						/*
						 * Check budget limitation before send
						 * 
						 * TODO: Use real size to calculate
						 */
						long length = envelope.getSerializedSize();
						if ( false == mBudgetManager.isAvailableByCurrentNetwork(1L, length) ) {
							if (_DEBUG) Log.i(TAG, "resumeCSCachedReport() fail due to no budget for current network");
							return;
						}

						if(mCSUploader.putReport(tag, envelope)) {
							file.delete();
						}
						else {
							Log.d("break resuming queue files");
							break; // [Power Consumption] if cache files contain too many files, it potentially take long time to upload with unstable network
						}
					}
				}
			}
		}
	}
	
	/** Check whether can resume cached log for Pomelo server.
	 * If user recalls usage report from TellHTC UI, then usage report(HTC_ULOGDATA, HTC_UB) cannot be resumed.
	 * If user recalls error report from TellHTC UI, then error report() cannot be resumed.
	 * rex, 2013/12/11
	 */
	private boolean canResumeCSCachedReport(EntryFile file) {
	    if (Common.STR_ERROR_LOG_TYPE.equals(file.getLogType())) {
	        if (Utils.isErrorReportSettingEnabled(mContext)) 
	            return true;
	    } else if (Common.STR_USAGE_LOG_TYPE.equals(file.getLogType())) {
	        if (Utils.isUserProfilingSettingEnabled(mContext))
	            return true;
	    } else {
	        // This is for backward compatible, due to file name might not contains log type information. 
	        // If log type of file cannot be recognized, then we check its tag name directly.
	        
	        // Use UPolicy to check whether files's tag name is in policy table or not.
	        if (UPolicy.canLogErrorReport(UPolicy.APPID_ERROR_REPORT, file.getTagName(), mContext))
	            return true;
	        
            // If its tag name is not in policy table and equals HTC_ULOGDATA, then this file belongs to error report.	        
	        if (Common.STR_HTC_POMELO_USAGE_LOG.equals(file.getTagName()))
	            if (Utils.isUserProfilingSettingEnabled(mContext))
	                return true;
	    }
	    
	    return false;
	}

	private boolean compareHandsetLogPKT(HandsetLogPKT a, HandsetLogPKT b) {
		DeviceInfo deviceInfo1 = a.device_info;
		DeviceInfo deviceInfo2 = b.device_info;
		boolean isDeviceInfoTheSame = deviceInfo1.equals(deviceInfo2);
		
		if(isDeviceInfoTheSame)
			Log.d("[compareHandsetLogPKT] DeviceInfos are the same");
		else {
			Log.d("[compareHandsetLogPKT] different DeviceInfo");
			Log.d("[compareHandsetLogPKT] First DeviceInfo\n"+deviceInfo1.toString());
			Log.d("[compareHandsetLogPKT] Second DeviceInfo\n"+deviceInfo2.toString());
		}
		
		String ver1 = a.version;
		String ver2 = b.version;
		if(ver1.equals(ver2))
			Log.d("[compareHandsetLogPKT] DeviceInfos are the same");
		
		
		int payloadCount1 = a.payload.size();
		int payloadCount2 = b.payload.size();
		
		if(payloadCount1 == payloadCount2) {
			LogPayload payload1 = null;
			LogPayload payload2 = null;
			for(int i=0; i<payloadCount1; i++) {
				Log.d("[compareHandsetLogPKT] run "+i);
				
				payload1 = a.payload.get(i);
				payload2 = b.payload.get(i);
				
				String appid1 = payload1.app_id;
				String appid2 = payload2.app_id;
				boolean isAppidTheSame = appid1.equals(appid2);
				
				if(!isAppidTheSame) {
					Log.d("[compareHandsetLogPKT] different app id");
					Log.d("[compareHandsetLogPKT] "+appid1);
					Log.d("[compareHandsetLogPKT] "+appid2);
				}
				
				String category1 = payload1.category;
				String category2 = payload2.category;
				boolean isCategoryTheSame = category1.equals(category2);
				if(!isCategoryTheSame) {
					Log.d("[compareHandsetLogPKT] different category");
					Log.d("[compareHandsetLogPKT] "+category1);
					Log.d("[compareHandsetLogPKT] "+category2);
				}
				
				long timestamp1 = payload1.timestamp;
				long timestamp2 = payload2.timestamp;
				boolean isTimestampTheSame = timestamp1 == timestamp2;
				if(!isTimestampTheSame) {
					Log.d("[compareHandsetLogPKT] different timestamp");
					Log.d("[compareHandsetLogPKT] "+timestamp1);
					Log.d("[compareHandsetLogPKT] "+timestamp2);					
				}
				
				String data1 = payload1.data;
				String data2 = payload2.data;
				boolean isDataTheSame = data1.equals(data2);
				if(!isDataTheSame) {
					Log.d("[compareHandsetLogPKT] different data");
					Log.d("[compareHandsetLogPKT] "+data1);
					Log.d("[compareHandsetLogPKT] "+data2);					
				}
			}
		}
		else {
			Log.d("[compareHandsetLogPKT] count1="+payloadCount1+", count2="+payloadCount2);
		}
		
		return false;
	}
	
}
