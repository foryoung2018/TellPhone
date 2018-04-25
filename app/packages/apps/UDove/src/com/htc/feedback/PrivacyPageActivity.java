package com.htc.feedback;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ActionMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htc.feedback.reportagent.Common;
import com.htc.lib1.cc.app.HtcProgressDialog;
import com.htc.lib1.cc.app.OnActionModeChangedListener;
import com.htc.lib1.cc.util.ActionBarUtil;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.HtcAlertDialog;
import com.htc.lib1.theme.ThemeType;
import com.htc.lib1.useragree.HtcUserAgreeDialog;
import com.htc.lib1.useragree.OnUserClickListener;
import com.htc.lib1.useragree.UserAgreeContent;
import com.htc.lib2.configuration.HtcWrapConfiguration;

public class PrivacyPageActivity extends Activity {
    private static final String TAG = "PrivacyPageActivity";
    private static final boolean LOG = Common.DEBUG;
    public static final String TYPE_PRIVACY_POLICY = "privacy_policy";
    public static final String TYPE_LEARN_MORE = "learn_more";
    private static final String ACTIVITY_LEARN_MORE = "com.htc.feedback.LearnMoreActivity";
	private static final String INTENT_KEY_LAUNCHED_BY = "LaunchedBy";
	private static final String INTENT_VALUE_LAUNCHED_BY = "LaunchedByOOBE";
    
    private float mHtcFontscale = 0.0f;
    boolean mIsThemeChanged = false;
    HtcCommonUtil.ThemeChangeObserver mThemeChangeObserver = new HtcCommonUtil.ThemeChangeObserver() {
		@Override
		public void onThemeChange(int type) {
			if(type == ThemeType.HTC_THEME_FULL || type == ThemeType.HTC_THEME_CC) {
				mIsThemeChanged = true;
			}
		}
	};
	private ActionMode mActionMode = null;
	private ColorDrawable mColorDrawable;
	private Drawable mTextureDrawable;

    private WebView mWebView;
    private HtcProgressDialog mProgressDialog;
    private HtcAlertDialog.Builder mBuilder;
    private HtcAlertDialog mDialog;
    private String mUrl;
    private boolean mIsLearnMore = false;
    
    private static final String DEFAULT_COUNTRY_SITE = "us";
    private static final Map<String, String> sLocaleToCountrySiteMap;
    static {
    	sLocaleToCountrySiteMap = new HashMap<String, String>();
    	sLocaleToCountrySiteMap.put("ar", "mea-sa");
    	sLocaleToCountrySiteMap.put("bg", "bg");
    	sLocaleToCountrySiteMap.put("cs", "cz");
    	sLocaleToCountrySiteMap.put("da", "dk");
    	sLocaleToCountrySiteMap.put("de", "de");
    	sLocaleToCountrySiteMap.put("de_at", "at");
    	sLocaleToCountrySiteMap.put("de_ch", "ch-de");
    	sLocaleToCountrySiteMap.put("el", "gr");
    	sLocaleToCountrySiteMap.put("el_cy", "cy");
    	sLocaleToCountrySiteMap.put("en", "us");
    	sLocaleToCountrySiteMap.put("en_au", "au");
    	sLocaleToCountrySiteMap.put("en_ca", "ca");
    	sLocaleToCountrySiteMap.put("en_gb", "uk");
    	sLocaleToCountrySiteMap.put("en_hk", "hk-en");
    	sLocaleToCountrySiteMap.put("en_id", "sea");
    	sLocaleToCountrySiteMap.put("en_ie", "ie");
    	sLocaleToCountrySiteMap.put("en_in", "in");
    	sLocaleToCountrySiteMap.put("en_nz", "nz");
    	sLocaleToCountrySiteMap.put("en_us", "us");
    	sLocaleToCountrySiteMap.put("es", "es");
    	sLocaleToCountrySiteMap.put("es_bz", "latam");
    	sLocaleToCountrySiteMap.put("et", "ee");
    	sLocaleToCountrySiteMap.put("fi", "fi");
    	sLocaleToCountrySiteMap.put("fr", "fr");
    	sLocaleToCountrySiteMap.put("fr_be", "be-fr");
    	sLocaleToCountrySiteMap.put("fr_ca", "ca-fr");
    	sLocaleToCountrySiteMap.put("fr_ch", "ch-fr");
    	sLocaleToCountrySiteMap.put("fr_lu", "lu");
    	sLocaleToCountrySiteMap.put("fr_sa", "mea-fr");
    	sLocaleToCountrySiteMap.put("hr", "hr");
    	sLocaleToCountrySiteMap.put("hu", "hu");
    	sLocaleToCountrySiteMap.put("in", "id");
    	sLocaleToCountrySiteMap.put("it", "it");
    	sLocaleToCountrySiteMap.put("it_ch", "ch-it");
    	sLocaleToCountrySiteMap.put("ja", "jp");
    	sLocaleToCountrySiteMap.put("kz", "kz");
    	sLocaleToCountrySiteMap.put("lt", "lt");
    	sLocaleToCountrySiteMap.put("lv", "lv");
    	sLocaleToCountrySiteMap.put("mt", "mt");
    	sLocaleToCountrySiteMap.put("my", "mm");
    	sLocaleToCountrySiteMap.put("nl", "nl");
    	sLocaleToCountrySiteMap.put("nl_be", "be-nl");
    	sLocaleToCountrySiteMap.put("no", "no");
    	sLocaleToCountrySiteMap.put("pl", "pl");
    	sLocaleToCountrySiteMap.put("pt", "pt");
    	sLocaleToCountrySiteMap.put("pt_br", "br");
    	sLocaleToCountrySiteMap.put("ro", "ro");
    	sLocaleToCountrySiteMap.put("ru", "ru");
    	sLocaleToCountrySiteMap.put("sk", "sk");
    	sLocaleToCountrySiteMap.put("sl", "si");
    	sLocaleToCountrySiteMap.put("sr", "rs");
    	sLocaleToCountrySiteMap.put("sv", "se");
    	sLocaleToCountrySiteMap.put("th", "th");
    	sLocaleToCountrySiteMap.put("tr", "tr");
    	sLocaleToCountrySiteMap.put("uk", "ua");
    	sLocaleToCountrySiteMap.put("vi", "vn");
    	sLocaleToCountrySiteMap.put("zh_cn", "cn");
    	sLocaleToCountrySiteMap.put("zh_hk", "hk-tc");
    	sLocaleToCountrySiteMap.put("zh_tw", "tw");
    }
    
