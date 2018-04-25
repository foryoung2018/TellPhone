package com.htc.feedback.reportagent.s3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.app.ApplicationErrorReport;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.DropBoxManager;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.DropBoxManager.Entry;
import android.text.TextUtils;

import com.amazon.s3.Response;
import com.htc.feedback.reportagent.util.DeviceInfoPreference;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.util.Utils;
import com.htc.feedback.reportagent.cache.S3EntryFile;
import com.htc.feedback.reportagent.cache.S3LogCacheManager;
import com.htc.feedback.reportagent.io.LogEntry;
import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.ReportService;
// sy_temp
import com.htc.feedback.reportagent.util.ReflectionUtil;
//import com.htc.wrap.android.os.HtcWrapSystemProperties;

/**
 * @author Peilun_Huang
 */
//public class BugReportService extends Service {
public class BugReportService extends FakeContext {
	final static String TAG = "BugReportService";
	// Notice: Change the version when properties is changed.
	final static String sMessageVersion = "HTC bugreport 1.0.0.10";
	final private static int RETRY_COUNT = 1;
	final private static int MAXLOG_COUNT = 56; // Keep cache count for 7-day UP and UB, 2013/3/28

	private S3Uploader s3uploader = new S3Uploader();

	public BugReportService(Service ctx) {
		super(ctx);
	}
	
	public void onUploadBugReport(Intent intent) {
		new GenericReport(intent).startUploadToS3Server();
	}
	
	public void onConnectivityChange() {
		this.resumeCachedReport(s3uploader);
	}
	
	public void updateDeviceInfoPreference(){
		/*
		 * For device first boot-up, getSentinelValue() must return 'false', so it will update current device info to preference.
		 * For the non-first boot-up, getSentinelValue() should return 'true', if return false, 
		 * it means that device info was update incompletely last time, so it will update current device info to preference again.
		 * Rex, 2013/01/17
		 */
		if(!DeviceInfoPreference.getSentinelValue(getContext()))
			S3DeviceInfoCreator.updateDeviceInfoPreference(getContext());
	}

	/**
	 * This function is keeping for RD internal use.
	 * @return the URL which receive a HTTP post target.
	 */
//	private static String getErrorURL() {
//		String url = HtcWrapSystemProperties.get("persist.htc.bugreport");
//		if( url == null || !url.startsWith("http") )
//			return "http://10.9.26.123/android/bugreport/";
//		return url;
//	}
//	
	private static String getSavedFilename(int count) {
	    return "report-"+count+".bin";
	}

	private void renameCachedFiles(){
		File file0 = getFileStreamPath(getSavedFilename(0));
		if(!file0.exists()) {
			Log.d(TAG, "position 0 has no cache file");
			return;
		}
		
		StringBuilder sb = new StringBuilder(585); // 585 = 25 (title) + 56 (files) x 2 (digits) x 5 ("from" "="">" "to"" ")
		sb.append("[renameCachedFiles] ");
		int filecount=getFilesCount();
		if(filecount<=MAXLOG_COUNT){
    		for (int j=filecount-1; j>=0; j--) {
    			File file = getFileStreamPath(getSavedFilename(j));
    			file.renameTo(getFileStreamPath(getSavedFilename(j+1)));
    			sb.append(j).append("=>").append(j+1).append(" ");
    		}
		}
		Log.d(TAG, sb.toString());
	}

