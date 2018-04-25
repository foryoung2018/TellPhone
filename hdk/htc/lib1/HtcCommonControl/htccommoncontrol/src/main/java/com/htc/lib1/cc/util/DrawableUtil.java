package com.htc.lib1.cc.util;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;

public class DrawableUtil {
    public static void parseXML2Drawable(Resources r, String tagName, int resID, Drawable d) {
        if ( null == r || null == tagName || 0 >= resID || null == d)
            return ;

        XmlResourceParser parser = r.getXml(resID);

        try {
            int type;
            while ((type = parser.next()) != XmlPullParser.START_TAG
            && type != XmlPullParser.END_DOCUMENT) {
            }
            if ( !tagName.equals(parser.getName()))
                return;

            AttributeSet attrs = Xml.asAttributeSet(parser);
            d.inflate(r, parser, attrs);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Overlay the colorForeground to your drawable, you can use the drawable returned when the color foreground may be a colorStateList.
     *
     * @param context  should be a {@link android.view.ContextThemeWrapper}
     * @param drawable the drawable need to be tinted
     * @return the tinted drawable which you may be a origin Drawable in API21 and a wrap drawable less than API21
     * @hide
     */
    public static Drawable overlayIcon(Context context, Drawable drawable) {
        if (drawable == null || context == null) return drawable;

        TypedValue out = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorForeground, out, true);
        ColorStateList foregroundColor = null;
        if (out.resourceId > 0) {
            foregroundColor = context.getResources().getColorStateList(out.resourceId);
        }

        if (foregroundColor == null) return drawable;

        Drawable d = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(d, foregroundColor);
        return d;
    }
}
