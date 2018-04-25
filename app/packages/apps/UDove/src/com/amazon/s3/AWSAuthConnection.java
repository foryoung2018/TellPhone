//  This software code is made available "AS IS" without warranties of any
//  kind.  You may copy, display, modify and redistribute the software
//  code either by itself or as incorporated into your code; provided that
//  you do not remove any proprietary notices.  Your use of this software
//  code is at your own risk and you waive any claim against Amazon
//  Digital Services, Inc. or its affiliates with respect to your use of
//  this software code. (c) 2006-2007 Amazon Digital Services, Inc. or its
//  affiliates.

package com.amazon.s3;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * An interface into the S3 system. It is initially configured with
 * authentication and connection parameters and exposes methods to access and
 * manipulate S3 data.
 */
public class AWSAuthConnection {
	public static final String LOCATION_DEFAULT = null;
	public static final String LOCATION_EU = "EU";

	private String awsAccessKeyId;
	private String awsSecretAccessKey;
	private boolean isSecure;
	private String server;
	private int port;
	private CallingFormat callingFormat;

	public AWSAuthConnection(String awsAccessKeyId, String awsSecretAccessKey) {
		this(awsAccessKeyId, awsSecretAccessKey, true);
	}

	public AWSAuthConnection(String awsAccessKeyId, String awsSecretAccessKey,
			boolean isSecure) {
		this(awsAccessKeyId, awsSecretAccessKey, isSecure, Utils.DEFAULT_HOST);
	}

	public AWSAuthConnection(String awsAccessKeyId, String awsSecretAccessKey,
			boolean isSecure, String server) {
		this(awsAccessKeyId, awsSecretAccessKey, isSecure, server,
				isSecure ? Utils.SECURE_PORT : Utils.INSECURE_PORT);
	}

	public AWSAuthConnection(String awsAccessKeyId, String awsSecretAccessKey,
			boolean isSecure, String server, int port) {
		this(awsAccessKeyId, awsSecretAccessKey, isSecure, server, port,
				CallingFormat.getSubdomainCallingFormat());

	}

	public AWSAuthConnection(String awsAccessKeyId, String awsSecretAccessKey,
			boolean isSecure, String server, CallingFormat format) {
		this(awsAccessKeyId, awsSecretAccessKey, isSecure, server,
				isSecure ? Utils.SECURE_PORT : Utils.INSECURE_PORT, format);
	}

	/**
	 * Create a new interface to interact with S3 with the given credential and
	 * connection parameters
	 * 
	 * @param awsAccessKeyId
	 *            Your user key into AWS
	 * @param awsSecretAccessKey
	 *            The secret string used to generate signatures for
	 *            authentication.
	 * @param isSecure
	 *            use SSL encryption
	 * @param server
	 *            Which host to connect to. Usually, this will be
	 *            s3.amazonaws.com
	 * @param port
	 *            Which port to use.
	 * @param callingFormat
	 *            Type of request Regular/Vanity or Pure Vanity domain
	 */
	public AWSAuthConnection(String awsAccessKeyId, String awsSecretAccessKey,
			boolean isSecure, String server, int port, CallingFormat format) {
		this.awsAccessKeyId = awsAccessKeyId;
		this.awsSecretAccessKey = awsSecretAccessKey;
		this.isSecure = isSecure;
		this.server = server;
		this.port = port;
		this.callingFormat = format;
	}

    /**
     * Writes an object to S3. 
     * 
     * @param bucket
     *            The name of the bucket to which the object will be added.
     * @param key
     *            The name of the key to use.
     * @param object
     *            An S3Object containing the data to write.
     * @param headers
     *            A Map of String to List of Strings representing the http
     *            headers to pass (can be null).
     * @param timeout
     *            The http connect & read timeout (0 means not set).
     */
    public Response put(String bucket, String key, S3Object object, Map headers, int timeout)
            throws MalformedURLException, IOException {
        HttpURLConnection request = makeRequest("PUT", bucket, Utils
                .urlencode(key), null, headers, object, timeout);

        if (request == null) {
            return null;
        }

        Response response = new Response();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            request.setDoOutput(true); // Configure to upload a request body
            request.setFixedLengthStreamingMode(object.contentLength); // For better performance

            byte[] buffer = new byte[8192];
            int c = -1;
            bis = new BufferedInputStream(object.getDataInputStream());
            bos = new BufferedOutputStream(request.getOutputStream());

            while ((c = bis.read(buffer)) >= 0) {
                bos.write(buffer, 0, c);
            }
            bos.flush();

            response.setResponseCode(request);
            // response.setMessage(request); // For debug only
        } finally   {
            if (bos != null)
                bos.close();
            if (request != null)
                request.disconnect();
            if (bis != null)
                bis.close();
            if (object != null)
                object.closeDataInputStream();
        }

