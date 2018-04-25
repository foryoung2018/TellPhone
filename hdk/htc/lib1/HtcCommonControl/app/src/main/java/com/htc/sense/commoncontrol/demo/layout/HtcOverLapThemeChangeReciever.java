package com.htc.sense.commoncontrol.demo.layout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HtcOverLapThemeChangeReciever extends BroadcastReceiver {
public final static String ACTION_THEME_CHANGE="android.intent.action.HTCOVERLAP_THEMECHANGE";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_THEME_CHANGE)) {
            intent.setClassName(context.getPackageName(), HtcOverLapDemo.class.getName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
