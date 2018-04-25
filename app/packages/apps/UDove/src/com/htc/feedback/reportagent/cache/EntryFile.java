package com.htc.feedback.reportagent.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import android.content.Context;
import android.text.TextUtils;
import com.htc.feedback.ErrorReportPreference;
import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.util.LogStream;
import com.htc.feedback.reportagent.util.UReportException;
import com.htc.feedback.reportagent.util.SafeZipInputStream;

public class EntryFile {
	private final File mFile;
	private final FileChangdListener mFileChangedListener;
	private String mTagName;
	private String mLogType;
	public EntryFile(File file, FileChangdListener fileChangedListener) throws UReportException {
		if(file == null)
			throw new UReportException("EntryFile can't accept a null pointer of file");
		mFile = file;
		mFileChangedListener = fileChangedListener;
	}
	
	public void delete() {
		if(mFile.exists()) {
			if(mFileChangedListener != null) {
				mFileChangedListener.onDelete(mFile);
			}
			mFile.delete();
		}
	}

	public String getName() {
		return mFile.getName();
	}
	
	// unzip
	/**
	 * @deprecated
	 */
	public InputStream getFileInputStream() throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(mFile);
		BufferedInputStream bis = new BufferedInputStream(fis, 1024);
		SafeZipInputStream zis = new SafeZipInputStream(bis);
		ZipEntry zEntry = null;
		try {
			while((zEntry = zis.getNextEntry()) != null) {
				if(Common.ZIP_FILE_ENTITY.equals(zEntry.getName()))
						return zis;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (fis != null)
				fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// zip
	/**
	 * @deprecated
	 */
	public static File writeNewFile(Context ctx, byte [] logBuf, String tag) {
		File logFolder = ctx.getDir(Common.RELATIVE_LOG_FOLDER_PATH, Context.MODE_PRIVATE);
		String filePath = logFolder.getAbsolutePath() + "/" + LogCacheUtil.generateFileName(tag);
		Log.d("store a new file: " + filePath+" , TAG="+tag);
		
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ZipOutputStream zos = null;
		try {
			fos = new FileOutputStream(filePath);
			bos = new BufferedOutputStream(fos, 1024);
			zos = new ZipOutputStream(bos);
			ZipEntry zentry = new ZipEntry(Common.ZIP_FILE_ENTITY);
			zos.putNextEntry(zentry);
			zos.write(logBuf);
			zos.closeEntry();
			zos.finish();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(bos != null)
					bos.close();
				if(fos != null)
					fos.close();
				if(zos != null)
					zos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return new File(filePath);
	}
	
	// unzip
	//modified by Ricky,2012.09.27
	public InputStream getFileInputStreamEx(Context context) throws IOException,GeneralSecurityException {
		FileInputStream fis = null;
		boolean isLogStream = false;
		try {
			fis = new FileInputStream(mFile);
			isLogStream = LogStream.isLogStream(fis);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(isLogStream) {
			fis = new FileInputStream(mFile);
			return LogStream.concatenateInputStream(fis, ErrorReportPreference.getSecretKey(context), ErrorReportPreference.getIV(context));
		}
		else{
			return getFileInputStream();
		}
			
	}
	
	// zip
	public static File writeNewFileEx(Context ctx, byte [] logBuf, String tag) {
		boolean ret = false;
		
		File logFolder = ctx.getDir(Common.RELATIVE_LOG_FOLDER_PATH, Context.MODE_PRIVATE);
		String filePath = logFolder.getAbsolutePath() + "/" + LogCacheUtil.generateFileName(tag);
		Log.d("store a new file: " + filePath +" , TAG="+tag);
		
		FileOutputStream fos = null;
		OutputStream os = null;
		try {
			fos = new FileOutputStream(filePath);
			os = LogStream.concatenateOutputStream(fos, true, ErrorReportPreference.getSecretKey(ctx), ErrorReportPreference.getIV(ctx));
			if(os != null) {
				os.write(logBuf);
				ret = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} finally {
			try {//[REX]: Must close os first, 2012/12/04
				if (os != null)
					os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				// [CQG #88]modified by Ricky,2012.0925
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(ret)
			return new File(filePath);
		else
			return null;
	}
	//parse tag from fileName , added by Ricky,2012.08.24
	public String getTagName() {
	    if (mTagName == null || mTagName.length() == 0)
	        parseFileName();
	    return mTagName;
	}
	
	public String getLogType() {
	    if (mLogType == null || mLogType.length() == 0)
            parseFileName();
        return mLogType;
	}
	
	private void parseFileName() {
	    String fileName = getName();
	    if (!TextUtils.isEmpty(fileName)) {
            String[] array = fileName.split("-");
            if (array != null && array.length >= 3) {
                if (!TextUtils.isEmpty(array[1]))
                    mTagName = array[1];
                if (!TextUtils.isEmpty(array[2]))
                    mLogType = array[2];
            }
        }
	    if (Common.DEBUG)
	        Log.d("EntryFile", "[parseFileName] tag name: " + mTagName + ", log type: " + mLogType);
	}
	
	
	//
}