    private void resumeCachedReport(S3Uploader helper) {
        String tag = "", sn = "";
        //boolean isUPOrUB = false;
        DataInputStream in = null;

        Properties prop = new Properties();
        Log.d(TAG, "Start upload resuming queue files.");
        //Update : check how many files in the queue and resume it
        S3EntryFile [] fileList = S3LogCacheManager.getInstance().getFiles(getContext());
        int filecount = fileList == null ? 0 : fileList.length;
        Log.d(TAG, "Start upload resuming queue files.Totl file count is "+filecount);

        //[CQG #25413192] REX,2013/06/24
        if (filecount == 0)
            return;

        for (S3EntryFile entryFile: fileList) {
            //[CQG #25413192] REX,2013/06/24
            if (entryFile == null)
                continue;

            Log.d(TAG,"[Start upload resuming queue files.]The file path: " + entryFile.getAbsolutePath());
            if (!entryFile.exists()){
                Log.d(TAG,"file cannot find, " + entryFile.getAbsolutePath());
              continue;
            }
            for (int i=RETRY_COUNT; i>=0; i--) {
                try {
                    in = new DataInputStream(entryFile.getFileInputStream());
                    tag = in.readUTF();
                    sn = in.readUTF();
                    //isUPOrUB = isUPOrUB(tag);
                    if(!Utils.isNetworkAllowed(getContext(), tag, ReflectionUtil.get("ro.build.description"),entryFile.getFileSize())) {
                        Log.d(TAG,"[resumeCachedReport] Stop upload due to no proper network");
                        continue;
                    }

                    prop.clear();
                    prop.setProperty("TAG", tag);
                    prop.setProperty("S/N", sn);
                    // sy_wang, 20140321, Get sense ID from the cached log file name
                    prop.setProperty("SENSE_ID", getSenseIdFromCachedFileName(entryFile.getName()));
                    if(Common.SECURITY_DEBUG) Log.d(TAG, prop.toString());
                    Response response = s3uploader.putReport(entryFile.getFilePointer(), prop, 30000);
                    if(response != null && response.getResponseCode() == HttpURLConnection.HTTP_OK){
                        Log.v(TAG, "Upload Done , TAG="+tag);
                        Log.d(TAG,"Upload The absolute file path: " + entryFile.getAbsolutePath());
                        entryFile.delete();
                        break; //break from retry loop
                    }
                    else{
                        Log.v(TAG, "Upload fail retry"+entryFile.getAbsolutePath());
                        // If the return code is not HTTP_OK, retry
                        if (i==0) {
                            deleteHtcPowerExpertTempFile(tag, entryFile); //Rex, Delete cache file directly when uploading HTC_POWER_EXPERT log failed, 2013/02/26
                            Log.w(TAG, "resumeCachedReport fail (upload on the s3 server failed)");
                        }
                      android.os.SystemClock.sleep(1000);
                    }
                } catch (Exception e){
                    Log.v(TAG, "get exception when resume cached files "+entryFile.getAbsolutePath(),e.getMessage());
                    //If meeting any exception(result is null, maybe socket timeout), go through this tunnel and retry
                    if (i==0) {
                        deleteHtcPowerExpertTempFile(tag, entryFile); //Rex, Delete cache file directly when uploading HTC_POWER_EXPERT log failed, 2013/02/26
                        Log.w(TAG, "resumeCachedReport exception",e.getMessage());
                    }
                    //For debug use
                    android.os.SystemClock.sleep(1000);
                } finally {
                    if(in != null)
                        try { in.close(); } catch (IOException e) {    e.printStackTrace();}
                }
            }
        }
    }
    /**
     * This new upload method is upload file object by S3 but not byte array to keep memory low (shooter's outofMemory)
     * Call this method to upload the prepared log files, this method also create deviceInfo.properties 
     * @param tag Indicate the log type.
     * @param message Description of the log.
     * @param log Log file in byte[]
     */
    private void upload(String tag, String message, String position, String unique_msg, LogEntry [] inputLogEntry, String [] zipEntryNames, int triggerType, String packageName, String processName){

        if (!S3Policy.canLogToS3(getContext(), tag))
            return;
        if( inputLogEntry == null || zipEntryNames == null) {
            Log.d(TAG, "inputLogEntry or zipEntryNames is null");
            return;
        }

        if (message==null)
            message="";
        if (position==null)
            position="";

        String sn = Utils.getSN();
        //Create a device information properties file
        ByteArrayOutputStream os_deviceinfo = new ByteArrayOutputStream();
        S3DeviceInfoCreator deviceInfoCreator = S3DeviceInfoCreator.getInstance(getContext());
        Properties propReportData = deviceInfoCreator.createDeviceInfoProperties(tag, message, position, unique_msg, triggerType, packageName, processName);
        try {
            propReportData.store(os_deviceinfo, sMessageVersion);
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {if(os_deviceinfo != null) os_deviceinfo.close();} catch (IOException e) {e.printStackTrace();}
        }

        if(Common.SECURITY_DEBUG) Log.d(TAG, propReportData.toString());

        Properties prop = new Properties();
        prop.setProperty("TAG", tag);
        prop.setProperty("S/N", sn);
        // sy_wang, 20140321, Get sense version and change to sense ID which has specific format.
        prop.setProperty("SENSE_ID", changeSenseVerToSenseId(Utils.getSenseVersionByCustomizationManager()));

        S3EntryFile entryFile = null;
        FileOutputStream zipos = null;
        DataOutputStream dataStream = null;
        ZipOutputStream zip = null;
        InputStream isInputLog = null;
        try {
            //Create a ZIP
            entryFile = S3LogCacheManager.getInstance().putS3LogToCache(getContext(), prop);
            zipos = entryFile.getFileOutputStream();
            dataStream = new DataOutputStream(zipos);
            dataStream.writeUTF(prop.getProperty("TAG", "ALL"));
            dataStream.writeUTF(prop.getProperty("S/N", "unknown"));
            zip =new ZipOutputStream(zipos);
            ZipEntry zeProperties = new ZipEntry("DeviceInfo.properties");
            zip.putNextEntry(zeProperties);
            zip.write(os_deviceinfo.toByteArray());
            zip.closeEntry();

            for(int i=0; i<inputLogEntry.length ; i++) {
                 if(inputLogEntry[i] != null) {
                    ZipEntry zeLogfile = new ZipEntry(zipEntryNames[i]);
                    zip.putNextEntry(zeLogfile);
                      isInputLog = inputLogEntry[i].getInputStream();
                       streamCopy(isInputLog,zip);
                       if(isInputLog != null) {
                           isInputLog.close();
                           isInputLog = null;
                       }
                    zip.closeEntry();
                }
            }
        } catch(Exception e) {
            Log.e(TAG, "Fail to compress Logfile and DeviceInfo", e);
        } finally {
            try {if(isInputLog != null) isInputLog.close();} catch (IOException e) {e.printStackTrace();}
            try {if(zip != null) zip.finish();} catch (IOException e) {e.printStackTrace();}
            try {if(dataStream != null) dataStream.close();} catch (IOException e) {e.printStackTrace();}
            try {if(zipos != null) zipos.close();} catch (IOException e) {e.printStackTrace();}
        }

        //General upload speed for 3G is 0.3 Mb/s .The max size of file to upload is 2.5MB
        //Time cost for uploading = 66.67x2(safety factor) = 133(s)
        String wakelockName = Common.WAKELOCK_S3;
        int wakelockTime = 133000;
        /*
           [2017.02.21 eric lu]
           For UB, we reduce wakelock to 30(s)to prevent power team issues.

           If the wakelock exceed 360(s) in 48 hours, we will need to explain why we need to acquire it.
           Hence, 360(s)/2 = 180(s)/day ,and we will upload 4 times per day
           180/4 = 45(s)/once
           Actually, uploading time was seldom exceed 5(s), we acquire 30(s) as a trade-off.
         */
        if (Common.STR_HTC_UB.equals(tag)){
            wakelockName = Common.WAKELOCK_S3_UB;
            wakelockTime = 30000;
        }

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, wakelockName);
        wakeLock.setReferenceCounted(false);
        wakeLock.acquire(wakelockTime);

