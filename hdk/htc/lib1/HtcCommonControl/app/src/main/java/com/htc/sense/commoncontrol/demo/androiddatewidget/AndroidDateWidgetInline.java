
package com.htc.sense.commoncontrol.demo.androiddatewidget;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.TimePicker;

import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class AndroidDateWidgetInline extends CommonDemoActivityBase {

    // where we display the selected date and time
    private TextView mTimeDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.date_widgets_example_2);

        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setCurrentHour(12);
        timePicker.setCurrentMinute(15);

        mTimeDisplay = (TextView) findViewById(R.id.dateDisplay);

        updateDisplay(12, 15);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                updateDisplay(hourOfDay, minute);
            }
        });
    }

    private void updateDisplay(int hourOfDay, int minute) {
        mTimeDisplay.setText(
                new StringBuilder()
                        .append(pad(hourOfDay)).append(":")
                        .append(pad(minute)));
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

}
