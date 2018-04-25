package com.htc.lib1.cc.support.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.Switch;

class HtcSwitchCompat {

    static void setTrackTintList(Switch gSwitch, ColorStateList tint) {
        if (gSwitch == null) return;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Drawable trackDrawable = gSwitch.getTrackDrawable();
            if (trackDrawable == null) return;

            trackDrawable = tintDrawable(trackDrawable, tint, null);
            gSwitch.setTrackDrawable(trackDrawable);
        } else {
            gSwitch.setTrackTintList(tint);
        }
    }

    static void setThumbTintList(Switch gSwitch, ColorStateList tint, PorterDuff.Mode tintMode) {
        if (gSwitch == null) return;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Drawable thumbDrawable = gSwitch.getThumbDrawable();
            if (thumbDrawable == null) return;

            thumbDrawable = tintDrawable(thumbDrawable, tint, tintMode);
            gSwitch.setThumbDrawable(thumbDrawable);
        } else {
            if (tintMode != null) gSwitch.setThumbTintMode(tintMode);
            gSwitch.setThumbTintList(tint);
        }
    }

    static Drawable tintDrawable(Drawable drawable, ColorStateList tint, PorterDuff.Mode tintMode) {
        if (drawable == null) return null;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable.mutate());
            if (tintMode != null) DrawableCompat.setTintMode(drawable, tintMode);
            DrawableCompat.setTintList(drawable, tint);
        } else {
            if (tintMode != null) drawable.setTintMode(tintMode);
            drawable.setTintList(tint);
        }
        return drawable;
    }
}