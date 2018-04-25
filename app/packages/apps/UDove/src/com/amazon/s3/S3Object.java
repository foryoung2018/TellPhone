//  This software code is made available "AS IS" without warranties of any
//  kind.  You may copy, display, modify and redistribute the software
//  code either by itself or as incorporated into your code; provided that
//  you do not remove any proprietary notices.  Your use of this software
//  code is at your own risk and you waive any claim against Amazon
//  Digital Services, Inc. or its affiliates with respect to your use of
//  this software code. (c) 2006 Amazon Digital Services, Inc. or its
//  affiliates.

package com.amazon.s3;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * A representation of a single object stored in S3.
 */
public class S3Object {
	public static final String METADATA_HEADER_CONTENT_MD5 = "Content-MD5";
    public static final String METADATA_HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String METADATA_HEADER_CONTENT_TYPE = "Content-Type";
    
    public String contentMD5 = null;
    public long contentLength; // Used in HttpURLConnection.setFixedLengthStreamingMode() of AWSAuthConnection
	//Declare the variables for different data uploaded use(NEW)
    public boolean isByteArray=false;
    public boolean isFile=false;
    public String file_ab_location="";
    public byte[] data2=null;
    //End of New vaiables declare
    private transient InputStream dataInputStream = null;
    
    /**
     * Store references to files when the object's data comes from a file, to allow for lazy
     * opening of the file's input streams.
     */
    private File dataInputFile = null;

    /**
     * A Map from String to List of Strings representing the object's metadata
     */
    public Map metadata;

    public S3Object(byte[] data, Map metadata) {	
		//NEW : set the byte array data for HttpEntityy use
    	isByteArray=true;
    	this.data2=data;
		//End of setting
    	this.dataInputStream = new ByteArrayInputStream(data);
       this.metadata = metadata;
		 //New: set the content length
        this.contentLength = Utils.getContentLength(this.dataInputStream);
    }
    
    /**
     * Create an object representing a file. The object is initialised with the file's name
     * as its key, the file's content as its data, a content type based on the file's extension
     * (see {@link Mimetypes}), and a content length matching the file's size.
     * The file's MD5 hash value is also calculated and provided to S3, so the service
     * can verify that no data are corrupted in transit.
     * <p>
     * <b>NOTE:</b> The automatic calculation of a file's MD5 hash digest as performed by
     * this constructor could take some time for large files, or for many small ones.
     *
     * @param file
     * the file the object will represent. This file must exist and be readable.
     *
     * @throws IOException when an i/o error occurred reading the file
     * @throws NoSuchAlgorithmException when this JRE doesn't support the MD5 hash algorithm
     */
    public S3Object(File file, Map metadata) throws IOException {
		this.metadata = metadata;
		//New : declare the S3 Object is a file    	
		isFile= true;
    	this.dataInputFile=file;
		//End of new declare
        setContentLength(file.length());
        
        if (!file.exists()) {
            throw new FileNotFoundException("Cannot read from file: " + file.getAbsolutePath());
        }
			//New: For upload a whole file, needs the absolute file path
        file_ab_location=file.getAbsolutePath();
        setDataInputFile(file);
        
        FileInputStream fis = null;
        try    {
            fis = new FileInputStream(file);
            setMd5Hash(Utils.computeMD5Hash(fis));
        } finally    {
        	if(fis != null)    fis.close();
        }
    }
    
    /**
	 * @param computeMD5Hash
	 */
	private void setMd5Hash(String md5) {
		this.contentMD5 = md5;
		
	}

	/**
     * Returns an input stream containing this object's data, or null if there is
     * no data associated with the object.
     * <p>
     * If you are downloading data from S3, you should consider verifying the
     * integrity of the data you read from this stream using one of the
     * {@link #verifyData(InputStream)} methods.
     *
     * @throws S3ServiceException
     */
    public InputStream getDataInputStream() throws IOException {
        if(dataInputStream == null && dataInputFile != null) {
            try {
                dataInputStream = new FileInputStream(dataInputFile);
            } catch (FileNotFoundException e) {
                throw e;
            }
        }
        
        return dataInputStream;
    }
    
    /**
     * Sets an input stream containing the data content to associate with this object.
     * <p>
     * <b>Note</b>: If the data content comes from a file, use the alternate method
     * {@link #setDataInputFile(File)} which allows objects to lazily open files and avoid any
     * Operating System limits on the number of files that may be opened simultaneously.
     * <p>
     * <b>Note 2</b>: This method does not calculate an MD5 hash of the input data,
     * which means S3 will not be able to recognize if data are corrupted in transit.
     * To allow S3 to verify data you upload, you should set the MD5 hash value of
     * your data using {@link #setMd5Hash(byte[])}.
     * <p>
     * This method will set the object's file data reference to null.
     *
     * @param dataInputStream
     * an input stream containing the object's data.
     */
    public void setDataInputStream(InputStream dataInputStream) {
        this.dataInputFile = null;
    	this.dataInputStream = dataInputStream;
    }

    /**
     * Sets the file containing the data content to associate with this object. This file will
     * be automatically opened as an input stream only when absolutely necessary, that is when
     * {@link #getDataInputStream()} is called.
     * <p>
     * <b>Note 2</b>: This method does not calculate an MD5 hash of the input data,
     * which means S3 will not be able to recognize if data are corrupted in transit.
     * To allow S3 to verify data you upload, you should set the MD5 hash value of
     * your data using {@link #setMd5Hash(byte[])}.
     * <p>
     * This method will set the object's input stream data reference to null.
     *
     * @param dataInputFile
     * a file containing the object's data.
     */
    public void setDataInputFile(File dataInputFile) {
        this.dataInputStream = null;
        this.dataInputFile = dataInputFile;
    }
    
    /**
     * @return
     * Return the file that contains this object's data, if such a file has been
     * provided. Null otherwise.
     */
    public File getDataInputFile() {
        return this.dataInputFile;
    }


    /**
     * Closes the object's data input stream if it exists.
     *
     * @throws IOException
     */
    public void closeDataInputStream() throws IOException {
        if (this.dataInputStream != null) {
            this.dataInputStream.close();
            this.dataInputStream = null;
        }
    }
    
     public void setContentLength(long size) {
         //addMetadata(METADATA_HEADER_CONTENT_LENGTH, String.valueOf(size));
    	 this.contentLength = size;
    }

}
