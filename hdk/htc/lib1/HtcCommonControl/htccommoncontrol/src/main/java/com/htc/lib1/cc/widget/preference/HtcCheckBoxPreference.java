package com.htc.lib1.cc.widget.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;

import com.htc.lib1.cc.R;
/**
 *
 * @deprecated Common Control internal used
 * This can't be used by applications.
 * @hide
 */
@Deprecated
public class HtcCheckBoxPreference extends android.preference.CheckBoxPreference {

    public HtcCheckBoxPreference(Context context, AttributeSet attrs, int defStyle) {
        super(new ContextThemeWrapper(context, R.style.Preference), attrs, defStyle);
        init();
    }

    public HtcCheckBoxPreference(Context context, AttributeSet attrs) {
        super(new ContextThemeWrapper(context, R.style.Preference), attrs);
        init();
    }

    public HtcCheckBoxPreference(Context context) {
        super(new ContextThemeWrapper(context, R.style.Preference));
        init();
    }

    private void init() {
        PreferenceCompat.setRecycleLayout(this, true);
    }
}
