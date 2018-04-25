package com.htc.feedback;

import org.json.JSONObject;
import android.content.Context;
import android.telephony.TelephonyManager;
import com.htc.feedback.reportagent.Common;

public class AdditionalInfo {
    private final static String TAG = "AdditionalInfo";
    private final static boolean DEBUG = Common.DEBUG;

    private static int mPhoneType = 0;
    private static int mMcc = 0;
    private static int mMnc = 0;

    public AdditionalInfo(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneType = telephonyManager.getPhoneType();

        if (mPhoneType == TelephonyManager.PHONE_TYPE_GSM) {
            String operatorString = telephonyManager.getNetworkOperator();
            if (DEBUG)
                Log.d(TAG, "netowrk operator = " + operatorString);

            if (operatorString != null && operatorString.length() > 0) {
                try {
                    mMcc = Integer.parseInt(operatorString.substring(0, 3));
                    mMnc = Integer.parseInt(operatorString.substring(3));
                } catch (Exception e) {
                    Log.e(TAG, "parse mnc mcc exception", e);
                }
            }
        }
    }

    public int getPhoneType() {
        return mPhoneType;
    }

    public int getMCC() {
        return mMcc;
    }

    public int getMNC() {
        return mMnc;
    }

    public static String getLocationInformation() {
        JSONObject radio = null;
        try {
            if (mPhoneType == TelephonyManager.PHONE_TYPE_GSM) {
                radio = new JSONObject();
                radio.put("MCC", mMcc);
                radio.put("MNC", mMnc);
            }
        } catch (org.json.JSONException e) {
            radio = null;
        }

        if (radio != null) {
            return radio.toString();
        } else {
            return null;
        }
    }

}
