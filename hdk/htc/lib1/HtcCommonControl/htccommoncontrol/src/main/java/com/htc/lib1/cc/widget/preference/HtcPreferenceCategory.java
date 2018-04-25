package com.htc.lib1.cc.widget.preference;

import com.htc.lib1.cc.R;

import android.content.Context;
import android.os.Build;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
/**
 *
 * @deprecated Common Control internal used
 * This can't be used by applications.
 * @hide
 */
@Deprecated
public class HtcPreferenceCategory extends PreferenceCategory {
    private static final boolean ISLOLLIPOP = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    private static int mThemeId = ISLOLLIPOP ? android.R.style.Theme_Material_Light
        : R.style.Preference;
    public HtcPreferenceCategory(Context context, AttributeSet attrs, int defStyle) {
        super(new ContextThemeWrapper(context, mThemeId), attrs, defStyle);
    }

    public HtcPreferenceCategory(Context context, AttributeSet attrs) {
        super(new ContextThemeWrapper(context, mThemeId), attrs);
    }

    public HtcPreferenceCategory(Context context) {
        super(new ContextThemeWrapper(context, mThemeId));
    }
}
