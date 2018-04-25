
package com.htc.imagebutton.aut;

import android.os.Bundle;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.widget.HtcImageButton;

public class HtcImageButtonAutActivity extends ActivityBase {
    private HtcImageButton imgbtn, imgbtn_coloron, imgbtn_inpress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.htcimagebutton_aut_layout);
        imgbtn_coloron = (HtcImageButton) findViewById(R.id.img_coloron);
        imgbtn_coloron.setColorOn(true);

        imgbtn_inpress = (HtcImageButton) findViewById(R.id.img_inpress);
        imgbtn_inpress.stayInPress(true);

    }

}
