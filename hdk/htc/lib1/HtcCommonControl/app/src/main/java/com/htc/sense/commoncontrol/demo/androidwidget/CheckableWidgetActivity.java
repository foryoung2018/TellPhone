package com.htc.sense.commoncontrol.demo.androidwidget;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Switch;

import com.htc.lib1.cc.support.widget.HtcTintManager;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class CheckableWidgetActivity extends CommonDemoActivityBase {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_checkable_widget);

        HtcTintManager htcTintManager = HtcTintManager.get(CheckableWidgetActivity.this);
        RadioButton radioButton = (RadioButton) findViewById(R.id.radiobutton1);
        htcTintManager.tintThemeColor(radioButton);
        RadioButton radioButton1 = (RadioButton) findViewById(R.id.radiobutton3);
        htcTintManager.tintThemeColor(radioButton1);


        CheckBox checkBox = (CheckBox) findViewById(R.id.cb1);
        htcTintManager.tintThemeColor(checkBox);

        CheckBox checkBox2 = (CheckBox) findViewById(R.id.cb2);
        htcTintManager.tintThemeColor(checkBox2);


        Switch aSwitch = (Switch) findViewById(R.id.sw1);
        htcTintManager.tintThemeColor(aSwitch);
        Switch aSwitch2 = (Switch) findViewById(R.id.sw2);
        htcTintManager.tintThemeColor(aSwitch2);

    }
}
