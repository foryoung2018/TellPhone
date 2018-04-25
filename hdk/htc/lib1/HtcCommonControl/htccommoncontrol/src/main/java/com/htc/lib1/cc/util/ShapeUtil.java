package com.htc.lib1.cc.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;

import com.htc.lib1.cc.R;

import java.lang.reflect.Field;

/**
 * A convenient utility class used to handle stroke tasks.
 */
public class ShapeUtil {
    private static final String TAG = ShapeUtil.class.getSimpleName();
    private static Field sGradientStatePadding = null;
    private static Field sGradientDrawablePadding = null;

    private static int getDefaultDiameter(Context context) {
        final Resources res = context.getResources();
        if (null == res) {
            throw (new Resources.NotFoundException());
        }
        return res.getDimensionPixelOffset(R.dimen.htc_list_item_photo_frame_diameter);
    }

    private static int getDefaultStrokeWidth(Context context) {
        final Resources res = context.getResources();
        if (null == res) {
            throw (new Resources.NotFoundException());
        }
        return res.getDimensionPixelOffset(R.dimen.htc_list_item_photo_frame_stroke);
    }

    private static int getDefaultStrokeColor(Context context) {
        final Resources res = context.getResources();
        if (null == res) {
            throw (new Resources.NotFoundException());
        }
        return res.getColor(R.color.light_secondaryfont_color);
    }

    /**
     * Create a circle with a stroke of drawable.
     *
     * @param context Interface to global information about an application environment
     * @param src     the original bitmap
     * @return return a circle drawable with stroke and use min (bitmap.width, bitmap.height) as
     * the diameter of the circle
     */
    public static Drawable createRoundStroke(Context context, Bitmap src) {
        if (null == context) {
            Log.e(TAG, "context = null", new Exception());
            return null;
        }

        if (null == src) {
            Log.e(TAG, "Bitmap = null", new Exception());
            return null;
        }

        return createRoundStroke(Math.min(src.getWidth(), src.getHeight()),
            getDefaultStrokeWidth(context),
            getDefaultStrokeColor(context),
            getDefaultStrokeWidth(context));
    }

    /**
     * Create a circle with a stroke of drawable.
     *
     * @param context  Interface to global information about an application environment
     * @param diameter diameter of the circle
     * @return return a circle drawable with stroke
     */
    public static Drawable createRoundStroke(Context context, int diameter) {
        if (null == context) {
            Log.e(TAG, "context = null", new Exception());
            return null;
        }

        return createRoundStroke(diameter, getDefaultStrokeWidth(context), getDefaultStrokeColor(context),
            getDefaultStrokeWidth(context));
    }

    /**
     * Create a circle with a stroke of drawable,according to default value
     *
     * @param context Interface to global information about an application environment
     * @return return a circle drawable with stroke according to default value
     */
    public static Drawable createRoundStroke(Context context) {
        if (null == context) {
            Log.e(TAG, "context = null", new Exception());
            return null;
        }

        return createRoundStroke(getDefaultDiameter(context), getDefaultStrokeWidth(context),
            getDefaultStrokeColor(context),
            getDefaultStrokeWidth(context));
    }

    /**
     * Create a circle with a stroke of drawable.
     *
     * @param diameter    diameter of the circle
     * @param strokeWidth width of the stroke
     * @param color       color of the stroke
     * @param padding     padding of the circle
     * @return return a circle drawable with stroke according to a given value
     */
    public static Drawable createRoundStroke(int diameter, int strokeWidth, int
        color, int padding) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setStroke(strokeWidth, color);
        drawable.setSize(diameter, diameter);
        setPadding(drawable, padding);

        return drawable;
    }

    private static void setPadding(GradientDrawable drawable, int padding) {
        try {
            if (null == sGradientStatePadding) {
                Class clazz = Class.forName("android.graphics.drawable.GradientDrawable$GradientState");
                sGradientStatePadding = clazz.getDeclaredField("mPadding");
                sGradientStatePadding.setAccessible(true);
            }
            sGradientStatePadding.set(drawable.getConstantState(), new Rect(padding, padding, padding, padding));

            if (null == sGradientDrawablePadding) {
                sGradientDrawablePadding = GradientDrawable.class.getDeclaredField("mPadding");
                sGradientDrawablePadding.setAccessible(true);
            }
            sGradientDrawablePadding.set(drawable, new Rect(padding, padding, padding, padding));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        drawable.invalidateSelf();
    }
}
