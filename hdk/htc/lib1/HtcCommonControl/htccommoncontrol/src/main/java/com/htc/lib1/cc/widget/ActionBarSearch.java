
package com.htc.lib1.cc.widget;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.inputmethod.EditorInfo;
import android.content.Context;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.content.res.TypedArray;
import android.widget.RelativeLayout;
import android.widget.AutoCompleteTextView;
import android.graphics.drawable.Drawable;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;
import com.htc.lib1.cc.util.ActionBarUtil;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.res.HtcResUtil;

/**
 * A widget can be used in Htc action bar.
 */
public class ActionBarSearch extends RelativeLayout implements TextWatcher {

    /**
     * EXTERNAL mode.
     */
    public static final int MODE_EXTERNAL = 1;
    /**
     * AUTOMOTIVE mode.
     * @deprecated Deprecated in Sense80,please do not use it!
     */
    public static final int MODE_AUTOMOTIVE = 2;

    private Drawable mIconDrawable = null;
    @ExportedProperty(category = "CommonControl")
    private int mMeasureSpecM2, mMeasureSpecM1;

    // htc style editable text view with animation inside
    private HtcAutoCompleteTextView mHtcAutoCompleteTextView = null;
    private boolean mIsTextChangedListenerExist;

    private int mDrawablePadding = 0;
    private int mContentInsetTop = 0;
    private int mTextAppearance;
    private int mIconPaddingLeft = 0;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public ActionBarSearch(Context context) {
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
    public ActionBarSearch(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.actionBarSearchStyle);
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
    public ActionBarSearch(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, HtcButtonUtil.BACKGROUND_MODE_THEME);
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
    public ActionBarSearch(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr);
        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        if (isValidMode(mode)) {
            mSupportMode = mode;
        }

        initCommonOffset();

        init(attrs, defStyleAttr);

        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setupHtcAutoCompleteTextView();
        setAutoShowClearIcon(true);
        updateSearchPadding();
    }

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param mode contruct with specific mode
     */
    public ActionBarSearch(Context context, int mode) {
        this(context, null, R.attr.actionBarSearchStyle, mode);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        final int defStyleAttrId = mSupportMode == -1 ? defStyleAttr : 0;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HtcActionBarSearch, defStyleAttrId, getDefStyleRes());
        mDrawablePadding = a.getDimensionPixelOffset(R.styleable.HtcActionBarSearch_android_drawablePadding, mMeasureSpecM2);
        mIconDrawable = a.getDrawable(R.styleable.HtcActionBarSearch_android_src);
        mContentInsetTop = a.getDimensionPixelOffset(R.styleable.HtcActionBarSearch_android_insetTop, 0);
        mTextAppearance = a.getResourceId(R.styleable.HtcActionBarSearch_android_textAppearance, R.style.b_button_primary_l);
        mIconPaddingLeft = a.getDimensionPixelOffset(R.styleable.HtcActionBarSearch_android_insetLeft, mMeasureSpecM2);
        a.recycle();

