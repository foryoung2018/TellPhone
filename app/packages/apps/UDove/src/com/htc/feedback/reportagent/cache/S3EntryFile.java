package com.htc.feedback.reportagent.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.htc.feedback.reportagent.util.UReportException;

public class S3EntryFile {
	private final File mFile;
	private final FileChangdListener mFileChangedListener;
	public S3EntryFile(File file, FileChangdListener fileChangedListener) throws UReportException {
		if(file == null)
			throw new UReportException("EntryFile can't accept a null pointer of file");
		mFile = file;
		mFileChangedListener = fileChangedListener;
	}
	
	/**
	 * 1. Remove the file from the cache queue <br/>
	 * 2. Delete the file if it exists.
	 */
	public void delete() {
		if(mFileChangedListener != null) 
			mFileChangedListener.onDelete(mFile);
		if(mFile.exists())
			mFile.delete();
	}
	
	public FileOutputStream getFileOutputStream() throws IOException {
		return new FileOutputStream(mFile);
	}
	
	public FileInputStream getFileInputStream() throws IOException {
		return new FileInputStream(mFile);
	}
	public long getFileSize() {
		return mFile.length();
	}

	public String getName() {
		return mFile.getName();
	}
	
	public String getAbsolutePath() {
		return mFile.getAbsolutePath();
	}
	
	public boolean exists() {
		boolean isExists = mFile.exists();
		if (!isExists)
			delete();
		return isExists;
	}
	
	/**
	 * Use other public function to operate this file.<br/>
	 * This function is only for giving the file pointer to S3Uploader to uploading this file. 
	 */
	public File getFilePointer() {
		return mFile;
	}
}
