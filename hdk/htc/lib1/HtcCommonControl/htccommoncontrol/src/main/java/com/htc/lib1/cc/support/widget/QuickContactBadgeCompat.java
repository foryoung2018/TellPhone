package com.htc.lib1.cc.support.widget;


import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.widget.QuickContactBadge;

import java.lang.reflect.Field;

public class QuickContactBadgeCompat {
    private static final String TAG = "QuickContactBadgeCompat";

    /**
     * Assigns the drawable that is to be drawn on top of the assigned contact photo.
     *
     * @param overlay Drawable to be drawn over the assigned contact photo. Must have a non-zero
     *                instrinsic width and height.
     */
    public static void setOverlay(QuickContactBadge quickContactBadge, Drawable overlay) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            quickContactBadge.setOverlay(overlay);
        } else {
            setOverlayForKK(quickContactBadge, overlay);
        }
    }

    private static Field sOverlayField;
    private static boolean sOverlayFieldFetched;

    private static void setOverlayForKK(QuickContactBadge quickContactBadge, Drawable overlay) {
        if (!sOverlayFieldFetched) {
            try {
                sOverlayField = QuickContactBadge.class.getDeclaredField("mOverlay");
                sOverlayField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                Log.i(TAG, "Failed to retrieve field", e);
            }
            sOverlayFieldFetched = true;
        }

        if (sOverlayField != null) {
            try {
                sOverlayField.set(quickContactBadge, overlay);
            } catch (IllegalAccessException e) {
                Log.i(TAG, "Failed to set ovlay via reflection", e);
                sOverlayField = null;
            }
        }
    }
}
