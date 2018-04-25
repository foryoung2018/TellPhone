package com.htc.feedback.reportagent.s3;

import android.text.TextUtils;

import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.util.ReportConfig;
import com.amazon.s3.AWSAuthConnection;
import com.amazon.s3.CallingFormat;
import com.amazon.s3.Response;
import com.amazon.s3.S3Object;
import com.amazon.s3.Utils;

import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;


class S3Uploader {
	private final static String TAG = "S3Uploader";
	static final String bucketName4Testing = "htc-error-log-testing-bucket";
	// sy_wang, 20140321, Distribute logs of HTC_POWER_EXPERT, HTC_UP and HTC_UB to different buckets
	static final String powerBucketName4Testing = "tellhtc-tbk-power";
	static final String upBucketName4Testing = "tellhtc-tbk-up";
	static final String ubBucketName4Testing = "tellhtc-tbk-ub";
	
	static final String bucketName4Releasing = "htc-error-log-releasing-bucket";

	private final AWSAuthConnection awsConn;

	public S3Uploader() {
		// Don't show plain text of Key Id and Key, so convert the string to byte array and do XOR.
		// Key Id
		byte[] b1 = new byte[] {
				(byte)0x20, (byte)0x2a, (byte)0x70, (byte)0x78, (byte)0x28, (byte)0x2d, (byte)0x27, (byte)0x2c, 
				(byte)0x0d, (byte)0x73, (byte)0x2c, (byte)0x22, (byte)0x01, (byte)0x76, (byte)0x2d, (byte)0x35, 
				(byte)0x26, (byte)0x54, (byte)0x6c, (byte)0x68
		};
		// Access Key
		byte[] b2 = new byte[] {
				(byte)0x52, (byte)0x29, (byte)0x78, (byte)0x63, (byte)0x16, (byte)0x26, (byte)0x59, (byte)0x13, 
				(byte)0x7a, (byte)0x72, (byte)0x08, (byte)0x13, (byte)0x44, (byte)0x72, (byte)0x01, (byte)0x09, 
				(byte)0x04, (byte)0x2d, (byte)0x6f, (byte)0x52, (byte)0x07, (byte)0x1a, (byte)0x0b, (byte)0x10, 
				(byte)0x50, (byte)0x7c, (byte)0x2a, (byte)0x35, (byte)0x07, (byte)0x43, (byte)0x05, (byte)0x2d, 
				(byte)0x0c, (byte)0x53, (byte)0x75, (byte)0x7a, (byte)0x21, (byte)0x33, (byte)0x0d, (byte)0x08
		};
		
		awsConn = new AWSAuthConnection(getOutput(b1), getOutput(b2), 
				true, Utils.DEFAULT_HOST, CallingFormat.getSubdomainCallingFormat());
	}
	
	// Do XOR on byte array then convert it to string
	private String getOutput(byte[] input) {
		byte[] str = new byte[] {
				(byte)0x61, (byte)0x61, (byte)0x39, (byte)0x39, (byte)0x62, (byte)0x62, (byte)0x61, (byte)0x61, 
				(byte)0x39, (byte)0x39, (byte)0x66, (byte)0x66, (byte)0x33, (byte)0x33, (byte)0x63, (byte)0x63
		};
		byte[] output = new byte[input.length];
		for(int i = 0; i < input.length; i++) {
			output[i] = (byte) (input[i] ^ str[i % str.length]);
		}
		return new String(output, StandardCharsets.US_ASCII);
	}

	// Modify from putReportByHttpClient
   public Response putReport(File file,Properties prop,int timeout) throws IOException {
        String sFileName = "";
        // sy_wang, 20140321, Append sense ID to log file name for back-end server managing a large amount of files
        String senseId = prop.getProperty("SENSE_ID", "");
        sFileName = prop.getProperty("TAG", "ALL")+"-"+prop.getProperty("S/N", "unknown")+"-"+UUID.randomUUID()+(TextUtils.isEmpty(senseId)?"":"-"+senseId)+".zip";
        if(Common.DEBUG) 
            Log.v(TAG, prop.toString()+" key="+sFileName);

        boolean isShippingRom = ReportConfig.isShippingRom();
        String bucketName=bucketName4Testing;
        if (!isShippingRom){
            // sy_wang, 20140321, Distribute logs of HTC_POWER_EXPERT, HTC_UP and HTC_UB to different buckets
            String tag = prop.getProperty("TAG", "ALL");
            // sy_wang, 20140328, Add new tag HTC_PWR_EXPERT
            if (Common.STR_HTC_POWER_EXPERT.equals(tag) || Common.STR_HTC_PWR_EXPERT.equals(tag)) {
                bucketName = powerBucketName4Testing;
            } else if (Common.STR_HTC_UP.equals(tag)) {
                bucketName = upBucketName4Testing + String.format("%02d", (int) (Math.random()*10));
            } else if (Common.STR_HTC_UB.equals(tag)) {
                bucketName = ubBucketName4Testing + String.format("%02d", (int) (Math.random()*10));
            } else {
                bucketName=bucketName4Testing;
            }
        }else if (isShippingRom){
            bucketName=bucketName4Releasing;
        }else {
            bucketName=bucketName4Testing;
        }

        return putObject(bucketName, sFileName, file, timeout);
    }

    /**
     * Putting an Object to s3.amazonaws.com, Currently, we support hosted-style
     * request only, not path-style request.
     * 
     * @param bucketName
     *            please reference
     *            http://docs.amazonwebservices.com/AmazonS3/latest
     *            /dev/index.html?LocationSelection.html for Bucket Restrictions
     *            and Limitations
     * @param key
     * @param data
     * @throws IOException
     */
    Response putObject(String bucketName, String key, File file, int timeout) throws IOException {
        Map headers = null;
        S3Object object = new S3Object(file, null);
        if(object.contentMD5 != null) {
            headers = new HashMap();
            Vector list = new Vector();
            list.add(object.contentMD5);
            headers.put("Content-MD5", list);
        }

        return awsConn.put(bucketName, key, object, headers, timeout);
    }

}
