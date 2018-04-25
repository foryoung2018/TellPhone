package com.htc.lib1.cc.widget.preference;

import com.htc.lib1.cc.R;

import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
/**
 *
 * @deprecated Common Control internal used
 * This can't be used by applications.
 * @hide
 */
@Deprecated
public class HtcSwitchPreference extends SwitchPreference {
    public HtcSwitchPreference(Context context, AttributeSet attrs, int defStyle) {
        super(new ContextThemeWrapper(context, R.style.Preference), attrs, defStyle);
        init();
    }

    public HtcSwitchPreference(Context context, AttributeSet attrs) {
        super(new ContextThemeWrapper(context, R.style.Preference), attrs);
        init();
    }

    public HtcSwitchPreference(Context context) {
        super(new ContextThemeWrapper(context, R.style.Preference));
        init();
    }

    private void init() {
        PreferenceCompat.setRecycleLayout(this, true);
    }
}
