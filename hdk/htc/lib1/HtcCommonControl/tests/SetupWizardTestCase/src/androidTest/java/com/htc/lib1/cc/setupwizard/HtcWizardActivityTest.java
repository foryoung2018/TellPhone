package com.htc.lib1.cc.setupwizard;

import android.widget.ProgressBar;

import com.htc.lib1.cc.setupwizard.util.SetupWizardActivityTestCaseBase;
import com.htc.lib1.cc.setupwizard.util.SetupWizardTestUtil;
import com.htc.test.util.ScreenShotUtil;

public class HtcWizardActivityTest extends SetupWizardActivityTestCaseBase {

    public HtcWizardActivityTest() throws ClassNotFoundException {
        super(
                Class.forName("com.htc.setupwizard.aut.DemoActivity1"));
    }

    private void assertWizardActivity() {
        ProgressBar pb = (ProgressBar) mSolo.getView("progress_bar");
        ScreenShotUtil.AssertViewEqualBefore(mSolo, pb.getRootView(), this);
    }

    public void testWizardShow() {
        assertWizardActivity();
    }

    public final void testSubTitleTextSize() {
        SetupWizardTestUtil.testSubTitleTextSize(mActivity, mSolo, this, "tip");
    }
}
