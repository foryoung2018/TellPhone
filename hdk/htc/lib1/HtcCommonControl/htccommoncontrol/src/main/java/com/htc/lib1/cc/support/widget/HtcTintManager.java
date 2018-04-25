package com.htc.lib1.cc.support.widget;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v4.widget.TintableCompoundButton;
import android.util.SparseArray;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.HtcCommonUtil;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * Helper class to tint HtcTheme color for Google Widgets , which contains {@link Switch},{@link CheckBox},{@link RadioButton}.
 * <p/>
 * Demo:
 * <pre class="prettyprint">
 * HtcTintManager htcTintManager = HtcTintManager.get(CheckableWidgetActivity.this);
 * RadioButton radioButton = (RadioButton) findViewById(R.id.radiobutton1);
 * htcTintManager.tintThemeColor(radioButton);
 * </pre>
 */
public class HtcTintManager {

    private final WeakReference<Context> mContextRef;
    private SparseArray<ColorStateList> mTintLists;
    private static final WeakHashMap<Context, HtcTintManager> INSTANCE_CACHE = new WeakHashMap<>();

    private static final int TINT_CHECKABLE_BUTTON_LIST = 0;
    private static final int TINT_SWITCH_TRACK_LIST = 1;
    private static final int TINT_SWITCH_THUMB_LIST = 2;
    private static final int TINT_CHECKABLE_BACKGROUND_LIST = 3;
    private static final int TINT_CHECKABLE_BUTTON_LIST_DARK = 4;
    private static final int TINT_CHECKABLE_BACKGROUND_LIST_DARK = 5;

    static final int[] DISABLED_STATE_SET = new int[]{-android.R.attr.state_enabled};
    static final int[] CHECKED_STATE_SET = new int[]{android.R.attr.state_checked};
    static final int[] EMPTY_STATE_SET = new int[0];

    private static final float SWITCH_TRACK_BY_THUMB_ALPHA = 0.3f;
    private static final float SWITCH_TRACK_DISABLED_ALPHA = 0.1f;
    private static final float DISABLED_ALPHA = 0.5f;
    private static final float DISABLED_ALPHA_DARK = 0.4f;
    private static final float BACKGROUND_RIPPLE_ALPHA = 0.26f;

    private boolean mLight = true;

    public static HtcTintManager get(Context context) {
        HtcTintManager tm = INSTANCE_CACHE.get(context);
        if (tm == null) {
            tm = new HtcTintManager(context);
            INSTANCE_CACHE.put(context, tm);
        }
        return tm;
    }

    private HtcTintManager(Context context) {
        mContextRef = new WeakReference<>(context);
        mLight = HtcCommonUtil.getThemeType(context) == HtcCommonUtil.THEME_LIGHT;
    }

    /**
     * Helper method to tint HtcTheme color for RadioButton.
     */
    public void tintThemeColor(RadioButton gRadioButton) {
        tintThemeColor(gRadioButton, mLight);
    }

    /**
     * Helper method to tint HtcTheme color for RadioButton.
     */
    public void tintThemeColor(RadioButton gRadioButton, boolean light) {
        final Context context = mContextRef.get();
        if (context == null) return;

        tintBackground(gRadioButton, light);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            gRadioButton.setButtonDrawable(R.drawable.abc_btn_radio_material);
        }

