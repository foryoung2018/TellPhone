
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint.FontMetricsInt;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.util.res.HtcResUtil;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * HtcListItemManager is help to get some value that used by HtcListItem
 *
 * @hide
 */
public final class HtcListItemManager {
    private WeakReference<Context> mContextRef;
    private static final WeakHashMap<Context, HtcListItemManager> INSTANCE_CACHE = new WeakHashMap<>();

    private static final Object sLockObject = new Object();
    private static final float VERTICAL_DIVIDER_RATIO = 0.147f;
    private static final float VERTICAL_DIVIDER_RATIO_AUTOMOTIVE = 0.2f;

    private static final int M1 = 0;
    private static final int M2 = 1;
    private static final int M3 = 2;
    private static final int M4 = 3;
    private static final int M5 = 4;
    private static final int M6 = 5;
    private static int[] Margin = new int[6];

    private static boolean sArrayInit = false;
    private static int sPhoneActionButtonWidth[] = new int[HtcListItem.NUM_ITEMMODE];
    private static int sPortraitWindowWidth = 0;
    private static int mVerticalDividerWidth = 0;
    private static int mLeftIndentSpace = 0;

    private static TextPaint mTextPaint = new TextPaint();

    private int mDesiredItemHeight[] = new int[HtcListItem.NUM_ITEMMODE];
    private int mTextTopGap[] = new int[HtcListItem.NUM_ITEMMODE];
    private int mTextMiddleGap[] = new int[HtcListItem.NUM_ITEMMODE];
    private int mTextBottomGap[] = new int[HtcListItem.NUM_ITEMMODE];
    private int mPrimaryTextBaseline[] = new int[HtcListItem.NUM_ITEMMODE];
    private int mSecondaryTextBaseline[] = new int[HtcListItem.NUM_ITEMMODE];
    private int mPrimaryTextViewHeight[] = new int[HtcListItem.NUM_ITEMMODE];
    private boolean mIsNeedInit[] = new boolean[HtcListItem.NUM_ITEMMODE];

    private static HtcListItemManager sInstance;

    private static int getMargin(int marginLevel, Context c) {
        if (!sArrayInit) {
            initStaticValue(c);
        }
        return Margin[marginLevel];
    }

    public static boolean checkWhiteTextMode(Context context, int textMode) {
        if (textMode != HtcListItem2TextComponent.MODE_UNKNOWN_LIST) {
            return textMode == HtcListItem2TextComponent.MODE_WHITE_LIST;
        }

        return HtcListItemManager.getInstance(context).getCurrentThemeType() == HtcCommonUtil.THEME_LIGHT;
    }

    /**
     *  Get the style set by Mode.
     *
     * @param preferItemMode the mode of ItemMode, from HtcListItem
     * @param textMode the mode of drak or light, from HtcListItem2TextComponent
     * @param preferStyleId if it is not 0, return it's style. the Mode will be invalid
     *
     * @param attrs if define style in xml, xml has higher priority than theme.
     * @return TypedArray
     *
     * */
    static TypedArray obtainStyleIdSet(int preferItemMode, int textMode, int preferStyleId,
                                       Context mContext, AttributeSet attrs) {
        int styleable[] = R.styleable.HtcListItemAppearance;
        TypedArray a;
        if (preferStyleId > 0) {
            a = mContext.obtainStyledAttributes(preferStyleId, styleable);
        } else {
            switch (preferItemMode) {

                case HtcListItem.MODE_KEEP_MEDIUM_HEIGHT:
                    if (checkWhiteTextMode(mContext, textMode))
                        a = mContext.obtainStyledAttributes(R.style.htcListItem_MediumHeight, styleable);
                    else
                        a = mContext.obtainStyledAttributes(R.style.htcListItem_Dark_MediumHeight, styleable);
                    break;
                case HtcListItem.MODE_POPUPMENU:
                    // popup menu has only dark style now
                    a = mContext.obtainStyledAttributes(R.style.htcListItem_Dark_Popupmenu, styleable);
                    break;
                //Both HtcListItem.MODE_DEFAULT & unspecific MODE will follow theme defined,
                //moreover if user set MODE_DARK_LIST or MODE_WHITE_LIST, set by request in priority
                case HtcListItem.MODE_DEFAULT:
                    if (textMode == HtcListItem2TextComponent.MODE_WHITE_LIST){
                        a = mContext.obtainStyledAttributes(R.style.htcListItem, styleable);
                        break;
                    } else if (textMode == HtcListItem2TextComponent.MODE_DARK_LIST) {
                        a = mContext.obtainStyledAttributes(R.style.htcListItem_Dark, styleable);
                        break;
                    }
                case HtcListItem.MODE_AUTOMOTIVE:
                case HtcListItem.MODE_CUSTOMIZED:
                default:    //By theme define
                    a = mContext.obtainStyledAttributes(attrs, styleable, R.attr.htcListItemStyle, R.style.htcListItem);
                    break;
            }
        }
        return a;
    }

