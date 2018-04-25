package com.htc.feedback;

import java.io.File;

import com.htc.feedback.reportagent.Common;

import android.app.Activity;

public final class Utils {
	
	private final static String TAG = "Utils";
	
    public static boolean isAPNativeCrash(String dropboxTag) {
        return (("HTC_APP_NATIVE_CRASH").equals(dropboxTag) || ("HTC_ROSIE_DIED").equals(dropboxTag) || ("HTC_HOME_RESTART").equals(dropboxTag));
	}
    
    // Return title of Log file
    public static String getLog1Title(Activity activity) {
    	return getLogTitle(activity, getTextString(activity, R.string.log_file_number_1));
	}
    
    // Return title of Tombstone file
	public static String getLog2Title(Activity activity) {
    	return getLogTitle(activity, getTextString(activity, R.string.log_file_number_2));
	}
	
	public static String getLogTitle(Activity activity, String number) {
		String title = getTextString(activity, R.string.log_file);
    	return String.format(title, number);
	}
	
	// Get text from strings.xml by resource Id and change to String
	public static String getTextString(Activity activity, int resId) {
		if (activity != null) {
			CharSequence charSequence = activity.getText(resId);
			if (charSequence != null)
				return charSequence.toString();
		}
		
		return "";
	}
	
	public static void deleteFilesInDir(File dir) {
        try {
            if (dir != null && dir.isDirectory()) {
                File[] files = dir.listFiles();
                if(files != null && files.length > 0) {
                    for(File file : files) {
                        if(file.delete()) {
                            if (Common.DEBUG)
                                Log.d(TAG, "Delete file succeeded, path=" + file.getAbsolutePath());
                        } else
                            Log.d(TAG, "Delete file failed, path=" + file.getAbsolutePath());
                    }
                } else {
                    Log.d(TAG, "No file in the dir");
                }
            } else
                Log.d(TAG, "dir is null or is not a directory");
        } catch (Exception e) {
            Log.e(TAG, "Exception in deleteFilesInDir", e);
        }
    }
}
