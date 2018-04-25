package com.htc.lib1.cc.util;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;

import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;

public class BitmapUtil {
    private static final String TAG = BitmapUtil.class.getName();

    /**
     * Make the src Bitmap to be square when it was rectangle
     *
     * @param src    the original bitmap
     * @param length the final length of the square bitmap
     * @return the origin bitmap if the length > origin bitmap width and length > origin bitmap height,
     *         the origin bitmap if it is already square and length==origin bitmap width,
     *         others will return a new square bitmap with the length side.
     */
    public static Bitmap cutToCenter(Bitmap src, int length) throws Exception {
        if (src == null || length <= 0) {
            if (HtcBuildFlag.Htc_DEBUG_flag) {
                Log.d(TAG, "src is null or length <=0");
            }
            return src;
        }

        if (length > Math.min(src.getWidth(), src.getHeight())) {
            if (HtcBuildFlag.Htc_DEBUG_flag) {
                Log.d(TAG, "the final length is larger than the original bitmap width or height");
                throw new Exception("the final length is larger than the original bitmap width or height");
            }
            return src;
        }

        int x = 0, y = 0;
        if (src.getWidth() - length > 0) {
            x = (src.getWidth() - length) / 2;
        }

        if (src.getHeight() - length > 0) {
            y = (src.getHeight() - length) / 2;
        }

        return Bitmap.createBitmap(src, x, y, length, length);
    }

    /**
     * Make the src Bitmap to be square when it was rectangle
     *
     * @param src the original bitmap
     * @return return a square bitmap with the default length = min(origin bitmap width, origin bitmap height)
     * @throws Exception
     */
    public static Bitmap cutToCenter(Bitmap src) throws Exception {
        return cutToCenter(src, Math.min(src.getWidth(), src.getHeight()));
    }

    /**
     * Make the src Bitmap to be square when it was rectangle, then display in circular with the diameter = the length of square Bitmap
     *
     * @param context  Interface to global information about an application environment
     * @param src      the original bitmap
     * @param diameter diameter of the circle
     * @return circular with the diameter = the length of square Bitmap
     * @throws Exception
     */
    public static RoundedBitmapDrawable getRoundedBitmapDrawable(Context context, Bitmap src, int diameter) throws Exception {
        return getRoundedBitmapDrawable(context, src, diameter, false);

    }

    /**
     * Make the src Bitmap to be square when it was rectangle, then display in circular with the diameter = the length of square Bitmap
     *
     * @param context       Interface to global information about an application environment
     * @param src           the original bitmap
     * @param diameter      diameter of the circle
     * @param isNotApCalled false if the AP called, true if not AP called
     * @return return a circular with the diameter = the length of square Bitmap
     * @throws Exception
     */
    private static RoundedBitmapDrawable getRoundedBitmapDrawable(Context context, Bitmap src, int diameter, boolean isNotApCalled) throws Exception {
        Bitmap dst;
        if (diameter == -1){
            dst = BitmapUtil.cutToCenter(src);
        }else{
            dst = BitmapUtil.cutToCenter(src, diameter);
        }
        if (!src.isRecycled() && dst != src && isNotApCalled) src.recycle();

        Resources res = context.getResources();
        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, dst);
        dr.setCircular(true);
        return dr;
    }

    /**
     * Make the src Bitmap to be square when it was rectangle, then display in circular with the diameter = the length of square Bitmap
     *
     * @param context Interface to global information about an application environment
     * @param src     the original bitmap
     * @return return a circular with the default diameter = min(origin bitmap width, origin bitmap height)
     * @throws Exception
     */
    public static RoundedBitmapDrawable getRoundedBitmapDrawable(Context context, Bitmap src) throws Exception {
        return getRoundedBitmapDrawable(context, src, -1, false);
    }

    /**
     * Make the src Bitmap to be square when it was rectangle, then display in circular with the diameter = the length of square Bitmap
     *
     * @param context  Interface to global information about an application environment
     * @param resId    the id of origin bitmap resource
     * @param diameter diameter of the circle
     * @return return a circular with the diameter = the length of square Bitmap
     * @throws Exception
     */
    public static RoundedBitmapDrawable getRoundedBitmapDrawable(Context context, int resId, int diameter) throws Exception {
        Bitmap src = BitmapFactory.decodeResource(context.getResources(), resId);
        if (src == null)
            throw new Exception("image can not be decoded");
        return getRoundedBitmapDrawable(context, src, diameter, true);
    }

    /**
     * Make the src Bitmap to be square when it was rectangle, then display in circular with the diameter = the length of square Bitmap
     *
     * @param context Interface to global information about an application environment
     * @param resId   the id of origin bitmap resource
     * @return return a circular with the default diameter = min(origin bitmap width, origin bitmap height)
     * @throws Exception
     */
    public static RoundedBitmapDrawable getRoundedBitmapDrawable(Context context, int resId) throws Exception {
        return getRoundedBitmapDrawable(context, resId, -1);
    }
}
