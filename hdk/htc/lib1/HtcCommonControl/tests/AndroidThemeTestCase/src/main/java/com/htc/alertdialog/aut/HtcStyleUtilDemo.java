package com.htc.alertdialog.aut;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Switch;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.util.HtcStyleUtil;

public class HtcStyleUtilDemo extends ActivityBase {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_htcstyleutil);
        RadioButton radioButton = (RadioButton) findViewById(R.id.radiobutton1);
        HtcStyleUtil.applyHtcStyle(this, radioButton);
        RadioButton radioButton1 = (RadioButton) findViewById(R.id.radiobutton3);
        HtcStyleUtil.applyHtcStyle(this, radioButton1);
        CheckBox checkBox = (CheckBox) findViewById(R.id.cb1);
        HtcStyleUtil.applyHtcStyle(this, checkBox);
        Button bt = (Button) findViewById(R.id.custombutton5);
        HtcStyleUtil.applyHtcStyle(this, bt);
        Switch mGoogleSwitchLight = (Switch) findViewById(R.id.myGoogleSwitchLight);
        HtcStyleUtil.applyHtcStyle(this, mGoogleSwitchLight);

    }
}
