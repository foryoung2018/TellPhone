
package com.htc.lib1.cc.widget;

import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.graphics.drawable.Drawable;
import android.content.res.Configuration;

import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;
import com.htc.lib1.cc.util.ActionBarUtil;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.res.HtcResUtil;

import com.htc.lib1.cc.R;

/**
 * A widget can be used in Htc action bar.
 */
public class ActionBarItemView extends LinearLayout implements View.OnLongClickListener {
    /**
     * EXTERNAL mode.
     */
    public static final int MODE_EXTERNAL = 1;

    /**
     * AUTOMOTIVE mode.
     * @deprecated Deprecated in Sense80,please do not use it!
     */
    public static final int MODE_AUTOMOTIVE = 2;

    /**
     * THEME mode.
     *
     * @hide
     */
    public static final int MODE_THEME = 0x10;

    /**
     * Bits of support mode, link with Sense7 UIGL1.0 Page107. Use with {@link #setSupportMode(int)}
     * .
     * <p>
     * Sample:
     *
     * <pre class="prettyprint">
     * ActionBarItemView.setSupportMode(ActionBarItemView.MODE_EXTERNAL | ActionBarItemView.FLAG_M2_IMG_M2);
     * </pre>
     *
     * </p>
     */
    public static final int FLAG_M2_IMG_M2 = 0x80000000;

    private ImageButton mImageButton = null;

    CharSequence mTitle;
    private OnLongClickListener mOnLongClickListener;

