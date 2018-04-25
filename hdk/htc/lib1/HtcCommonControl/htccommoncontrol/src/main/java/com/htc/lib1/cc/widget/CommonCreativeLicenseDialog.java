package com.htc.lib1.cc.widget;

import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.htc.lib1.cc.R;

/**
 * @hide
 * Helper class to License Dialog for replacing HTC asset by Google asset.
 */
public class CommonCreativeLicenseDialog extends HtcAlertDialog {
    public CommonCreativeLicenseDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final Context context = getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.alert_dialog_license, null);
        TextView licencelink = (TextView) v.findViewById(android.R.id.text1);
        licencelink.setMovementMethod(LinkMovementMethod.getInstance());
        setTitle(R.string.license_dialog_title);
        setView(v);
        super.onCreate(savedInstanceState);
    }

}