        return response;
    }

	/**
	 * Make a new HttpURLConnection.
	 * 
	 * @param method
	 *            The HTTP method to use (GET, PUT, DELETE)
	 * @param bucketName
	 *            The bucket name this request affects
	 * @param key
	 *            The key this request is for
	 * @param pathArgs
	 *            parameters if any to be sent along this request
	 * @param headers
	 *            A Map of String to List of Strings representing the http
	 *            headers to pass (can be null).
	 * @param object
	 *            The S3Object that is to be written (can be null).
	 * @param timeout
	 *            The http connect & read timeout (0 means not set).
	 */
	private HttpURLConnection makeRequest(String method, String bucket,
			String key, Map pathArgs, Map headers, S3Object object, int timeout)
			throws MalformedURLException, IOException {
		CallingFormat callingFormat = Utils.getCallingFormatForBucket(
				this.callingFormat, bucket);
		if (isSecure && callingFormat != CallingFormat.getPathCallingFormat()
				&& bucket.indexOf(".") != -1) {
			System.err.println("You are making an SSL connection, however," +
					" the bucket contains periods and the wildcard certificate " +
					"will not match by default.  Please consider using HTTP.");
		}

		// build the domain based on the calling format
		URL url = callingFormat.getURL(isSecure, server, this.port, bucket,
				key, pathArgs);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		if (connection == null)
		    return null;
		
		connection.setRequestMethod(method);
		// [20150810][sy_wang] Set timeout to prevent from power consuming if server doesn't response
		connection.setConnectTimeout(timeout);
		connection.setReadTimeout(timeout);

		// subdomain-style urls may encounter http redirects.
		// Ensure that redirects are supported.
		if (!connection.getInstanceFollowRedirects()
				&& callingFormat.supportsLocatedBuckets())
			throw new RuntimeException("HTTP redirect support required.");

		addHeaders(connection, headers);
		if (object != null)
			addMetadataHeaders(connection, object.metadata);
		addAuthHeader(connection, method, bucket, key, pathArgs);

		return connection;
	}

	/**
	 * Add the given headers to the HttpURLConnection.
	 * 
	 * @param connection
	 *            The HttpURLConnection to which the headers will be added.
	 * @param headers
	 *            A Map of String to List of Strings representing the http
	 *            headers to pass (can be null).
	 */
	private void addHeaders(HttpURLConnection connection, Map headers) {
		addHeaders(connection, headers, "");
	}

	/**
	 * Add the given metadata fields to the HttpURLConnection.
	 * 
	 * @param connection
	 *            The HttpURLConnection to which the headers will be added.
	 * @param metadata
	 *            A Map of String to List of Strings representing the s3
	 *            metadata for this resource.
	 */
	private void addMetadataHeaders(HttpURLConnection connection, Map metadata) {
		addHeaders(connection, metadata, Utils.METADATA_PREFIX);
	}

	/**
	 * Add the given headers to the HttpURLConnection with a prefix before the
	 * keys.
	 * 
	 * @param connection
	 *            The HttpURLConnection to which the headers will be added.
	 * @param headers
	 *            A Map of String to List of Strings representing the http
	 *            headers to pass (can be null).
	 * @param prefix
	 *            The string to prepend to each key before adding it to the
	 *            connection.
	 */
	private void addHeaders(HttpURLConnection connection, Map headers,
			String prefix) {
		if (headers != null) {
			for (Iterator i = headers.keySet().iterator(); i.hasNext();) {
				String key = (String) i.next();
				for (Iterator j = ((List) headers.get(key)).iterator(); j
						.hasNext();) {
					String value = (String) j.next();
					connection.addRequestProperty(prefix + key, value);
				}
			}
		}
	}

	/**
	 * Add the appropriate Authorization header to the HttpURLConnection.
	 * 
	 * @param connection
	 *            The HttpURLConnection to which the header will be added.
	 * @param method
	 *            The HTTP method to use (GET, PUT, DELETE)
	 * @param bucket
	 *            the bucket name this request is for
	 * @param key
	 *            the key this request is for
	 * @param pathArgs
	 *            path arguments which are part of this request
	 */
	private void addAuthHeader(HttpURLConnection connection, String method,
			String bucket, String key, Map pathArgs) {
		if (connection.getRequestProperty("Date") == null) {
			connection.setRequestProperty("Date", httpDate());
		}
		if (connection.getRequestProperty("Content-Type") == null) {
			connection.setRequestProperty("Content-Type", "");
		}

		String canonicalString = Utils.makeCanonicalString(method, bucket, key,
				pathArgs, connection.getRequestProperties());
		String encodedCanonical = Utils.encode(this.awsSecretAccessKey,
				canonicalString, false);
		connection.setRequestProperty("Authorization", "AWS "
				+ this.awsAccessKeyId + ":" + encodedCanonical);
	}

	/**
	 * Generate an rfc822 date for use in the Date HTTP header.
	 */
	public static String httpDate() {
		final String DateFormat = "EEE, dd MMM yyyy HH:mm:ss ";
		SimpleDateFormat format = new SimpleDateFormat(DateFormat, Locale.US);
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		return format.format(new Date()) + "GMT";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AWSAuthConnection [awsAccessKeyId=");
		builder.append(awsAccessKeyId);
		builder.append(", awsSecretAccessKey=");
		builder.append(awsSecretAccessKey);
		builder.append(", callingFormat=");
		builder.append(callingFormat);
		builder.append(", isSecure=");
		builder.append(isSecure);
		builder.append(", port=");
		builder.append(port);
		builder.append(", server=");
		builder.append(server);
		builder.append("]");
		
		return builder.toString();
	}
}
