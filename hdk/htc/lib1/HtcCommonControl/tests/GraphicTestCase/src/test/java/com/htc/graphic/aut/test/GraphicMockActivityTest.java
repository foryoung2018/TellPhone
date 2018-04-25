package com.htc.graphic.aut.test;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.widget.TextView;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.htc.graphic.aut.BuildConfig;
import com.htc.graphic.aut.R;
import com.htc.graphic.aut.GraphicMockActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@Config(constants = BuildConfig.class)
@RunWith(RobolectricGradleTestRunner.class)
public class GraphicMockActivityTest {

    @Test
    public void onCreate() throws Exception {
        Activity activity = Robolectric.setupActivity(GraphicMockActivity.class);

        TextView tv = (TextView) activity.findViewById(R.id.text);
        assertThat(tv.getText()).isEqualTo("TRUE");

        assertThat(tv.getDrawingCache()).isNotEqualTo(null);
    }

    @Test
    public void getDrawingCache() throws Exception {
//        Activity activity = Robolectric.setupActivity(MainActivity.class);
        Activity activity = Robolectric.buildActivity(GraphicMockActivity.class).create().start().resume().visible().get();
        TextView tv = (TextView) activity.findViewById(R.id.text);
        assertThat(tv.getDrawingCache()).isNotEqualTo(null);
        tv.buildDrawingCache();

        Bitmap b = tv.getDrawingCache();
        File fileToSave = new File("TextViewResult"+BuildConfig.BUILD_TYPE+".png");
        try {
            FileOutputStream fos = new FileOutputStream(fileToSave);
            if (b.compress(Bitmap.CompressFormat.PNG, 100, fos) == false)
                System.out.println("Compress/Write failed");
            fos.flush();
            fos.close();
        } catch (Exception e) {
            System.out.println("Can't save the screenshot! Requires write permission (android.permission.WRITE_EXTERNAL_STORAGE) in AndroidManifest.xml of the application under test.");
        }
    }

    @Test
    public void getWidthAndHeight() throws Exception {
//        Activity activity = Robolectric.setupActivity(MainActivity.class);
        Activity activity = Robolectric.buildActivity(GraphicMockActivity.class).create().start().resume().visible().get();
        TextView tv = (TextView) activity.findViewById(R.id.text);
        assertThat(tv.getWidth()).isGreaterThan(0);
        assertThat(tv.getHeight()).isGreaterThan(0);
    }
}