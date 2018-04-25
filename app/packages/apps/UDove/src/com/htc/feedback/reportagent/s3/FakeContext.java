package com.htc.feedback.reportagent.s3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;

public class FakeContext {

	private Service mService;
	protected int MODE_PRIVATE;

	public FakeContext(Service ctx) {
		mService = ctx;
		MODE_PRIVATE = mService.MODE_PRIVATE;
	}
	
	protected ContentResolver getContentResolver(){
		return mService.getContentResolver();
	}
	
	protected File getFileStreamPath(String name) {
		return mService.getFileStreamPath(name);
	}
	
	protected Object getSystemService(String name) {
		return mService.getSystemService(name);
	}
	
	protected Context getApplicationContext() {
		return mService.getApplicationContext();
	}
	
	protected void stopSelf() {
		// TODO: check it later
	}
	
	protected File getFilesDir() {
		return mService.getFilesDir();
	}
	
	public int getFilesCount(){
		 //[CQG #84],fixed by Ricky,2012,10.02
		File f = null;
		File[] files = null;
		if (mService != null) {
			f = mService.getFilesDir();
			if (f != null) {
				files = f.listFiles();
				if (files != null)
					return files.length;
			}
		}
			return 0;
	}
	
	protected Context getContext() {
		return mService;
	}
	
	protected Service getService() {
		return mService;
	}
}
