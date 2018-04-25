
package com.htc.lib1.cc.actionbar.stress;

import com.htc.actionbar.aut.ActionBarMockActivity;
import com.htc.lib1.cc.actionbar.ActionBarTestUtil;
import com.htc.lib1.cc.actionbar.ActionBarWidgetsFactory;
import com.htc.lib1.cc.actionbar.HtcActionBarActivityTestCase;
import com.htc.test.util.NoRunTheme;

@NoRunTheme
public class TestActionBarContainerStress extends HtcActionBarActivityTestCase {
    private static final int STRESSNUM = 10;

    public TestActionBarContainerStress() {
        super(ActionBarMockActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        initActionBarContainers();
    }

    private void uiTreadFunction(Runnable runnable) {
        try {
            for (int i = 0; i < STRESSNUM; i++) {
                runTestOnUiThread(runnable);
            }
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    }

    public void testActionBarContainer_stressAddActionBtn() {
        uiTreadFunction(new Runnable() {
            @Override
            public void run() {
                ActionBarTestUtil.addLeftViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, true));
            }
        });
    }
}
