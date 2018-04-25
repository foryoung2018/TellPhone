package com.htc.feedback;

import android.app.Activity;
import android.app.ApplicationErrorReport;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.MenuItem;
import android.view.Menu;
import android.content.res.Configuration;

import com.htc.feedback.reportagent.Common;
import com.htc.feedback.policy.UPolicy;
import com.htc.lib1.theme.NavBarColorUtil;

public class ReportActivity extends Activity implements OnClickListener {
    private static final String TAG = "ReportActivity";
    private static final boolean LOG = Common.DEBUG;
    // UI items
    private EditText mDescEditText;
	private Button mCancelBtn = null;
	private Button mSendBtn = null;
    private TextView mPreviewLink;
    // Report informations
    private ApplicationErrorReport mReport;
    private ApplicationInfo mAppInfo;
    // Menu option id
    private static final int MENU_ID_SETTINGS = Menu.FIRST+101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LOG)
            Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specific_feedback_report);
        
        // init preview link
        mPreviewLink = (TextView) findViewById(R.id.preview_link);
        mPreviewLink.setOnClickListener(this);

        // init note and privacy policy link
    	TextView note = (TextView) findViewById(R.id.report_note);
    	note.setText(SettingsPreferenceFragment.getNoteSpanStr(ReportActivity.this, getApplicationContext()));
    	note.setMovementMethod(LinkMovementMethod.getInstance());

        // init edit text
        mDescEditText = (EditText) findViewById(R.id.desc_edit);
        if (ReportConfig.isShippingRom()) {
            mDescEditText.setVisibility(View.GONE);
        } else {
            // hide IME, wierd
            mDescEditText.setInputType(InputType.TYPE_NULL);
            setInputMinLines(getResources().getConfiguration());
            mDescEditText.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    mDescEditText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE
                            | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT);
                    mDescEditText.onTouchEvent(event);
                    return true;
                }
            });
        }

        initActionFooterBar(getIntent());

        //navigation bar color change feature(Only for SW navigation bar and 18:9 device)
        NavBarColorUtil.setNavBarBkg(getWindow());
    }
    
    private void sendErrorReport() {
        // support policy
        UPolicy policy = new UPolicy(UPolicy.APPID_ERROR_REPORT, getApplicationContext());
        String category = (mReport == null) ? getIntent().getStringExtra("tag") : getIntent().getStringExtra("dropboxTag");
        if (!UPolicy.canLogErrorReport(UPolicy.APPID_ERROR_REPORT, category, getApplicationContext())) {
            if (LOG)
                Log.d(TAG, "disable by UPolicy: " + category);
            finish();
            return;
        }

        Intent sendIntent = new Intent();
        sendIntent.putExtras(getIntent());
        sendIntent.putExtra("msg", mDescEditText.getText().toString());
        if (mReport != null && mReport.type == ApplicationErrorReport.TYPE_ANR
                && !ReportConfig.isShippingRom()) {
            sendIntent.setClass(this, FeedbackBugReportActivity.class);
            startActivity(sendIntent);
        } else {
            sendIntent.setClass(this, FeedbackService.class);
            startService(sendIntent);
        }
        finish();
    }
    
    private void setInputMinLines(Configuration newConfig) {
        if (Configuration.ORIENTATION_PORTRAIT == newConfig.orientation)
        	mDescEditText.setMinLines(7);
        else if (Configuration.ORIENTATION_LANDSCAPE == newConfig.orientation)
        	mDescEditText.setMinLines(4); 
    }
    
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	if (LOG)
    		Log.d(TAG, "onConfigurationChanged");
    	setInputMinLines(newConfig);
    }

    private void initActionFooterBar(Intent intent) {
    	mCancelBtn = (Button) findViewById(R.id.cancel_btn);
		mCancelBtn.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View arg0) 
			{
				finish();
			}	
		});
		mSendBtn = (Button) findViewById(R.id.send_btn);
		mSendBtn.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View arg0) 
			{
				sendErrorReport();
			}	
		});
    	
		getActionBar().setTitle(R.string.feedback_error_report_title);
        
        mReport = (ApplicationErrorReport) intent.getParcelableExtra(Intent.EXTRA_BUG_REPORT);
        PackageManager pm = getPackageManager();
        try {
            String packageName = (mReport != null) ? mReport.packageName : "android";
            mAppInfo = pm.getApplicationInfo(packageName, 0);
            getActionBar().setSubtitle(pm.getApplicationLabel(mAppInfo).toString());
        } catch (Exception e) {
            Log.e(TAG, "fail to get application info", e);
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
        case MENU_ID_SETTINGS:
        	Intent settingIntent = new Intent("com.htc.feedback.REPORT_SETTINGS");
        	startActivity(settingIntent);
        	break;
        }
        return super.onOptionsItemSelected(item);  	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, MENU_ID_SETTINGS, 0, R.string.btn_feedback_settings);
        return true;
    }
    
    @Override
    public void onResume() {
        if (LOG)
            Log.d(TAG, "onResume()");
        super.onResume();
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (LOG)
            Log.d(TAG, "onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);
    }
    
    public void onClick(View view) {
        if (view == mPreviewLink) {
            Intent previewIntent = new Intent(ReportActivity.this, PreviewActivity.class);
            previewIntent.putExtras(getIntent());
            startActivity(previewIntent);
        }
    }
        
}
