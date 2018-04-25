
package com.htc.lib1.cc.reminderview;

import com.htc.reminderview.aut.TestActivityVideoCall;
import com.htc.test.HtcActivityTestCaseBase;

public class ReminderViewTestCase extends HtcActivityTestCaseBase {

    public ReminderViewTestCase() {
        super(TestActivityVideoCall.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initActivity();

    }

    public void testReminderView() {

        assertEquals(true, true);
    }
}
