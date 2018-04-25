package com.htc.feedback.reportagent.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtil {
    
    private static final String CLASS = "android.os.SystemProperties";
    private static Class<?> sSystemPropertiesClass;
    private static Method sGet;
    private static Method sGetWithDefault;
    private static Method sGetBoolean;
    private volatile static boolean sIsInit;
    
    private ReflectionUtil() {
    
    }
    
    private static void init() {// throws ClassNotFoundException, NoSuchMethodException {
        if (!sIsInit) {
            synchronized(ReflectionUtil.class) {
                if (!sIsInit) {
                    try {
						if (sSystemPropertiesClass == null)
						    sSystemPropertiesClass =  Class.forName(CLASS);
						
						if (sGet == null && sSystemPropertiesClass != null)
						    sGet = sSystemPropertiesClass.getMethod("get", String.class);
						
						if (sGetWithDefault == null && sSystemPropertiesClass != null)
							sGetWithDefault = sSystemPropertiesClass.getMethod("get", String.class, String.class);
						
						if (sGetBoolean == null && sSystemPropertiesClass != null)
						    sGetBoolean = sSystemPropertiesClass.getMethod("getBoolean", String.class, boolean.class);
						
						sIsInit = true;
					} catch (Exception e) {
						Log.d("[SystemProperties] init fail, " + e.getMessage());
					}
                }
            }
        }
    }
    
    public static String get(String key) {// throws ClassNotFoundException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        init();
        String result = "";
        try {
			Object obj = sGet.invoke(null, key);
			if (obj != null && obj instanceof String)
			    result = (String) obj;
		} catch (Exception e) {
			Log.d("[SystemProperties] get(String key) fail, " + e.getMessage());
		}
        return result;
    }
    
    public static String get(String key, String defValue) {// throws ClassNotFoundException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        init();
        String result = defValue;
        try {
			Object obj = sGetWithDefault.invoke(null, key, defValue);
			if (obj != null && obj instanceof String)
			    result = (String) obj;
		} catch (Exception e) {
			Log.d("[SystemProperties] get(String key, String defValue) fail, " + e.getMessage());
		}
        return result;
    }
    
    public static boolean getBoolean(String key, boolean defValue) {// throws ClassNotFoundException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        init();
        boolean result = defValue;
        try {
			Object obj = sGetBoolean.invoke(null, key, defValue);
			if (obj != null && obj instanceof Boolean)
			    result = ((Boolean)obj).booleanValue();
		} catch (Exception e) {
			Log.d("[SystemProperties] getBoolean(String key, boolean defValue) fail, " + e.getMessage());
		}
        return result;
    }

}
