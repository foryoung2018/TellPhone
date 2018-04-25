package com.htc.lib1.cc.widget;
import com.htc.lib1.cc.R;

class HtcInputFieldUtil {
    /**
     * The public constant for user to set the mode of this widget.
     * This is used for user to put this widget on bright background.
     * For example: put this widget in a white scene.
     * This is the default value.
     */
    static final int MODE_BRIGHT_BACKGROUND = 0;

    /**
     * The public constant for user to set the mode of this widget.
     * This is used for user to put this widget on dark background.
     * For example: put this widget in a black scene.
     */
    static final int MODE_DARK_BACKGROUND = 1;

    final static float REST_ALPHA = 1.0f;
    //>> Sense 60: disable alpha changes
    final static float DISABLED_ALPHA_LIGHT = 0.5f;
    final static float DISABLED_ALPHA_DARK = 0.4f;

    //>> Sense 60: default font style different
    final static int FONT_STYLE_LIGHT = R.style.input_default_m;
    final static int FONT_STYLE_DARK = R.style.b_button_primary_l;

    static int mapXMLMode(int xmlMode){
        switch (xmlMode){
           case 0:
              return MODE_BRIGHT_BACKGROUND;
           case 1:
              return MODE_DARK_BACKGROUND;
           default:
              return MODE_BRIGHT_BACKGROUND;
        }
    }

    //>> Sense 60: default font style according to mode
    static int getDefaultFontStyleByMode(int mode){
       if(mode == MODE_DARK_BACKGROUND){
            return FONT_STYLE_DARK;
        } else{
            return FONT_STYLE_LIGHT;
        }
    }
}