    @ExportedProperty(category = "CommonControl")
    private int mItemWidth = 0;
    @ExportedProperty(category = "CommonControl")
    private int mItemHeight = 0;
    @ExportedProperty(category = "CommonControl")
    private int mMeasureSpecM2;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public ActionBarItemView(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called when a view is
     * being constructed from an XML file, supplying attributes that were specified in the XML file.
     * This version uses a default style of 0, so the only attribute values applied are those in the
     * Context's Theme and the given AttributeSet.
     * <p>
     * The method onFinishInflate() will be called after all children have been added.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     * @hide
     */
    public ActionBarItemView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.htcActionButtonStyle);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a theme attribute. This
     * constructor of View allows subclasses to use their own base style when they are inflating.
     * For example, a Button class's constructor would call this version of the super class
     * constructor and supply <code>R.attr.buttonStyle</code> for <var>defStyleAttr</var>; this
     * allows the theme's button style to modify all of the base view attributes (in particular its
     * background) as well as the Button class's attributes.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style
     *            resource that supplies default values for the view. Can be 0 to not look for
     *            defaults.
     * @see #View(Context, AttributeSet)
     * @hide
     */
    public ActionBarItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        mMeasureSpecM2 = HtcResUtil.getM2(context);

        super.setOnLongClickListener(this);

        setupLayoutParameters();

        // inflate external layout and merge to current layout
        LayoutInflater.from(context).inflate(R.layout.action_itemview, this, true);

        mImageButton = (ImageButton) findViewById(R.id.imageButton);
        // check the layout resource correctness
        if (mImageButton == null) throw new RuntimeException("inflate layout resource incorrect");

        mImageButton.setFocusable(false);

        setBackground(ActionBarUtil.getActionMenuItemBackground(context));

    }

    /**
     * Get action bar item icon.
     *
     * @return the icon drawable of this item
     * @deprecated [Not use any longer]
     * @hide
     */
    @Deprecated
    public Drawable getIcon() {
        return mImageButton == null ? null : mImageButton.getDrawable();
    }

    /**
     * Set action bar item icon.
     *
     * @param drawable the drawable you want to show
     */
    public void setIcon(Drawable drawable) {
        if (mImageButton != null) mImageButton.setImageDrawable(drawable);
    }

    /**
     * Set action bar item icon.
     *
     * @param bitmap the bitmap you want to show
     */
    public void setIcon(Bitmap bitmap) {
        if (mImageButton != null) mImageButton.setImageBitmap(bitmap);
    }

    /**
     * Set action bar item icon.
     *
     * @param resid the drawable resource id you want to show
     */
    public void setIcon(int resid) {
        if (mImageButton != null) mImageButton.setImageResource(resid);
    }

    private static final float DISABLE_ALPHA = 0.4f;
    private static final float ENABLE_ALPHA = 1.0f;

    @Override
    public void setEnabled(boolean enable) {
        super.setEnabled(enable);
        if (mImageButton != null) {
            mImageButton.setEnabled(enable);
            mImageButton.setAlpha(enable ? ENABLE_ALPHA : DISABLE_ALPHA);
        }
    }

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = MODE_EXTERNAL, to = "MODE_EXTERNAL"),
            @IntToString(from = MODE_AUTOMOTIVE, to = "MODE_AUTOMOTIVE"),
            @IntToString(from = MODE_THEME, to = "MODE_THEME"),
            @IntToString(from = MODE_EXTERNAL | FLAG_M2_IMG_M2, to = "MODE_EXTERNAL | FLAG_M2_IMG_M2"),
            @IntToString(from = MODE_AUTOMOTIVE | FLAG_M2_IMG_M2, to = "MODE_AUTOMOTIVE|FLAG_M2_IMG_M2"),
            @IntToString(from = MODE_THEME | FLAG_M2_IMG_M2, to = "MODE_THEME|FLAG_M2_IMG_M2")
    })
    private int mSupportMode = MODE_THEME;

    /**
     * support special usage for automotive mode only.
     *
     * @param mode support mode
     */
    public void setSupportMode(int mode) {
        // skip to avoid useless operation
        if (mSupportMode == mode || !isValidMode(mode)) return;

        mSupportMode = mode;

        setupAutomotiveMode();

        if (getInternalFlag() == FLAG_M2_IMG_M2) {
            setPadding(mMeasureSpecM2, 0, mMeasureSpecM2, 0);
        } else {
            setPadding(0, 0, 0, 0);
        }
    }

    // special support for automotive usage
    private void setupAutomotiveMode() {
        setupLayoutParameters();
        requestLayout();
    }

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = Configuration.ORIENTATION_UNDEFINED, to = "ORIENTATION_UNDEFINED"),
            @IntToString(from = Configuration.ORIENTATION_PORTRAIT, to = "ORIENTATION_PORTRAIT"),
            @IntToString(from = Configuration.ORIENTATION_LANDSCAPE, to = "ORIENTATION_LANDSCAPE")
    })
    private int mOrientation = Configuration.ORIENTATION_UNDEFINED;

    /**
     * [Module internal use].
     *
     * @deprecated
     * @hide
     */
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mOrientation != newConfig.orientation) {
            mOrientation = newConfig.orientation;
            setupLayoutParameters();
            requestLayout();
        }
    }

    /**
     * @hide
     */
    @Override
    public boolean onLongClick(View v) {
        if (null != mTitle) {
            final int[] screenPos = new int[2];
            final Rect displayFrame = new Rect();
            getLocationOnScreen(screenPos);
            getWindowVisibleDisplayFrame(displayFrame);

            final Context context = getContext();
            final int width = getWidth();
            final int height = getHeight();
            final int midy = screenPos[1] + height / 2;
            final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;

            Toast cheatSheet = Toast.makeText(context, mTitle, Toast.LENGTH_SHORT);
            if (midy < displayFrame.height()) {
                // Show along the top; follow action buttons
                cheatSheet.setGravity(Gravity.TOP | Gravity.END,
                        screenWidth - screenPos[0] - width / 2, height);
            } else {
                // Show along the bottom center
                cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
            }
            cheatSheet.show();

        }

        if (null != mOnLongClickListener) mOnLongClickListener.onLongClick(v);

        return true;
    }

    /**
     * set title for LongClick Toast.
     */
    public void setTitle(CharSequence title) {
        mTitle = title;
    }

    /**
     * @hide
     */
    @Override
    public void setOnLongClickListener(OnLongClickListener listener) {
        mOnLongClickListener = listener;
    }

    /**
     * (non-Javadoc).
     *
     * @see android.widget.LinearLayout#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getInternalFlag() == FLAG_M2_IMG_M2) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(mItemHeight, MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(MeasureSpec.makeMeasureSpec(mItemWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mItemHeight, MeasureSpec.EXACTLY));
        }
    }

    private void setupLayoutParameters() {
        mItemWidth = ActionBarUtil.getItemWidth(getContext(), getInternalMode());
        mItemHeight = ActionBarUtil.getActionBarHeight(getContext(), (getInternalMode() == MODE_AUTOMOTIVE));
    }

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = MODE_EXTERNAL, to = "MODE_EXTERNAL"),
            @IntToString(from = MODE_AUTOMOTIVE, to = "MODE_AUTOMOTIVE"),
            @IntToString(from = MODE_THEME, to = "MODE_THEME")
    })
    private int getInternalMode() {
        return mSupportMode & ~FLAG_M2_IMG_M2;
    }

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = FLAG_M2_IMG_M2, to = "FLAG_M2_IMG_M2")
    })
    private int getInternalFlag() {
        return mSupportMode & FLAG_M2_IMG_M2;
    }

    private boolean isValidMode(int mode) {
        mode = (mode & ~FLAG_M2_IMG_M2);
        if (mode == MODE_THEME || mode == MODE_EXTERNAL) {
            return true;
        } else {
            if (HtcBuildFlag.Htc_DEBUG_flag) Log.d("ActionBarItemView", "Invalid mode:" + mode);
            return false;
        }
    }

    /**
     * @param should be one of {@link ActionBarItemView#MODE_THEME},
     *            {@link ActionBarItemView#MODE_EXTERNAL},{@link ActionBarItemView#MODE_AUTOMOTIVE}.
     * @hide
     */
    public static int getDefStyleRes(int mode) {
        switch (mode) {
            case MODE_EXTERNAL:
                return R.style.HtcActionButton;
            default:
                return R.style.HtcActionButton;
        }
    }
}
