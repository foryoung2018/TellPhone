package com.htc.lib1.cc.HtcStyleUtil;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Switch;

import com.htc.alertdialog.aut.HtcStyleUtilDemo;
import com.htc.alertdialog.aut.R;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.EventUtil;
import com.htc.test.util.ScreenShotUtil;

public class HtcStyleUtilTest extends HtcActivityTestCaseBase {
    private static final int STATE_REST = 0;
    private static final int STATE_PRESS = 1;
    private static final int STATE_DISABLE = 2;
    private static final int STATE_CHECK = 3;
    private static final int STATE_Radio = 4;
    private static final int STATE_SWITH = 5;
    private RadioButton mRadioButton;
    private CheckBox mCheckbox;
    private Switch mSwitch;

    public HtcStyleUtilTest() {
        super(HtcStyleUtilDemo.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    public void test_button_rest() {
        testButton(R.id.custombutton5, STATE_REST);
    }

    public void test_button_press() {
        testButton(R.id.custombutton5, STATE_PRESS);
    }

    public void test_button_disable() {
        testButton(R.id.custombutton5, STATE_DISABLE);
    }

    public void test_checked() {
        testCheckBox(R.id.cb1, true, STATE_CHECK);
    }

    public void test_unchecked() {
        testCheckBox(R.id.cb1, false, STATE_CHECK);
    }

    public void test_disablechecked() {
        testCheckBox(R.id.cb1, false, STATE_DISABLE);
    }

    public void test_switchOn() {
        testSwitch(R.id.myGoogleSwitchLight, true, STATE_SWITH);
    }

    public void test_switchOff() {
        testSwitch(R.id.myGoogleSwitchLight, false, STATE_SWITH);
    }

    public void test_switchdisable() {
        testSwitch(R.id.myGoogleSwitchLight, false, STATE_DISABLE);
    }

    public void test_radiochecked() {
        testRadioButton(R.id.radiobutton1, true, STATE_Radio);
    }

    public void test_radiounchecked() {
        testRadioButton(R.id.radiobutton1, false, STATE_Radio);
    }

    public void test_radiodisable() {
        testRadioButton(R.id.radiobutton1, false, STATE_DISABLE);
    }

    private void testButton(int id, int state) {
        assertNotNull(mActivity);
        getInstrumentation().waitForIdleSync();

        final Button button = (Button) mSolo.getView(id);
        Log.e("mSwitch", "button" + button);
        switch (state) {
            case STATE_REST:
                ScreenShotUtil.AssertViewEqualBefore(mSolo, button,
                        HtcStyleUtilTest.this);
                break;
            case STATE_PRESS:
                EventUtil.callLongPressed(getInstrumentation(), button,
                        new EventUtil.EventCallBack() {
                            @Override
                            public void onPressedStatus(View view) {
                                getInstrumentation().waitForIdleSync();
                                mSolo.sleep(3000);
                                ScreenShotUtil.AssertViewEqualBefore(mSolo, button,
                                        HtcStyleUtilTest.this);
                            }
                        });
                break;
            case STATE_DISABLE:
                getInstrumentation().runOnMainSync(new Runnable() {
                    @Override
                    public void run() {
                        button.setEnabled(false);
                    }
                });
                getInstrumentation().waitForIdleSync();
                ScreenShotUtil.AssertViewEqualBefore(mSolo, button,
                        HtcStyleUtilTest.this);
                break;
        }
    }

    public void testCheckBox(int id, final boolean isChecked, int state) {
        assertNotNull(mActivity);
        getInstrumentation().waitForIdleSync();
        mCheckbox = (CheckBox) mActivity.findViewById(id);
        Log.e("mSwitch", "mCheckbox" + mCheckbox);
        final String fileName = ScreenShotUtil.getScreenShotName(HtcStyleUtilTest.this);
        switch (state) {
            case STATE_CHECK:
                getInstrumentation().runOnMainSync(new Runnable() {

                    @Override
                    public void run() {
                        mCheckbox.setChecked(isChecked);
                    }
                });
                mSolo.sleep(2000);
                ScreenShotUtil.AssertViewEqualBefore(mSolo, mCheckbox, fileName);
                break;
            case STATE_DISABLE:
                getInstrumentation().runOnMainSync(new Runnable() {
                    @Override
                    public void run() {
                        mCheckbox.setEnabled(false);
                    }
                });
                getInstrumentation().waitForIdleSync();
                mSolo.sleep(2000);
                ScreenShotUtil.AssertViewEqualBefore(mSolo, mCheckbox, fileName);
                break;
        }

    }

    private void testSwitch(int id,
                       final boolean status, int state) {
        assertNotNull(mActivity);
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.setOrientationMark(true);
        mSwitch = (Switch) mActivity.findViewById(id);
        Log.e("mSwitch", "mSwitch" + mSwitch);
        switch (state) {
            case STATE_SWITH:
                getInstrumentation().runOnMainSync(new Runnable() {
                    @Override
                    public void run() {
                        mSwitch.setChecked(status);
                    }
                });
                if (!mSwitch.isChecked() && status) {
                    mSolo.clickOnView(mSwitch);
                }
                mSolo.sleep(2000);
                getInstrumentation().waitForIdleSync();
                ScreenShotUtil.AssertViewEqualBefore(mSolo,
                        mSwitch, this);
                break;
            case STATE_DISABLE:
                getInstrumentation().runOnMainSync(new Runnable() {
                    @Override
                    public void run() {
                        mSwitch.setEnabled(false);
                    }
                });
                mSolo.sleep(2000);
                getInstrumentation().waitForIdleSync();
                ScreenShotUtil.AssertViewEqualBefore(mSolo,
                        mSwitch, this);
                break;
        }
    }

    public void testRadioButton(int id, final boolean isChecked, int state) {
        assertNotNull(mActivity);
        getInstrumentation().waitForIdleSync();
        mRadioButton = (RadioButton) mActivity.findViewById(id);
        Log.e("mSwitch", "mRadioButton" + mRadioButton);
        final String fileName = ScreenShotUtil.getScreenShotName(HtcStyleUtilTest.this);
        switch (state) {
            case STATE_Radio:
                getInstrumentation().runOnMainSync(new Runnable() {

                    @Override
                    public void run() {
                        mRadioButton.setChecked(isChecked);
                    }
                });
                mSolo.sleep(2000);
                ScreenShotUtil.AssertViewEqualBefore(mSolo, mRadioButton, fileName);
                break;
            case STATE_DISABLE:
                getInstrumentation().runOnMainSync(new Runnable() {
                    @Override
                    public void run() {
                        mRadioButton.setEnabled(false);
                    }
                });
                mSolo.sleep(2000);
                getInstrumentation().waitForIdleSync();
                ScreenShotUtil.AssertViewEqualBefore(mSolo,
                        mRadioButton, this);
                break;
        }
    }
}
