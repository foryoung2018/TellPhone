package com.htc.lib1.cc.widget.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Switch;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.support.widget.HtcTintManager;
import com.htc.lib1.cc.util.CheckUtil;

public class PreferenceSwitch extends Switch {

    private void init() {
        View checkableView = findViewById(R.id.switchWidget);
        int ResId = getResources().getIdentifier("switchWidget", "id",
                "android");
        if (0 != ResId) {
            checkableView.setId(ResId);
        }

        HtcTintManager.get(getContext()).tintThemeColor(this);
    }

    /**
     * Create a new PreferenceSwitch.
     * @param context the application environment
     */
    /** @hide */
    public PreferenceSwitch(Context context) {
        super(context);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }

    /**
     * Create a new PreferenceSwitch.
     * @param context the application environment
     * @param attrs attributeSet
     */
    /** @hide */
    public PreferenceSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }

}
