package com.htc.setupwizard.aut.util;

import com.htc.aut.util.ActivityUtil;

import android.app.Activity;
import android.content.Intent;

public class SetupWizardUtil {

    public static void initThemeAndCategory(Activity act) {

        String themeName = null;
        int categoryId = 0;

        if (null != act) {
            final Intent i = act.getIntent();
            if (null != i) {
                themeName = i.getStringExtra(ActivityUtil.THEMENAME);
                categoryId = i.getIntExtra(ActivityUtil.CATEGORYID, 0);
            }
        }

        ActivityUtil.initTheme(themeName, act);
        ActivityUtil.initCategory(act, categoryId);
    }
}