        final ColorStateList tintList = getTintList(light ? TINT_CHECKABLE_BUTTON_LIST : TINT_CHECKABLE_BUTTON_LIST_DARK);
        if (tintList != null) {
            tintCompoundButton(gRadioButton, tintList);
        }
    }

    /**
     * Helper method to tint HtcTheme color for CheckBox.
     */
    public void tintThemeColor(CheckBox gCheckBox) {
        tintThemeColor(gCheckBox, mLight);
    }

    /**
     * Helper method to tint HtcTheme color for CheckBox.
     */
    public void tintThemeColor(CheckBox gCheckBox, boolean light) {
        final Context context = mContextRef.get();
        if (context == null) return;

        tintBackground(gCheckBox, light);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            gCheckBox.setButtonDrawable(R.drawable.abc_btn_check_material);
        }

        final ColorStateList tintList = getTintList(light ? TINT_CHECKABLE_BUTTON_LIST : TINT_CHECKABLE_BUTTON_LIST_DARK);
        if (tintList != null) {
            tintCompoundButton(gCheckBox, tintList);
        }
    }

    /**
     * Helper method to tint HtcTheme color for CompoundButton Background（RadioButton and CheckBox）.
     */
    private void tintBackground(CompoundButton compoundButton, boolean light) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;

        final Drawable background = compoundButton.getBackground();

        if (background == null || !(background instanceof RippleDrawable)) return;

        RippleDrawable backgroundRipple = (RippleDrawable) background;

        final ColorStateList rippleColor = getTintList(light ? TINT_CHECKABLE_BACKGROUND_LIST : TINT_CHECKABLE_BACKGROUND_LIST_DARK);
        if (rippleColor != null) {
            backgroundRipple.setColor(rippleColor);
        }
    }

    private void tintCompoundButton(CompoundButton compoundButton, ColorStateList tintList) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && !(compoundButton instanceof TintableCompoundButton)) {
            Drawable d = CompoundButtonCompat.getButtonDrawable(compoundButton);
            d = DrawableCompat.wrap(d.mutate());
            DrawableCompat.setTintList(d, tintList);
            compoundButton.setButtonDrawable(d);
        } else {
            CompoundButtonCompat.setButtonTintList(compoundButton, tintList);
        }
    }

    /**
     * Helper method to tint HtcTheme color for Switch.
     */
    public void tintThemeColor(Switch gSwitch) {
        final Context context = mContextRef.get();
        if (context == null) return;

        tintBackground(gSwitch, mLight);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            gSwitch.setThumbResource(R.drawable.abc_switch_thumb_material);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            gSwitch.setTrackResource(R.drawable.abc_switch_track_mtrl_alpha);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final ColorStateList trackTintList = getTintList(TINT_SWITCH_TRACK_LIST);
            if (trackTintList != null) {
                HtcSwitchCompat.setTrackTintList(gSwitch, trackTintList);
            }
        }

        final ColorStateList thumbTintList = getTintList(TINT_SWITCH_THUMB_LIST);
        if (thumbTintList != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                HtcSwitchCompat.setThumbTintList(gSwitch, thumbTintList, PorterDuff.Mode.SRC_ATOP);
            } else {
                HtcSwitchCompat.setThumbTintList(gSwitch, thumbTintList, null);
            }
        }
    }

    /**
     * This ColorStateList use for Android Widget TintList.
     */
    private ColorStateList getTintList(int category) {
        final Context context = mContextRef.get();
        if (context == null) return null;

        // Try the cache first (if it exists)
        ColorStateList tint = mTintLists != null ? mTintLists.get(category) : null;

        if (tint == null) {
            if (category == TINT_CHECKABLE_BUTTON_LIST) {
                tint = createCheckableButtonColorStateList(context, true);
            } else if (category == TINT_SWITCH_TRACK_LIST) {
                tint = createSwitchTrackColorStateList(context);
            } else if (category == TINT_SWITCH_THUMB_LIST) {
                tint = createSwitchThumbColorStateList(context);
            } else if (category == TINT_CHECKABLE_BACKGROUND_LIST) {
                tint = createCheckableBackgroundStateList(context, true);
            } else if (category == TINT_CHECKABLE_BUTTON_LIST_DARK) {
                tint = createCheckableButtonColorStateList(context, false);
            } else if (category == TINT_CHECKABLE_BACKGROUND_LIST_DARK) {
                tint = createCheckableBackgroundStateList(context, false);
            }

            if (tint != null) {
                if (mTintLists == null) {
                    // If our tint list cache hasn't been set up yet, create it
                    mTintLists = new SparseArray<>();
                }
                // Add any newly created ColorStateList to the cache
                mTintLists.append(category, tint);
            }
        }
        return tint;
    }

    /**
     * This ColorStateList use for CheckBox's state.
     */
    private ColorStateList createCheckableButtonColorStateList(Context context, boolean light) {
        final int[][] states = new int[3][];
        final int[] colors = new int[3];
        int i = 0;


        final int normalColorRes = light ? R.color.abc_secondary_text_material_light : R.color.abc_secondary_text_material_dark;
        final float disabledAlpha = light ? DISABLED_ALPHA : DISABLED_ALPHA_DARK;

        // Disabled state
        states[i] = DISABLED_STATE_SET;
        colors[i] = ThemeUtils.getDisabledColor(context, normalColorRes, disabledAlpha);
        i++;

        states[i] = CHECKED_STATE_SET;
        colors[i] = HtcCommonUtil.getCommonThemeColor(context, R.styleable.ThemeColor_category_color);
        i++;

        // Default enabled state
        states[i] = EMPTY_STATE_SET;
        colors[i] = ContextCompat.getColor(context, normalColorRes);
        i++;

        return new ColorStateList(states, colors);
    }

    /**
     * This ColorStateList use for SwitchTrack's state.
     */
    private ColorStateList createSwitchTrackColorStateList(Context context) {
        final int[][] states = new int[3][];
        final int[] colors = new int[3];
        int i = 0;

        final int checkedThumbColor = HtcCommonUtil.getCommonThemeColor(context, R.styleable.ThemeColor_light_category_color);
        final int normalThumbColor = HtcCommonUtil.getCommonThemeColor(context, R.styleable.ThemeColor_progress_track_center_color);

        // Disabled state
        states[i] = DISABLED_STATE_SET;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            colors[i] = normalThumbColor;
        } else {
            colors[i] = ColorUtils.setAlphaComponent(normalThumbColor, Math.round(Color.alpha(normalThumbColor) * SWITCH_TRACK_DISABLED_ALPHA));
        }
        i++;

        states[i] = CHECKED_STATE_SET;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            colors[i] = checkedThumbColor;
        } else {
            colors[i] = ColorUtils.setAlphaComponent(checkedThumbColor, Math.round(Color.alpha(checkedThumbColor) * SWITCH_TRACK_BY_THUMB_ALPHA));
        }
        i++;

        // Default enabled state
        states[i] = EMPTY_STATE_SET;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            colors[i] = normalThumbColor;
        } else {
            colors[i] = ColorUtils.setAlphaComponent(normalThumbColor, Math.round(Color.alpha(normalThumbColor) * SWITCH_TRACK_BY_THUMB_ALPHA));
        }
        i++;
        return new ColorStateList(states, colors);
    }

    /**
     * This ColorStateList use for SwitchThumb's state.
     */
    private ColorStateList createSwitchThumbColorStateList(Context context) {
        final int[][] states = new int[3][];
        final int[] colors = new int[3];
        int i = 0;

        final int checkedThumbColor = HtcCommonUtil.getCommonThemeColor(context, R.styleable.ThemeColor_light_category_color);
        final int normalThumbColor = HtcCommonUtil.getCommonThemeColor(context, R.styleable.ThemeColor_progress_track_center_color);

        // Disabled state
        states[i] = DISABLED_STATE_SET;
        colors[i] = ColorUtils.setAlphaComponent(normalThumbColor, Math.round(Color.alpha(normalThumbColor) * DISABLED_ALPHA));
        i++;

        states[i] = CHECKED_STATE_SET;
        colors[i] = checkedThumbColor;
        i++;

        // Default enabled state
        states[i] = EMPTY_STATE_SET;
        colors[i] = normalThumbColor;
        i++;
        return new ColorStateList(states, colors);
    }

    private ColorStateList createCheckableBackgroundStateList(Context context, boolean light) {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];
        int i = 0;

        final int checkedColor = HtcCommonUtil.getCommonThemeColor(context, R.styleable.ThemeColor_category_color);
        states[i] = CHECKED_STATE_SET;
        colors[i] = ColorUtils.setAlphaComponent(checkedColor, Math.round(Color.alpha(checkedColor) * BACKGROUND_RIPPLE_ALPHA));
        i++;

        // Default enabled state
        final int highlightColorRes = light ? R.color.ripple_material_light : R.color.ripple_material_dark;
        states[i] = EMPTY_STATE_SET;
        colors[i] = ContextCompat.getColor(context, highlightColorRes);
        i++;

        return new ColorStateList(states, colors);
    }

}
