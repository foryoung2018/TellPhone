package com.htc.feedback.reportagent.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class ThreadUtil {
	private static ThreadUtil sThreadHandler;
	private ThreadUtil() {
    	if(mHThread == null){
    		mHThread = new HandlerThread("ReportAgentThread");
    		mHThread.start();
    	}
    	if(mHandler == null)
    		mHandler = new Handler(mHThread.getLooper());
	}

	public static ThreadUtil getInstance() {
		if(sThreadHandler == null)
			sThreadHandler = new ThreadUtil();
		return sThreadHandler;
	}
	
	public void post(Runnable r) {
		mHandler.post(r);
	}
	
	public Looper getLooper(){
		return mHandler.getLooper();
	}
	
    private HandlerThread mHThread;
    private Handler mHandler;
}
