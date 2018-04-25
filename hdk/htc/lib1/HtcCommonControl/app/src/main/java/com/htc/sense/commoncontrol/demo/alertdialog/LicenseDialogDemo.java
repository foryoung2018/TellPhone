package com.htc.sense.commoncontrol.demo.alertdialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.htc.lib1.cc.widget.CommonCreativeLicenseDialog;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class LicenseDialogDemo extends CommonDemoActivityBase {
    private Context mContext=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog_license_app);
        Button license=(Button)findViewById(R.id.text_dialog_btn);
        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonCreativeLicenseDialog dialog = new CommonCreativeLicenseDialog(mContext);
                dialog.show();
            }
        });
    }



}