        if (mIconDrawable == null) {
            android.util.Log.e("ActionBarSearch", "mIconDrawable is null!");
            mIconDrawable = getResources().getDrawable(R.drawable.icon_btn_cancel_dark_s);
        }
    }

    private int getDefStyleRes() {
        switch (mSupportMode) {
            case MODE_EXTERNAL:
                return R.style.ActionBarSearch;
            default:
                return R.style.ActionBarSearch;
        }
    }

    /**
     * Every time the AP need to manually judge and set up ActionBarSearch "X", very troublesome.
     * This method can simplify the process, and through set true or false can control without "X".
     *
     * @param autoShowClearIcon The autoShowClearIcon is optional, with true and false.
     */
    public void setAutoShowClearIcon(boolean autoShowClearIcon) {

        if (autoShowClearIcon && !mIsTextChangedListenerExist) {
            mHtcAutoCompleteTextView.addTextChangedListener(this);
            mIsTextChangedListenerExist = true;
            setClearIconVisibility((TextUtils.isEmpty(mHtcAutoCompleteTextView.getText())) ? View.GONE
                    : View.VISIBLE);
        } else if (!autoShowClearIcon && mIsTextChangedListenerExist) {
            mHtcAutoCompleteTextView.removeTextChangedListener(this);
            mIsTextChangedListenerExist = false;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setClearIconVisibility((TextUtils.isEmpty(s)) ? View.GONE : View.VISIBLE);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private void initCommonOffset() {
        // setup the initial common environment
        mMeasureSpecM2 = HtcResUtil.getM2(getContext());
        mMeasureSpecM1 = HtcResUtil.getM1(getContext());
    }

    private void setupHtcAutoCompleteTextView() {
        // setup the input search view environment
        if (mHtcAutoCompleteTextView == null) {
            mHtcAutoCompleteTextView = new HtcAutoCompleteTextView(getContext());
            mHtcAutoCompleteTextView.setSingleLine();
            mHtcAutoCompleteTextView.setHint(android.R.string.search_go);
            mHtcAutoCompleteTextView.setGravity(Gravity.CENTER_VERTICAL);
            mHtcAutoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_DONE);
            mHtcAutoCompleteTextView.setMode(HtcAutoCompleteTextView.MODE_DARK_BACKGROUND);
            addView(mHtcAutoCompleteTextView);
        } else if (mHtcAutoCompleteTextView.getParent() == null) {
            addView(mHtcAutoCompleteTextView);
        }

        mHtcAutoCompleteTextView.setTextAppearance(getContext(), mTextAppearance);

        mHtcAutoCompleteTextView.setSupportMode(mSupportMode);

        RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lparams.topMargin = lparams.bottomMargin = mContentInsetTop;
        lparams.addRule(RelativeLayout.CENTER_VERTICAL);
        mHtcAutoCompleteTextView.setLayoutParams(lparams);
    }

    @ExportedProperty(category = "CommonControl")
    private int getIconWidth() {
        if (mIconView == null) {
            return 0;
        }
        Drawable iconDrawable = mIconView.getDrawable();
        if (iconDrawable == null) {
            iconDrawable = mIconDrawable;
        }
        return iconDrawable.getIntrinsicWidth();
    }

    @ExportedProperty(category = "CommonControl")
    private int getCommonOffset() {
        return mDrawablePadding;
    }

    // update the input search padding calculate padding based on different case
    private void updateSearchPadding() {
        final int commonOffset = getCommonOffset();

        int paddingEnd = 0;
        // calculate the internal icon padding
        if (mIconView != null && mIconView.getVisibility() != GONE) {
            paddingEnd += commonOffset + getIconWidth();
        }
        // calculate the internal progress padding
        if (mProgressView != null && mProgressView.getVisibility() != GONE) {
            paddingEnd += commonOffset + mProgressView.getIndeterminateDrawable().getIntrinsicWidth();
        }
        if (ActionBarUtil.IS_SUPPORT_RTL) {
            mHtcAutoCompleteTextView.setPaddingRelative(0, 0, paddingEnd, 0);
        } else {
            mHtcAutoCompleteTextView.setPadding(0, 0, paddingEnd, 0);
        }

    }

    // editable text internal clear all listener
    private View.OnClickListener mInternalListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (mHtcAutoCompleteTextView != null) mHtcAutoCompleteTextView.setText("");
        }
    };

    /**
     * get AutoCompleteTextView in ActionBarSearch.
     *
     * @return AutoCompleteTextView
     */
    public AutoCompleteTextView getAutoCompleteTextView() {
        return mHtcAutoCompleteTextView;
    }

    /**
     * Setup the customize clear icon listener.
     *
     * @param listener on click listener
     */
    public void setClearIconOnClickListener(View.OnClickListener listener) {
        if (mIconView != null) mIconView.setOnClickListener(listener == null ? mInternalListener : listener);
    }

    private HtcImageButton mIconView = null;

    private void setupIconView() {
        // runtime initialize and create icon view
        if (mIconView == null) {
            mIconView = new HtcImageButton(getContext());
            mIconView.setId(android.R.id.icon);
            mIconView.setImageDrawable(mIconDrawable);
            // Sense6.0_UIGL_2.0 page49 the icon should apply 75% opacity when it puts in dark input
            // field
            mIconView.setAlpha(0.75f);
            mIconView.setScaleType(ImageView.ScaleType.CENTER);
            mIconView.setOnClickListener(mInternalListener);
            mIconView.setClickable(true);
            mIconView.setContentDescription(getResources().getString(R.string.va_clear));

            addView(mIconView);
        } else if (mIconView.getParent() == null) {
            addView(mIconView);
        }

        final int commonOffset = getCommonOffset();

        // setup the icon view layout environment
        RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        if (ActionBarUtil.IS_SUPPORT_RTL) {
            mIconView.setPaddingRelative(mIconPaddingLeft, 0, 0, 0);
            lparams.setMarginStart(mIconPaddingLeft != 0 ? 0 : commonOffset);
        } else {
            mIconView.setPadding(mIconPaddingLeft, 0, 0, 0);
            lparams.leftMargin = mIconPaddingLeft != 0 ? 0 : commonOffset;
        }

        if (ActionBarUtil.IS_SUPPORT_RTL) {
            lparams.addRule(RelativeLayout.ALIGN_PARENT_END);
        } else {
            lparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        lparams.addRule(RelativeLayout.CENTER_VERTICAL);

        mIconView.setLayoutParams(lparams);
    }

    /**
     * Set clear icon visibility in ActionBarSearch.
     *
     * @param visibility the visibility of clear icon
     */
    public void setClearIconVisibility(int visibility) {
        // skip to avoid useless operation
        if (mIconView != null && mIconView.getVisibility() == visibility) return;

        setupIconView();

        mIconView.setVisibility(visibility);

        updateSearchPadding();
    }

    private ProgressBar mProgressView = null;

    /**
     * Set progress icon visibility in ActionBarSearch.
     *
     * @param visibility the visibility of progress icon
     */
    public void setProgressVisibility(int visibility) {
        if (mProgressView != null && mProgressView.getVisibility() == visibility) return;

        setupProgressView();
        mProgressView.setVisibility(visibility);
        updateSearchPadding();
    }

    private void setupProgressView() {
        // runtime initialize and create progress view
        if (mProgressView == null) {
            // setup the child view layout environment
            RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            if (ActionBarUtil.IS_SUPPORT_RTL) {
                lparams.addRule(RelativeLayout.ALIGN_PARENT_END);
            } else {
                lparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }
            lparams.addRule(RelativeLayout.CENTER_VERTICAL);

            mProgressView = new ProgressBar(getContext(), null, R.attr.htcActionBarProgressBarStyle);
            mProgressView.setLayoutParams(lparams);
            addView(mProgressView);
        } else if (mProgressView.getParent() == null) {
            addView(mProgressView);
        }
        setupProgressViewParams();
    }

    private void setupProgressViewParams() {
        if (mProgressView == null) return;

        RelativeLayout.LayoutParams progressLayoutParams = (RelativeLayout.LayoutParams) mProgressView.getLayoutParams();

        progressLayoutParams.alignWithParent = true;
        progressLayoutParams.addRule(ALIGN_PARENT_RIGHT, 0);
        if (ActionBarUtil.IS_SUPPORT_RTL) {
            progressLayoutParams.addRule(ALIGN_PARENT_END, 0);
            progressLayoutParams.addRule(RelativeLayout.START_OF, mIconView.getId());
        } else {
            progressLayoutParams.addRule(RelativeLayout.LEFT_OF, mIconView.getId());
        }

        mProgressView.setLayoutParams(progressLayoutParams);
    }

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = MODE_EXTERNAL, to = "MODE_EXTERNAL"),
            @IntToString(from = MODE_AUTOMOTIVE, to = "MODE_AUTOMOTIVE"),
            @IntToString(from = HtcButtonUtil.BACKGROUND_MODE_THEME, to = "MODE_THEME")
    })
    private int mSupportMode = HtcButtonUtil.BACKGROUND_MODE_THEME;

    /**
     * Support special usage for automotive mode only.
     *
     * @param mode support mode
     */
    public void setSupportMode(int mode) {
        // skip to avoid useless operation
        if (mSupportMode == mode || !isValidMode(mode)) return;

        mSupportMode = mode;
        init(null, R.attr.actionBarSearchStyle);
        setupAutomotiveMode();
    }

    // special support for automotive usage
    private void setupAutomotiveMode() {
        setupHtcAutoCompleteTextView();
        setupIconView();
        updateSearchPadding();
    }

    /**
     * Set action bar search icon.
     *
     * @param drawable icon drawable @ you can use drawable.setAlpha(xxxx) for the drawable @ and
     *            then use setIcon(drawable) set it as ActionBarSearch icon.
     */

    public void setIcon(Drawable drawable) {
        setupIconView();
        mIconView.setImageDrawable(drawable);
        updateSearchPadding();
    }

    /**
     * Set action bar search icon.
     *
     * @param resid icon drawable resource id
     */
    public void setIcon(int resid) {
        setIcon(getResources().getDrawable(resid));
    }

    /**
     * Set icon content description for accessibility.
     *
     * @param contentDescription the contentDescription of the icon
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setIconContentDescription(String contentDescription) {
        if (contentDescription != null && mIconView != null) mIconView.setContentDescription(contentDescription);
    }

    private boolean isValidMode(int mode) {
        if (mode == HtcButtonUtil.BACKGROUND_MODE_THEME || mode == MODE_EXTERNAL) {
            return true;
        } else {
            if (HtcBuildFlag.Htc_DEBUG_flag) Log.d("ActionBarSearch", "Invalid mode :" + mode);
            return false;
        }
    }
}
