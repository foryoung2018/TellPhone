package com.htc.feedback.reportagent.util;
import com.htc.feedback.reportagent.Common;

public class Log {
    
    public static int v(String msg) {
        return android.util.Log.v(Common.TAG, msg);
    }   

    public static int d(String msg) {
        return android.util.Log.d(Common.TAG, msg);
    }   

    public static int i(String msg) {
        return android.util.Log.i(Common.TAG, msg);
    }   

    public static int w(String msg) {
        return android.util.Log.w(Common.TAG, msg);
    }   

    public static int e(String msg) {
        return android.util.Log.e(Common.TAG, msg);
    }   
    
    public static int v(String tag, String msg) {
        return android.util.Log.v(Common.TAG, tag + " " + msg);
    }   

    public static int d(String tag, String msg) {
        return android.util.Log.d(Common.TAG, tag + " " + msg);
    }   

    public static int i(String tag, String msg) {
        return android.util.Log.i(Common.TAG, tag + " " + msg);
    }   

    public static int w(String tag, String msg) {
        return android.util.Log.w(Common.TAG, tag + " " + msg);
    }   

    public static int e(String tag, String msg) {
        return android.util.Log.e(Common.TAG, tag + " " + msg);
    }   
    
    public static int v(String tag, String func, String msg) {
        return android.util.Log.v(Common.TAG, tag + "." + func + " " + msg);
    }   

    public static int d(String tag, String func, String msg) {
        return android.util.Log.d(Common.TAG, tag + "." + func + " " + msg);
    }   

    public static int i(String tag, String func, String msg) {
        return android.util.Log.i(Common.TAG, tag + "." + func + " " + msg);
    }   

    public static int w(String tag, String func, String msg) {
        return android.util.Log.w(Common.TAG, tag + "." + func + " " + msg);
    }   

    public static int e(String tag, String func, String msg) {
        return android.util.Log.e(Common.TAG, tag + "." + func + " " + msg);
    }   
    public static int e(String tag, String msg, Throwable tr){
    	return android.util.Log.e(Common.TAG,tag+"."+msg,tr);
    }
    
}