    /**
     * Only initializes the static value.
     *
     * @param c
     */
    private static void initStaticValue(Context c) {
        synchronized (sLockObject) {
            Resources res = c.getResources();
            Margin[M1] = res.getDimensionPixelOffset(R.dimen.margin_l);
            Margin[M2] = res.getDimensionPixelOffset(R.dimen.margin_m);
            Margin[M3] = res.getDimensionPixelOffset(R.dimen.margin_s);
            Margin[M4] = res.getDimensionPixelOffset(R.dimen.margin_xs);
            Margin[M5] = res.getDimensionPixelOffset(R.dimen.spacing);
            Margin[M6] = res.getDimensionPixelOffset(R.dimen.leading);
            mVerticalDividerWidth = res
                    .getDimensionPixelOffset(R.dimen.htc_list_item_vertical_divider_width);
            mLeftIndentSpace = res.getDimensionPixelOffset(R.dimen.htc_list_item_left_indent_space);
            sArrayInit = true;
        }
    }

    /**
     * Get the width of vertical divider. The value has been defined in the
     * resource
     *
     * @param c The context the HtcListItem is running in.
     * @return The width of vertical divider
     */
    static int getVerticalDividerWidth(Context c) {
        if (!sArrayInit) {
            initStaticValue(c);
        }
        return mVerticalDividerWidth;
    }

    /**
     * get all children's vertical gap
     *
     * @return the value of gap
     */
    static int getDesiredChildrenGap(Context c) {
        return getM2(c);
    }

    static int getPortraitWindowWidth(Context c) {
        if (sPortraitWindowWidth == 0) {
            DisplayMetrics metrics = new DisplayMetrics();
            metrics = c.getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;

            if (width < height)
                sPortraitWindowWidth = width;
            else
                sPortraitWindowWidth = height;
        }
        return sPortraitWindowWidth;
    }

    /**
     * Get the width of action button, if the mode is automotive mode,the width
     * is 20% * {@link #getPortraitWindowWidth Window width}. if not automotive
     * mode,the width is 14.7% * {@link #getPortraitWindowWidth Window width}.
     *
     * @param c The context the HtcListItem is running in.
     * @param itemMode The mode of HtcListItem
     * @return The width of action button
     */
    static int getActionButtonWidth(Context c, int itemMode) {
        if (!sArrayInit) {
            initStaticValue(c);
        }
        if (sPhoneActionButtonWidth[itemMode] == 0) {
            sPhoneActionButtonWidth[itemMode] = changeOddToEven((int) (getPortraitWindowWidth(c) * (itemMode == HtcListItem.MODE_AUTOMOTIVE ? VERTICAL_DIVIDER_RATIO_AUTOMOTIVE
                    : VERTICAL_DIVIDER_RATIO)));
        }
        return sPhoneActionButtonWidth[itemMode];
    }

    /**
     * check the value is odd number
     *
     * @param value The value need to be checked
     * @return true: The value is odd number. false The value is even number
     */
    static boolean isOdd(int value) {
        return (value & 1) == 1;
    }

