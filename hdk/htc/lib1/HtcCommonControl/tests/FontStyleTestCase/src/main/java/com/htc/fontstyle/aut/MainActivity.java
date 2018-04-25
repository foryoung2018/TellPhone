package com.htc.fontstyle.aut;

import com.htc.aut.ActivityBase;

import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;

public class MainActivity extends ActivityBase {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String styleName = intent.getStringExtra("styleName");
        int styleId = intent.getIntExtra("styleId", 0);

        if (null != styleName && 0 != styleId) {
            tv = (TextView) findViewById(R.id.tv);
            tv.setTextAppearance(this, styleId);
            tv.setText(styleName);
        }
    }

    @Override
    protected boolean isInitCategory() {
        return false;
    }
}
