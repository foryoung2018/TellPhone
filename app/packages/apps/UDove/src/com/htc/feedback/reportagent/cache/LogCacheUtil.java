package com.htc.feedback.reportagent.cache;

import java.io.File;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.Common;
import com.htc.feedback.policy.ReportPolicy;
//import com.htc.feedback.reportagent.ReportUPolicy;
import com.htc.feedback.policy.UPolicy;
import android.content.Context;
import android.text.TextUtils;

/*package*/ final class LogCacheUtil {
	private LogCacheUtil(){}
	
	/*package*/ static String generateFileName(String tag) {
	    
	    // Add log type information to file name of cached log. 
	    // Be aware of that we treat HTC_UB as error report in engineering ROM, instead of usage report.
	    // rex, 2013/12/11
	    String logType = Common.STR_ERROR_LOG_TYPE;
	    if (Common.STR_HTC_POMELO_USAGE_LOG.equals(tag))
	        logType = Common.STR_USAGE_LOG_TYPE;
	        
		StringBuilder sb = new StringBuilder();
		sb.append(toHexString(System.currentTimeMillis()))
			.append("-")
			.append(tag)
			.append("-")
			.append(logType)
			.append("-")
			.append(UUID.randomUUID());
		return sb.toString();
	}
	
    /**
     * copied from Integer
     */
    private static final char[] DIGITS = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
        'u', 'v', 'w', 'x', 'y', 'z'
    };
    
	/**
	 * modified from Long.toHexString()
     * The purpose is to keep zero of higher 4 bits with whole long integer.
     * In other words, it will output 16 chars totally each time.
	 */
    private static String toHexString(long v) {
        int bufLen = 16;  // Max number of hex digits in a long
        char[] buf = new char[bufLen];

        for(int i=bufLen-1; i>=0; i--) {
        	buf[i] = DIGITS[((int) v) & 0xF];
        	v >>>= 4;
        }

        return new String(buf, 0, bufLen);
    }

    /*package*/ static long getCacheLimitFromPolicy(Context ctx) {
		UPolicy policy = ReportPolicy.getInstance(ctx);
		String cacheSizeStr = policy.getValue("log", "cache");
		long cacheSize = -1;
		if(!TextUtils.isEmpty(cacheSizeStr)) {
			try {
				cacheSize = Long.parseLong(cacheSizeStr);
			} catch(NumberFormatException e) {
				e.printStackTrace();
				cacheSize = -1;
			}
		}
		cacheSize = cacheSize >= 0 ? cacheSize * 1024 * 1024 : cacheSize;
		Log.d("cache limit: "+cacheSize);
		return cacheSize;
    }
    
    /*package*/ static String generateS3LogFileName(String tag) {
		StringBuilder sb = new StringBuilder("");
		if (tag != null)
			sb.append(System.currentTimeMillis()).append("@").append(tag.toUpperCase(Locale.US)).append(".bin");
		return sb.toString();
	}
    
    // sy_wang, 20140321, Append sense ID to log file name for back-end server managing a large amount of files
    /*package*/ static String generateS3LogFileName(Properties prop) {
		StringBuilder sb = new StringBuilder("");
		String tag = prop.getProperty("TAG", null);
		String senseId = prop.getProperty("SENSE_ID", null);
		
		if (tag != null && senseId != null)
			sb.append(System.currentTimeMillis()).append("@").append(tag.toUpperCase(Locale.US)).append("@").append(senseId).append(".bin");
		return sb.toString();
	}
	
    /**
     * High priority log type: <br/>
     * 1. HTC_APP_CRASH <br/>
     * 2. HTC_APP_ANR <br/>
     * 3. SYSTEM_CRASH <br/>
     * 4. HTC_APP_NATIVE_CRASH <br/>
     * 5. HTC_UB <br/>
     * @param file
     * @return
     */
    /*package*/ static boolean isHighPriorityLogType(File file) {
		String[] splitedString = file.getName().split("@");
		if (splitedString.length == 2) {
			if (splitedString[1].indexOf(".") > 0) {
				String tag = splitedString[1].substring(0, splitedString[1].indexOf("."));
				if(Common.STR_HTC_APP_ANR.equals(tag) || Common.STR_HTC_APP_CRASH.equals(tag) 
						|| Common.STR_SYSTEM_CRASH.equals(tag) || Common.STR_HTC_UB.equals(tag) || Common.STR_HTC_APP_NATIVE_CRASH.equals(tag))
					return true;
			}
		}
		return false;
	}
}
