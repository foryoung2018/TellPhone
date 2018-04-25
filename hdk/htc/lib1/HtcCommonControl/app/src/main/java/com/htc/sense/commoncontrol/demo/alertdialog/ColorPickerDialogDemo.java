
package com.htc.sense.commoncontrol.demo.alertdialog;

import com.htc.lib1.cc.widget.ColorPickerDialog;
import com.htc.lib1.cc.widget.ColorPickerDialog.ColorSelectedListener;
import com.htc.lib1.cc.widget.HtcGridView;
import com.htc.lib1.cc.widget.ColorPickerDialog.Builder;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ColorPickerDialogDemo extends CommonDemoActivityBase implements
        android.view.View.OnClickListener {

    private static final int DIALOG_ONEBUTTON = 1;
    private static final int DIALOG_TWOBUTTONS = 2;
    private static final int DIALOG_NOBUTTON = 3;
    private static final int DIALOG_PADDING = 4;
    private static final int DIALOG_ITEM = 5;
    private static final int DIALOG_NONUMCOL = 6;
    private static final int DIALOG_NULLARRAYCOLOR = 7;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.colorpicker_dialog);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.onebutton:
                showDialog(DIALOG_ONEBUTTON);
                break;

            case R.id.twobutton:
                showDialog(DIALOG_TWOBUTTONS);
                break;

            case R.id.nobutton:
                showDialog(DIALOG_NOBUTTON);
                break;

            case R.id.nopadding:
                showDialog(DIALOG_PADDING);
                break;

            case R.id.itemclick:
                showDialog(DIALOG_ITEM);
                break;

            case R.id.nonumcol:
                showDialog(DIALOG_NONUMCOL);
                break;

            case R.id.nullarraycolor:
                showDialog(DIALOG_NULLARRAYCOLOR);
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final String va_ok = getString(R.string.va_ok);
        final String va_cancel = getString(R.string.va_cancel);
        int[] oneBtn = {
            0x00FF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF,
            0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF, 0x00FF0000,
            0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF, 0x00FF0000
        };

        int[] twoBtn = {
            0x00FF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF,
            0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF, 0x00FF0000
        };

        int[] noBtn = {
            0x00FF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF,
            0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF, 0x00FF0000,
            0xFFFF00FF, 0x00FF0000, 0xFFFFFF00, 0xFFFF00FF,
            0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF, 0x00FF0000,
        };
        switch (id) {

            case DIALOG_ONEBUTTON:
                return new ColorPickerDialog.Builder(mContext)
                    .setNumColumns(3)
                    .setColorArray(oneBtn)
                    .setTitle(R.string.colorPickerdialog)
                    .setNegativeButton(va_cancel, null)
                    .create();

            case DIALOG_TWOBUTTONS:
                return new ColorPickerDialog.Builder(mContext)
                    .setNumColumns(4)
                    .setColorArray(twoBtn)
                    .setTitle(R.string.colorPickerdialog)
                    .setPositiveButton(va_ok, null)
                    .setNegativeButton(va_cancel, null)
                    .create();

            case DIALOG_NOBUTTON:
                return new ColorPickerDialog.Builder(mContext)
                    .setNumColumns(4)
                    .setColorArray(noBtn)
                    .setTitle(R.string.colorPickerdialog)
                    .create();

            case DIALOG_PADDING:
                ColorPickerDialog.Builder builder = new Builder(mContext);
                builder.setTitle(R.string.colorPickerdialog_setpadding);
                builder.setNumColumns(4);
                builder.setColorArray(noBtn);
                ColorPickerDialog dialog = builder.show();
                HtcGridView gv = (HtcGridView) dialog.findViewById(android.R.id.list);
                int gap = mContext.getResources().getDimensionPixelOffset(R.dimen.leading);
                gv.setPadding(gap, gap, gap, 0);
                return dialog;

            case DIALOG_ITEM:
                return new ColorPickerDialog.Builder(mContext)
                    .setNumColumns(4)
                    .setColorArray(noBtn)
                    .setOnColorClickListener(new ColorSelectedListener() {

                        @Override
                        public void setSelectedColor(int color) {
                            Toast.makeText(mContext, "The value of color = " + color, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setTitle(R.string.colorPickerdialog)
                    .create();

            case DIALOG_NONUMCOL:
                return new ColorPickerDialog.Builder(mContext)
                    .setNumColumns(7)
                    .setColorArray(noBtn)
                    .setTitle(R.string.colorPickerdialog)
                    .create();

            case DIALOG_NULLARRAYCOLOR:
                return new ColorPickerDialog.Builder(mContext)
                    .setNumColumns(4)
                    .setColorArray(null)
                    .setTitle(R.string.colorPickerdialog)
                    .create();
        }
        return null;
    }
}