    /**
     * check the value,if the value is odd number then change it to a even
     * number by add 1
     *
     * @param value The value need to be checked and change
     * @return A even number
     */
    private static int changeOddToEven(int value) {
        return isOdd(value) ? ++value : value;
    }

    /**
     * if enabled, set alpha to 1.0, else to 0.4
     *
     * @param view
     * @param enabled is view enabled
     */
    static void setViewOpacity(View view, boolean enabled) {
        if (enabled)
            view.setAlpha(1.0f);
        else
            view.setAlpha(0.4f);
    }

    static int getLeftIndentSpace(Context c) {
        if (!sArrayInit) {
            initStaticValue(c);
        }
        return mLeftIndentSpace;
    }

    static int getIndentSpace(Context c)
    {
        return getLeftIndentSpace(c);
    }

    static int getM1(Context c) {
        return getMargin(M1, c);
    }

    static int getM2(Context c) {
        return getMargin(M2, c);
    }

    static int getM3(Context c) {
        return getMargin(M3, c);
    }

    static int getM4(Context c) {
        return getMargin(M4, c);
    }

    static int getM5(Context c) {
        return getMargin(M5, c);
    }

    static int getM6(Context c) {
        return getMargin(M6, c);
    }

    public synchronized static HtcListItemManager getInstance(Context c) {
        if (null != c && c instanceof ContextThemeWrapper && null != sInstance && sInstance.getContextHashCode() == c.hashCode()) {
            return sInstance;
        }

        sInstance = INSTANCE_CACHE.get(c);
        if (sInstance == null) {
            sInstance = new HtcListItemManager(c);
            INSTANCE_CACHE.put(c, sInstance);
        }
        return sInstance;
    }

    private Context getRefContext() {
        return (mContextRef != null && mContextRef.get() != null) ? mContextRef.get() : null;
    }

    private int getContextHashCode() {
        final Context context = getRefContext();
        return context == null ? -1 : context.hashCode();
    }

    /**
     * Constructor that is called when you want to get some value for
     * HtcListItem. just list {@link #getDesiredListItemHeight},
     * {@link #getDesiredTopGap}, {@link #getDesiredBottomGap},
     * {@link #getDesiredTopGap}
     *
     * @param c The context the HtcListItem is running in.
     */
    private HtcListItemManager(Context c) {
        mContextRef = new WeakReference<>(c);
        for (int i = 0; i < mIsNeedInit.length; i++) {
            mIsNeedInit[i] = true;
        }
    }

    private int mThemeType = -1;

    int getCurrentThemeType() {
        final Context context = getRefContext();
        if (mThemeType == -1) mThemeType = HtcCommonUtil.getThemeType(context);

        return mThemeType;
    }

