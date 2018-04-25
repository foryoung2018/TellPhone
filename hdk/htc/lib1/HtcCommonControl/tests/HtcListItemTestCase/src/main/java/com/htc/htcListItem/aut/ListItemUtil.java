package com.htc.htcListItem.aut;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import com.htc.htclistitem.aut.R;
import com.htc.lib1.cc.util.BitmapUtil;

public class ListItemUtil {
    public static Drawable wrapBorderDrawable(int resId, Context context) {
        Resources res = context.getResources();
        Bitmap src = BitmapFactory.decodeResource(res, resId);
        try {
            src = BitmapUtil.cutToCenter(src, Math.min(src.getWidth(), src.getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, src);
        dr.setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 2.0f);

        Drawable[] drawables = new Drawable[2];
        drawables[0] = dr;
        drawables[1] = res.getDrawable(R.drawable.round);
        LayerDrawable layer = new LayerDrawable(drawables);
        return layer;
    }
}
