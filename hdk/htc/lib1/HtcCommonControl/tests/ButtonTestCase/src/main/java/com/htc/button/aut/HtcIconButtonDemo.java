
package com.htc.button.aut;

import android.os.Bundle;
import com.htc.aut.ActivityBase;

public class HtcIconButtonDemo extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.htcbutton_demos_iconbutton);

    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }

}
