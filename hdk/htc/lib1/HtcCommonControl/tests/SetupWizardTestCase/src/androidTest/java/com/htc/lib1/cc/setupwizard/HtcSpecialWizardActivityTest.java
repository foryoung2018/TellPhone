package com.htc.lib1.cc.setupwizard;

import android.view.View;

import com.htc.lib1.cc.setupwizard.util.SetupWizardActivityTestCaseBase;
import com.htc.lib1.cc.setupwizard.util.SetupWizardTestUtil;
import com.htc.test.util.ScreenShotUtil;

public class HtcSpecialWizardActivityTest extends SetupWizardActivityTestCaseBase {

    public HtcSpecialWizardActivityTest() throws ClassNotFoundException {
        super(Class.forName("com.htc.setupwizard.aut.DemoSpecialActivity"));
    }

    private void assertWizardActivity() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                SetupWizardTestUtil.hideScrollView(mSolo);
            }
        });
        View view =mActivity.findViewById(android.R.id.content);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, view, this);
    }

    public void testSpecialWizardShow() {
        assertWizardActivity();
    }

    public final void testDescriptTextSize() {
        SetupWizardTestUtil
                .testDescriptTextSize(mActivity, mSolo, this, "desc");
    }

    public final void testDescriptMarginMeasure() {
        SetupWizardTestUtil.testDescriptMarginMeasure(mActivity, mSolo, this,
                "desc");
    }

}
