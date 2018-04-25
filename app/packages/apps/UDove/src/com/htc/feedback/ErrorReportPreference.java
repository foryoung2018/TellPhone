package com.htc.feedback;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import android.content.Context;
import android.content.SharedPreferences;

public class ErrorReportPreference {
	private final static String TAG = "ErrorReportPreference";
	private final static String preferenceName = "ErrorReport";
	private final static String keyHasCheckedLogFolder = "HasCheckedLogFolder";
	private static String KEY_SECRET_KEY = "SecretKey";
	private static String KEY_INITIALIZATION_VECTOR = "InitializationVector";
	
	public static boolean getHasCheckedLogFolder(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
    	boolean value = preferences.getBoolean(keyHasCheckedLogFolder, false);
    	return value;
	}
	
	static void setHasCheckedLogFolder(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(keyHasCheckedLogFolder, true);       
        editor.commit();
	}
	
    protected static byte[] generateRawKey() throws NoSuchAlgorithmException{
    	KeyGenerator kgen = KeyGenerator.getInstance("AES");  
        SecureRandom sr = new SecureRandom();
        kgen.init(256, sr); // Use 256 bits key length for better security  
        SecretKey skey = kgen.generateKey();  
        byte[] raw = skey.getEncoded();
        return raw;
    }
    
    protected static byte[] generateIV() {
    	byte[] iv = new byte[16];
    	SecureRandom sr = new SecureRandom();
    	sr.nextBytes(iv);
    	return iv;
    }
	
	private static void setSecretKey(Context context, byte[] rawKey) {
		// Save the secret key once only.
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_SECRET_KEY, byteArrayToHexString(rawKey));
        editor.commit();
	}
	
	public static byte[] getSecretKey(Context context) {
		byte[] rawKey = null;
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
    	String value = preferences.getString(KEY_SECRET_KEY, "");
		if(!"".equals(value))
			rawKey = hexStringToByteArray(value);
		else {
			// Set secret key for first time
			try {
				rawKey = generateRawKey();
				setSecretKey(context, rawKey);
			} catch (NoSuchAlgorithmException e) {
				Log.e(TAG, "Exception in getSecretKey", e);
			}
		}
		return rawKey;
	}
	
	protected static void setIV(Context context, byte[] iv) {
		// Save the iv once only.
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_INITIALIZATION_VECTOR, byteArrayToHexString(iv));
        editor.commit();
	}
	
	public static byte[] getIV(Context context) {
		byte[] iv = null;
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
    	String value = preferences.getString(KEY_INITIALIZATION_VECTOR, "");
		if(!"".equals(value))
			iv = hexStringToByteArray(value);
		else {
			// Set IV for first time
			iv = generateIV();
			setIV(context, iv);
		}
		return iv;
	}
	
	private static String byteArrayToHexString(byte[] b) {
	      String hs = "";
	      String stmp = "";
	      for (int n = 0; n < b.length; n ++) {
	   
	        stmp = Integer.toHexString(b[n] & 0xFF);
	        if (stmp.length() == 1)
	             hs += ("0" + stmp);
	        else
	             hs += stmp;
	        }
	      return hs;
	}
	
	private static byte[] hexStringToByteArray(String s) {
      int len = s.length();
      byte[] data = new byte[len / 2];
      for (int i = 0; i < len; i += 2) {
          data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                  + Character.digit(s.charAt(i+1), 16));
      }
      return data;
  }
}