    public void showNoConnectionDialog(Context ctx1) {
        final Context ctx = ctx1;
        HtcAlertDialog.Builder builder = new HtcAlertDialog.Builder(ctx);
        builder.setCancelable(false);
        builder.setMessage(R.string.dialog_msg_network_unavailable);
        builder.setTitle(R.string.dialog_title_network_unavailable);
        builder.setPositiveButton(R.string.btn_feedback_settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ctx.startActivity(new Intent(Settings.ACTION_SETTINGS));
                ((Activity) ctx).finish();
                return;
            }
        });
        builder.setNegativeButton(R.string.btn_feedback_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((Activity) ctx).finish();
                return;
            }
        });

        HtcAlertDialog connDialog = builder.create();
        showDialogWithHidingNavigationBarInNeed(connDialog);
    }

    // Eric Lu, 2016.07.26, To show Network Access Dialog in China sense
    private void showUserAgreeDialog() {
        final Activity thisActivity = PrivacyPageActivity.this;
        // Setup the title of HtcUserAgreeDialog
        UserAgreeContent content = new UserAgreeContent(thisActivity.getString(R.string.setting_name));
        // Launch a HtcUserAgreeDialog
        HtcUserAgreeDialog.launchUserAgreeDialog(thisActivity, new OnUserClickListener() {

            @Override
            public void onUserClick(int which) {
                // Callback of HtcUserAgreeDialog
                // RESULT_YES: User agrees to proceed the application
                // RESULT_NO: Application should terminate here
                if (which == HtcUserAgreeDialog.RESULT_YES) {
                    // Keep using the application
                    Log.d(TAG, "Agree to access network");
                    initialize();
                } else {
                    Log.d(TAG, "Not agree to access network");
                    thisActivity.finish();
                }
            }
        }, content);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LOG)
            Log.d(TAG, "onCreate");
        // sy_wang, 20131218, To apply Htc Huge font size
        // Currently this activity only contains a WebView. But WebView isn't
        // included in scope of font size change, so this doesn't take effect on it.
        // If there's another item, huge font size can apply on it
        HtcWrapConfiguration.applyHtcFontscale(this);
        mHtcFontscale = getResources().getConfiguration().fontScale;
        super.onCreate(savedInstanceState);
        hideNavigationBarInNeed();
        // sy_wang, 20140103, To apply multiple theme
        HtcCommonUtil.initTheme(this, HtcCommonUtil.BASELINE);
        HtcCommonUtil.registerThemeChangeObserver(this, ThemeType.HTC_THEME_FULL, mThemeChangeObserver);
        HtcCommonUtil.registerThemeChangeObserver(this, ThemeType.HTC_THEME_CC, mThemeChangeObserver);

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            if (LOG)
                Log.d(TAG, "Network is not connected");
            // show warning dialog
            showNoConnectionDialog(PrivacyPageActivity.this);
        } else {
            if (LOG)
                Log.d(TAG, "Network is connected");
            // eric_lu, 20160726, User agree dialog for China sense
            if (HtcUserAgreeDialog.needShowUserAgreeDialog(PrivacyPageActivity.this)) {
                showUserAgreeDialog();
            } else {
                if (LOG)
                    Log.d(TAG, "Not China Sense");
                initialize();
            }
        }
    }

    private void initialize() {
        final RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.specific_feedback_tos,null);
        mWebView = (WebView) layout.findViewById(R.id.tos_page);

        try {
            String activityName = getIntent().getComponent().getClassName();
            if (LOG)
                Log.d(TAG, "Get activity name: " + activityName);

            if (ACTIVITY_LEARN_MORE.equals(activityName))
                mIsLearnMore = true;
        } catch (Exception e) {
            Log.e(TAG, "Exception when check activity name", e);
        }

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            // Refer to
            // /MyHTC_6.0/module-libidentity/src/com/htc/cs/identity/dialog/TermDialog.java
            // -- begin

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.d(TAG, "onReceivedError, errorCode=" + errorCode + ", description=" + description + ", failingUrl="+ failingUrl);
                mWebView.setVisibility(View.GONE);

                TextView textView = (TextView) layout.findViewById(R.id.error_page);
                if (textView != null) {
                    String msg = getString(R.string.webpage_not_available_title) + "\n\n"
                            + String.format(getString(R.string.webpage_not_available_message),
                                    mIsLearnMore == true ? "https://www.htc.com/us/terms/learn-more/?text"
                                            : "https://www.htc.com/us/terms/privacy/?text");
                    textView.setText(msg);
                    textView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // When webpage not available, don't let user click the privacy policy url
                if (mUrl != null && mUrl.equals(url)) {
                    return true;
                }
                /*
                 * Try to use system browser to open links when user clicks onit.
                 * If failed then fallback to default webview behavior.
                 */
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, "Exception on shouldOverrideUrlLoading", e);
                    return super.shouldOverrideUrlLoading(mWebView, url);
                }
                return true;
            }
            // Refer to
            // /MyHTC_6.0/module-libidentity/src/com/htc/cs/identity/dialog/TermDialog.java
            // -- end

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        });

        mBuilder = new HtcAlertDialog.Builder(PrivacyPageActivity.this);
        if (mIsLearnMore)
            mBuilder.setTitle(R.string.learn_more);
        else
            mBuilder.setTitle(R.string.title_htc_tos);
        mBuilder.setView(layout);
        mBuilder.setNegativeButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Dismiss the dialog
                dialog.cancel();
            }
        });
        mBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        mDialog = mBuilder.create();
        mDialog.setOnActionModeChangedListener(new OnActionModeChangedListener() {
            @Override
            public void onActionModeStarted(ActionMode mode) {
                if (LOG)
                    Log.d(TAG, "onActionModeStarted");
                mActionMode = mode;

                int categoryColor = HtcCommonUtil.getCommonThemeColor(PrivacyPageActivity.this,
                        com.htc.lib1.cc.R.styleable.ThemeColor_multiply_color);
                mColorDrawable = new ColorDrawable(categoryColor);
                mTextureDrawable = HtcCommonUtil.getCommonThemeTexture(PrivacyPageActivity.this,
                        com.htc.lib1.cc.R.styleable.CommonTexture_android_headerBackground);

                ActionBarUtil.setActionModeBackground(PrivacyPageActivity.this, mActionMode, mColorDrawable);
                switchActionModeBkg(getResources().getConfiguration().orientation);
            }
        });
        showDialogWithHidingNavigationBarInNeed(mDialog);

        mProgressDialog = new HtcProgressDialog(PrivacyPageActivity.this);
        mProgressDialog.setMessage(getString(R.string.privacy_loading_message));
        showDialogWithHidingNavigationBarInNeed(mProgressDialog);

        if (mIsLearnMore)
            mUrl = "https://www.htc.com/" + getCountrySite() + "/terms/learn-more/?text";
        else
            mUrl = "https://www.htc.com/" + getCountrySite() + "/terms/privacy/?text";
        Log.d(TAG, "url=" + mUrl);
        mWebView.loadUrl(mUrl);
    }
    
    // Since htc.com page doesn't use language code as Android's for their country site, we have to transfer it according to the mapping table they provided
    private String getCountrySite() {
    	String countrySite = DEFAULT_COUNTRY_SITE;
    	try {
			String languageCode = getResources().getConfiguration().locale.getLanguage().toLowerCase(Locale.ENGLISH);
			String countryCode = getResources().getConfiguration().locale.getCountry().toLowerCase(Locale.ENGLISH);
			String locale = languageCode + "_" + countryCode;
	    	if (LOG) Log.d(TAG, "locale=" + locale);
			
			if ((countrySite = sLocaleToCountrySiteMap.get(locale)) == null) {
				if ((countrySite = sLocaleToCountrySiteMap.get(languageCode)) == null)
					countrySite = DEFAULT_COUNTRY_SITE;
			}
		} catch (Exception e) {
			Log.e(TAG, "Exception in getCountrySite()", e);
		}
    	
    	return countrySite;
    }
    
    @Override
    protected void onStop() {
        if (LOG)
            Log.d(TAG, "onStop");
        super.onStop();
        if(mWebView!=null)
        	mWebView.stopLoading();
    }

    @Override
    protected void onDestroy() {
        if (LOG)
            Log.d(TAG, "onDestroy");
        super.onDestroy();
        if(mWebView!=null)
        	mWebView.destroy();
        
        HtcCommonUtil.unregisterThemeChangeObserver(ThemeType.HTC_THEME_FULL, mThemeChangeObserver);
        HtcCommonUtil.unregisterThemeChangeObserver(ThemeType.HTC_THEME_CC, mThemeChangeObserver);
    }

	@Override
    public void onResume() {
        if (LOG)
            Log.d(TAG, "onResume()");
        super.onResume();
        // sy_wang, 20140103, To apply multiple theme and Htc Huge font size, call recreate only once for these two conditions
        boolean needRecreate = false;
        needRecreate |= HtcWrapConfiguration.checkHtcFontscaleChanged(this, mHtcFontscale);
        needRecreate |= mIsThemeChanged;
        
        if (needRecreate == true) {
        	getWindow().getDecorView().postOnAnimation(new Runnable() {
        		@Override
        		public void run() {
        			HtcCommonUtil.notifyChange(PrivacyPageActivity.this, HtcCommonUtil.TYPE_THEME);
        			recreate();
        		}
        	});
        	mIsThemeChanged = false;
        }
    }
	
	@Override
	public void onConfigurationChanged (Configuration newConfig) {
		if (LOG)
			Log.d(TAG, "onConfigurationChanged");
		super.onConfigurationChanged(newConfig);
		HtcCommonUtil.updateCommonResConfiguration(this);
		
		switchActionModeBkg(newConfig.orientation);
	}
	
	private void switchActionModeBkg(int orientation) {
		if(mActionMode != null && mTextureDrawable != null) {
			if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
				ActionBarUtil.setActionModeBackground(PrivacyPageActivity.this, mActionMode, mColorDrawable);
			} else {
				ActionBarUtil.setActionModeBackground(PrivacyPageActivity.this, mActionMode, mTextureDrawable);
			}
		}
	}

	private void showDialogWithHidingNavigationBarInNeed(Dialog dialog) {
		if(dialog == null)
			return;

		if(INTENT_VALUE_LAUNCHED_BY.equals(getIntent().getStringExtra(INTENT_KEY_LAUNCHED_BY))) {
            // [Workaround to prevent from shortly showing navigation bar] 1. Add FLAG_NOT_FOCUSABLE
            if(dialog.getWindow() != null)
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }

		dialog.show();

		if(INTENT_VALUE_LAUNCHED_BY.equals(getIntent().getStringExtra(INTENT_KEY_LAUNCHED_BY))) {
            hideNavigationBar(dialog.getWindow());

			// [Workaround to prevent from shortly showing navigation bar] 2. Remove FLAG_NOT_FOCUSABLE
            if(dialog.getWindow() != null)
			    dialog.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
		}
	}

	private void hideNavigationBarInNeed() {
        if (INTENT_VALUE_LAUNCHED_BY.equals(getIntent().getStringExtra(INTENT_KEY_LAUNCHED_BY)))
            hideNavigationBar(getWindow());
    }

    private void hideNavigationBar(Window w) {
        if(w == null)
            return;

        View decorView = w.getDecorView();
        if (decorView != null)
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