        try {
            Log.d(TAG, "Start upload");
            Log.d(TAG,"The size of uploaded file is: "+entryFile.getFileSize()+"(bytes)");
            Utils.logDataSizeForWakeLock(wakelockName,entryFile.getFileSize());

            for (int i=RETRY_COUNT; i>=0; i--) {
                try {
                    if (!Utils.isNetworkAllowed(getContext(), tag, ReflectionUtil.get("ro.build.description"),entryFile.getFileSize())){
                        Log.d(TAG,"[upload] Stop upload due to no proper network");
                        continue;
                    }
                    Log.d(TAG,"uploaded file size: "+entryFile.getFileSize()+"(bytes)"); //must showed it at begin,added by Ricky 2012.06.27
                    Response response = s3uploader.putReport(entryFile.getFilePointer(), prop, 30000);
                    //Compare the header

                    if (response != null && response.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        entryFile.delete();
                        Log.v(TAG, "Upload Done , TAG="+tag);
                        resumeCachedReport(s3uploader);
                        break;
                    }
                    else 
                    {
                        if (i==0) {
                            deleteHtcPowerExpertTempFile(tag, entryFile); //Rex, Delete cache file directly when uploading HTC_POWER_EXPERT log failed, 2013/02/26
                            Log.w(TAG, "fail ");
                            // storeReport(prop);do nothing since already stored
                        }
                    }
                    android.os.SystemClock.sleep(1000);
                } catch (Exception e) {
                    if (i==0) {
                        deleteHtcPowerExpertTempFile(tag, entryFile); //Rex, Delete cache file directly when uploading HTC_POWER_EXPERT log failed, 2013/02/26
                           Log.w(TAG, "Got exception",e.getMessage());
                           //storeReport(prop); do nothing since already stored
                    }
                    android.os.SystemClock.sleep(1000);
                }
            }
        } catch (Exception e) {
            Log.e(TAG,"Exception occurs", e);
        } finally {
               if(wakeLock !=null && wakeLock.isHeld())
                   wakeLock.release();
        }
    }
    
	private class GenericReport {
		String tag = null;
		long time = -1;
		String tags[] = null;
		long times[] = null;
		String zipEntryNames[];
		final String msg;
		final String file;
		boolean fromDropBox = false;
		final String position;
		private final String mUniqueMsg;
		final int triggerType;
		String action;
		String packageName;
		String processName;
		byte[] key = null;
		byte[] iv = null;

		GenericReport(Intent intent) {
			action = intent.getAction();
			msg = intent.getStringExtra("msg");
			fromDropBox = intent.getBooleanExtra("fromDropBox", false);				
			file = intent.getStringExtra("file");
			position = intent.getStringExtra("radio");
			mUniqueMsg = intent.getStringExtra("unique_msg");
			triggerType =  intent.getIntExtra("triggerType", -1);
			
            ApplicationErrorReport report = Utils.getApplicationErrorReport(intent);
            if(report != null) {
                packageName = report.packageName;
                processName = report.processName;
            }
			
			if(ReportService.ACTION_UPLOAD_UB_LOG.equals(action)) {
				tag = Common.STR_HTC_UB;
				tags = intent.getStringArrayExtra("tag");
				if (fromDropBox)
					times = intent.getLongArrayExtra("time");
				zipEntryNames = intent.getStringArrayExtra("zipEntryName");
			} else {
				tag = intent.getStringExtra("tag");
				if (fromDropBox) {
					time = intent.getLongExtra("time", -1);
					key = intent.getByteArrayExtra("errorReportKey");
					iv = intent.getByteArrayExtra("errorReportIv");
				}
			}
		}
		
		// No more byte array passed through since Shooter's outofMemory exeception
		// Use cached file to upload to prevent it
		public synchronized void startUploadToS3Server() {
		    LogEntry [] logEntrys = null;
			try {
                if(ReportService.ACTION_UPLOAD_UB_LOG.equals(action)) {
                    if (tags == null || times == null || zipEntryNames == null || 
                            tags.length != times.length || tags.length != zipEntryNames.length || 
                            zipEntryNames.length != tags.length || !isZipEntryNameAllowed(zipEntryNames)) {
                        Log.d(TAG, "Stop uploading UB log --> tags array or times array or zip entry array isn't allowed.");
                        return;
                    }
                    if(Common.DEBUG && tags != null && times != null && tags.length == times.length) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("[UB Report] Ready to upload --> ");
                        for(int i=0; i<tags.length; i++) {
                            sb.append("tag: ").append(tags[i]).append(", time: ").append(times[i]).append(" ");
                        }
                        Log.d(TAG,sb.toString());
                    }
                    logEntrys = new LogEntry[tags.length];
                    for(int i=0; i<tags.length; i++)
                        logEntrys[i] = new LogEntry(tags[i], times[i], getContext(), key, iv);
                }
                else {
                    logEntrys = new LogEntry[1];
    				if(file != null)
    				    logEntrys[0] = new LogEntry(file, getContext());
    				else if (fromDropBox)
    				    logEntrys[0] = new LogEntry(tag, time, getContext(), key, iv);
    				zipEntryNames = new String [1];
    				zipEntryNames[0] = "Logfile";
                }
                
                upload(tag, msg, position, mUniqueMsg, logEntrys, zipEntryNames, triggerType, packageName, processName);
			}
			catch (Exception e) {
				Log.e(TAG, "Fail to create LogEntry", e);
			} finally {
			    if(logEntrys != null) {
                    for(LogEntry logEntry : logEntrys)
                        logEntry.closeEntry();
			    }
			}
	    }
	    
		// TODO: If this function is going to deal with other team's file name, we should shouldn't allow 
		// symbols such as '^' '[' ']' and so on
        private boolean isZipEntryNameAllowed(String [] zipEntryNames) {
            if(zipEntryNames != null)
                for(int i=0; i<zipEntryNames.length; i++) {
                    if(TextUtils.isEmpty(zipEntryNames[i])) {
                        Log.d(TAG, "zipEntryName "+i+"is empty or null");
                        return false;
                    }
                    for(int j=i+1; j<zipEntryNames.length ; j++){
                        Log.d(TAG, "compare #"+i+" : "+zipEntryNames[i]+" with #"+j+" : "+zipEntryNames[j]); // TODO : remove this line
                        if(zipEntryNames[i] != null && zipEntryNames[i].equals(zipEntryNames[j])) {
                            Log.d(TAG, "zipEntryName can't be duplicate, "+i+" : "+zipEntryNames[i]+" with "+j+" : "+zipEntryNames[j]);
                            return false;
                        }
                    }
                }
            return true;
        }
	}

	//end of hacker device's filtering
	// This code is for save memory, just copy the inputstream to outputstream 
	// Will be used when zipping file (feed in OutputStream as ZipEntry's input)
	private void streamCopy(InputStream in, OutputStream out) throws IOException {	    
		synchronized (in) {
			synchronized (out) {
				byte[] buffer = new byte[256];
				while (true) {
	        		int bytesRead = in.read(buffer);
	        		if (bytesRead == -1) break;
	        		out.write(buffer, 0, bytesRead);
	        	}
	      	}
	    }
	}
	  // End of StreamCopy
    
    private static void deleteHtcPowerExpertTempFile(String tag, S3EntryFile entryFile){
    	// sy_wang, 20140328, Add new tag HTC_PWR_EXPERT
    	if(Common.STR_HTC_POWER_EXPERT.equals(tag) || Common.STR_HTC_PWR_EXPERT.equals(tag))
    		entryFile.delete();
    }
    
    // sy_wang, 20140321, Get sense ID from cached log file name
    private static String getSenseIdFromCachedFileName(String fileName) {
    	String senseId = Common.STR_DEFAULT_SENSE_ID;
    	
    	if(TextUtils.isEmpty(fileName))
    		return senseId;
    	
    	try {
			int idx = fileName.indexOf(Common.STR_SENSE_ID_PREFIX);
			// Has sense version in file name
		    if(idx > -1 && idx + 8 <= fileName.length())
		    	senseId = fileName.substring(idx, idx+8);
		    // No sense version in file name, it should be old format file. Don't set senseId.
		    else
		    	senseId = "";
		} catch (Exception e) {
			Log.e(TAG, "Exception in getSenseIdFromCachedFileName", e);
		}
    	
    	return senseId;
    }
    // sy_wang, 20140321, Change sense version to sense ID
    private static String changeSenseVerToSenseId(String senseVer) {
    	String senseId = Common.STR_DEFAULT_SENSE_ID;
    	
    	if(TextUtils.isEmpty(senseVer))
    		return senseId;
    	
    	try {
			String sense = "";
			int idx = senseVer.indexOf(".");
			
			// Expected case is at most 3 digits on dot's left and at least 1 digit on dot's right, ex:
			// 6.5   -> HSV_0065
			// 10.0  -> HSV_0100
			// 10.03 -> HSV_0100
			// 123.1 -> HSV_1231
			if(idx > 0 && idx < 4 && idx+1 < senseVer.length())
				sense = senseVer.substring(0, idx) + senseVer.substring(idx+1, idx+2);
			// Unexpected case, get the first 4 digits from left
			else
				sense = senseVer.substring(0, senseVer.length() > 4 ? 4 : senseVer.length());
			
			senseId = Common.STR_SENSE_ID_PREFIX + "0000".substring(0, 4 - sense.length()) + sense;
		} catch (Exception e) {
			Log.e(TAG, "Exception in changeSenseVerToSenseId", e);
		}
    	
    	return senseId;
    }
}
