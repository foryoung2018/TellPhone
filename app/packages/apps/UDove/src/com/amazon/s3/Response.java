//  This software code is made available "AS IS" without warranties of any
//  kind.  You may copy, display, modify and redistribute the software
//  code either by itself or as incorporated into your code; provided that
//  you do not remove any proprietary notices.  Your use of this software
//  code is at your own risk and you waive any claim against Amazon
//  Digital Services, Inc. or its affiliates with respect to your use of
//  this software code. (c) 2006 Amazon Digital Services, Inc. or its
//  affiliates.

package com.amazon.s3;

import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

//For consistent Log tag
import com.htc.feedback.reportagent.util.Log;

/**
 * The parent class of all other Responses.  This class keeps track of the
 * HttpURLConnection response.
 */
public class Response {
    private final static String TAG = "S3";

    private int responseCode = -1;
    private String message = null;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(HttpURLConnection connection) {
        if (connection == null) {
            return;
        }

        try {
            this.responseCode = connection.getResponseCode();
        } catch (IOException e) {
            Log.e(TAG, "IOException in setResponseCode", e);
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(HttpURLConnection connection) {
        StringBuilder builder = new StringBuilder();
        builder.append("Response [connection=");
        builder.append(connection);
        builder.append("]");

        if(connection == null) {
            this.message = builder.toString();
            return;
        }

        InputStream is = null;
        InputStreamReader isr = null;
        try {
            builder.append("\nResponse Code == " + connection.getResponseCode());
            Map headers = connection.getHeaderFields();
            if (headers != null) {
                for (Iterator i = headers.keySet().iterator(); i.hasNext();) {
                    String key = (String) i.next();
                    for (Iterator j = ((List) headers.get(key)).iterator(); j
                            .hasNext();) {
                        String value = (String) j.next();
                        builder.append("\n" + key + ": " + value);
                    }
                }
            }

            builder.append("\n\n" + connection.getResponseMessage());

            is = connection.getErrorStream();
            if (is != null) {
                isr = new InputStreamReader(is);
                char[] input = new char[256];
                int length = 0;
                while((length = isr.read(input)) != -1)    {
                    builder.append(input, 0, length);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Response:: ", e);
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    Log.e(TAG, "IOException when close isr", e);
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e(TAG, "IOException when close is", e);
                }
            }
        }

        this.message = builder.toString();
    }

}