    private void initValueForMode(int itemMode) {
        if (itemMode < 0 || itemMode >= HtcListItem.NUM_ITEMMODE) {
            return;
        }
        final Context context = getRefContext();
        if (context == null) {
            Log.e("HtcListItemManager", "context has been released");
            return;
        }

        Resources res = context.getResources();
        int mSecondaryTextViewHeight[] = new int[HtcListItem.NUM_ITEMMODE];
        TypedArray a = HtcListItemManager.obtainStyleIdSet(itemMode, 0, 0, context, null);
        int primaryTextStyleId = a.getResourceId(R.styleable.HtcListItemAppearance_android_textAppearance,
                R.style.list_primary_m);
        int secondaryTextStyleId = a.getResourceId(R.styleable.HtcListItemAppearance_android_textAppearanceLarge,
                R.style.list_secondary_m);
        a.recycle();
        updateTextHeight(context, primaryTextStyleId,
                mPrimaryTextBaseline, mPrimaryTextViewHeight, itemMode);
        updateTextHeight(context, secondaryTextStyleId,
                mSecondaryTextBaseline, mSecondaryTextViewHeight, itemMode);
        if (itemMode == HtcListItem.MODE_DEFAULT) {

            mTextTopGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_top_margin_percent,
                    mPrimaryTextViewHeight[itemMode], 1) + 0.5f);
            mTextMiddleGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_midlle_margin_percent,
                    mPrimaryTextViewHeight[itemMode], 1) + 0.5f);
            mTextBottomGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_bottom_margin_percent,
                    mPrimaryTextViewHeight[itemMode], 1) + 0.5f);
        } else if (itemMode == HtcListItem.MODE_POPUPMENU) {

            mTextTopGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_popupmenu_top_margin_percent,
                    mPrimaryTextViewHeight[itemMode], 1) + 0.5f);
            mTextMiddleGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_popupmenu_midlle_margin_percent,
                    mPrimaryTextViewHeight[itemMode], 1) + 0.5f);
            mTextBottomGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_popupmenu_bottom_margin_percent,
                    mPrimaryTextViewHeight[itemMode], 1) + 0.5f);
        } else if (itemMode == HtcListItem.MODE_AUTOMOTIVE) {

            mTextTopGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_automotive_top_margin_percent,
                    mPrimaryTextViewHeight[itemMode], 1) + 0.5f);
            mTextMiddleGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_automotive_midlle_margin_percent,
                    mPrimaryTextViewHeight[itemMode], 1) + 0.5f);
            mTextBottomGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_automotive_bottom_margin_percent,
                    mPrimaryTextViewHeight[itemMode], 1) + 0.5f);
        } else if (itemMode == HtcListItem.MODE_KEEP_MEDIUM_HEIGHT) {

            mTextTopGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_top_margin_percent,
                    mPrimaryTextViewHeight[itemMode], 1) + 0.5f);
            mTextMiddleGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_midlle_margin_percent,
                    mPrimaryTextViewHeight[itemMode], 1) + 0.5f);
            mTextBottomGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_bottom_margin_percent,
                    mPrimaryTextViewHeight[itemMode], 1) + 0.5f);
        }

        mDesiredItemHeight[itemMode] = mTextTopGap[itemMode] + mTextMiddleGap[itemMode] + mTextBottomGap[itemMode]
                + mPrimaryTextViewHeight[itemMode] + mSecondaryTextViewHeight[itemMode];
        if (isOdd(mDesiredItemHeight[itemMode])) {
            mDesiredItemHeight[itemMode]++;
            mTextBottomGap[itemMode]++;
        }
        mIsNeedInit[itemMode] = false;
    }

    /**
     * Get the desired height of HtcListItem.
     *
     * @param itemMode The mode of HtcListItem
     * @return The HtcListItem height
     * @hide
     */
    public int getDesiredListItemHeight(int itemMode) {
        if (mIsNeedInit[itemMode]) {
            initValueForMode(itemMode);
        }
        return mDesiredItemHeight[itemMode];
    }

    /**
     * Get the top gap of HtcListItem, The value is 30% of primary text height.
     *
     * @param itemMode The mode of HtcListItem.
     * @return The top gap value for HtcListItem.
     * @hide
     */
    public int getDesiredTopGap(int itemMode) {
        if (mIsNeedInit[itemMode]) {
            initValueForMode(itemMode);
        }
        return mTextTopGap[itemMode];
    }

    /**
     * Get the middle gap of HtcListItem, The value is 12% of primary text height.
     *
     * @param itemMode The mode of HtcListItem.
     * @return The middle gap value for HtcListItem.
     * @hide
     */
    public int getDesiredMiddleGap(int itemMode) {
        if (mIsNeedInit[itemMode]) {
            initValueForMode(itemMode);
        }
        return mTextMiddleGap[itemMode];
    }
    /**
     * Get the center of primary text, the value is {@link #getDesiredTopGap
     * TopGap} + {@link #getPrimaryTextBaseLine PrimaryTextBaseLine} * 1/2
     *
     * @param itemMode The mode of HtcListItem.
     * @return The center of primary text for HtcListItem7Badges1LineBottomStamp
     * @hide
     */
    public int getDesiredCenterFor7Badge(int itemMode) {
        if (mIsNeedInit[itemMode]) {
            initValueForMode(itemMode);
        }
        return (int) (getDesiredTopGap(itemMode) + 0.5f * mPrimaryTextBaseline[itemMode]);
    }

    /**
     * Get the bottom gap of HtcListItem, The value is 48% of secondary text
     * height.
     *
     * @param itemMode The mode of HtcListItem.
     * @return The value of HtcListItem bottom gap.
     * @hide
     */
    public int getDesiredBottomGap(int itemMode) {
        if (mIsNeedInit[itemMode]) {
            initValueForMode(itemMode);
        }
        return mTextBottomGap[itemMode];
    }

    /**
     * Get the offset of primary text base line from the HtcListItem's top,
     * include the {@link #getDesiredTopGap TopGap} and
     * {@link #getPrimaryTextBaseLine PrimaryTextBaseLine}.
     *
     * @param itemMode The mode of HtcListItem.
     * @return The offset of primary text base line from the HtcListItem's top.
     * @hide
     */
    public int getPrimaryBaseLine(int itemMode) {
        if (mIsNeedInit[itemMode]) {
            initValueForMode(itemMode);
        }
        return getDesiredTopGap(itemMode) + mPrimaryTextBaseline[itemMode];
    }

    /**
     * Get the offset of secondary text base line from the HtcListItem's top,
     * include the {@link #getDesiredTopGap Top Gap},
     * SecondaryTextBaseLine and the height of
     * primary text .
     *
     * @param itemMode The mode of HtcListItem.
     * @return The offset of secondary text base line from the HtcListItem's
     *         top.
     * @hide
     */
    public int getSecondaryBaseLine(int itemMode) {
        if (mIsNeedInit[itemMode]) {
            initValueForMode(itemMode);
        }
        return getDesiredTopGap(itemMode) + mPrimaryTextViewHeight[itemMode]
               + getDesiredMiddleGap(itemMode) + mSecondaryTextBaseline[itemMode];
    }

    /**
     * Get the offset of primary text base line from the primarytext's top.
     *
     * @param itemMode The mode of HtcListItem.
     * @return The offset of primary text base line from primarythe text's top.
     * @hide
     */
    public int getPrimaryTextBaseLine(int itemMode) {
        if (mIsNeedInit[itemMode]) {
            initValueForMode(itemMode);
        }
        return mPrimaryTextBaseline[itemMode];
    }

    /**
     * Get the width of photo frame, the value is
     * {@link #getDesiredListItemHeight HtcListItem's height} + {@link #getM2
     * M2}.
     *
     * @param c The context the HtcListItem is running in.
     * @param itemMode The mode of HtcListItem.
     * @return The width of photo frame
     * @hide
     */
    public int getPhotoFrameWidth(Context c, int itemMode) {
        if (mIsNeedInit[itemMode]) {
            initValueForMode(itemMode);
        }
        return getDesiredListItemHeight(itemMode) + getM2(c);
    }

    /**
     * This API is used to update text height and text baseline.
     *
     * @param c
     * @param style
     * @param includeBelow if true, return all height, include the text height
     *            below and above baseline. if false, only the text height above
     *            baseline.
     * @param baseline
     * @param textheight
     * @param itemMode
     */
    private void updateTextHeight(Context c, int style, int[] baseline, int[] textheight,
            int itemMode) {
        if (mTextPaint != null) {
            HtcResUtil.setTextAppearance(c, style, mTextPaint, false);
            FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
            textheight[itemMode] = fontMetrics.bottom - fontMetrics.top;
            baseline[itemMode] = -fontMetrics.top;
        } else {
            textheight[itemMode] = 0;
            baseline[itemMode] = 0;
        }
    }

    private static int[] sItemMode = new int[2];

    public static int[] resolveListItemMode(Context context, AttributeSet set) {
        TypedArray a = context.obtainStyledAttributes(set, R.styleable.HtcListItemMode, R.attr.htcListItemStyle, R.style.htcListItem);
        sItemMode[0] = a.getInt(R.styleable.HtcListItemMode_itemMode, HtcListItem.MODE_DEFAULT);
        sItemMode[1] = a.getInt(R.styleable.HtcListItemMode_textMode, HtcListItem2TextComponent.MODE_UNKNOWN_LIST);
        a.recycle();
        return sItemMode;
    }

}
