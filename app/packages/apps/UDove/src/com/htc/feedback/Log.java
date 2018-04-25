package com.htc.feedback;

public class Log {
	
	public final static String TAG = "UDove";
    
    public static int v(String msg) {
        return android.util.Log.v(TAG, msg);
    }   

    public static int d(String msg) {
        return android.util.Log.d(TAG, msg);
    }   

    public static int i(String msg) {
        return android.util.Log.i(TAG, msg);
    }   

    public static int w(String msg) {
        return android.util.Log.w(TAG, msg);
    }   

    public static int e(String msg) {
        return android.util.Log.e(TAG, msg);
    }   
    
    public static int v(String tag, String msg) {
        return android.util.Log.v(TAG, tag + " " + msg);
    }   

    public static int d(String tag, String msg) {
        return android.util.Log.d(TAG, tag + " " + msg);
    }   

    public static int i(String tag, String msg) {
        return android.util.Log.i(TAG, tag + " " + msg);
    }   

    public static int w(String tag, String msg) {
        return android.util.Log.w(TAG, tag + " " + msg);
    }   

    public static int e(String tag, String msg) {
        return android.util.Log.e(TAG, tag + " " + msg);
    }   
    
    public static int v(String tag, String func, String msg) {
        return android.util.Log.v(TAG, tag + "." + func + " " + msg);
    }   

    public static int d(String tag, String func, String msg) {
        return android.util.Log.d(TAG, tag + "." + func + " " + msg);
    }   

    public static int i(String tag, String func, String msg) {
        return android.util.Log.i(TAG, tag + "." + func + " " + msg);
    }   

    public static int w(String tag, String func, String msg) {
        return android.util.Log.w(TAG, tag + "." + func + " " + msg);
    }   

    public static int e(String tag, String func, String msg) {
        return android.util.Log.e(TAG, tag + "." + func + " " + msg);
    }   
    public static int e(String tag, String msg, Throwable tr){
    	return android.util.Log.e(TAG,tag+"."+msg,tr);
    }
    
}
