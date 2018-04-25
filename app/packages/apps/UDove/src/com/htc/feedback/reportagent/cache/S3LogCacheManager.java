package com.htc.feedback.reportagent.cache;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import android.content.Context;

import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.EngineeringPreference;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.util.UReportException;

public class S3LogCacheManager{
	
	final private static String TAG = "S3LogCacheManager"; 
	private ArrayList<File> mFiles;
	S3FileChangedListener mListener;
	final private static int MAXLOG_COUNT = 56; // Keep cache count for 7-day UP and UB, 2013/3/28
	
	// == singleton ==
	private static S3LogCacheManager sS3LogCacheManager; 
	public static S3LogCacheManager getInstance() {
		if(sS3LogCacheManager == null) {
			sS3LogCacheManager = new S3LogCacheManager();
		}
		return sS3LogCacheManager;
	}	
	private S3LogCacheManager() {
		mListener = new S3FileChangedListener();
	}
	
	// == member functions ==
	private void init(Context ctx) {		
		if(mFiles == null) {
			renameOldCachedFile(ctx);
			File [] files = getFilesFromFS(ctx);
			mFiles = new ArrayList<File>();
			if(files!=null)
				Collections.addAll(mFiles, files);
			Collections.sort(mFiles);
			if (mFiles.size() != 0 && Common.DEBUG) {
				for (File file : mFiles)
					Log.d(TAG, "[init] : " + file.getName());
			}
		}
	}
	
	private File [] getFilesFromFS(Context ctx) {
		File logFolder = ctx.getFilesDir();
		if(logFolder != null && logFolder.isDirectory())		
			return logFolder.listFiles();
		return new File[0];
	}
	
	private void trimToFit() {
		while (mFiles.size() >= MAXLOG_COUNT) {
			boolean allHighPriority = true;
			for(File file: mFiles) {
				if (!LogCacheUtil.isHighPriorityLogType(file)) {
					Log.d(TAG, "[trimToFit] " + file.getName());
					mListener.onDelete(file);
					file.delete();
					allHighPriority = false;
					break;
				}
			}
			if (allHighPriority) {
				File file = mFiles.get(0);
				Log.d(TAG, "[trimToFit] " + file.getName() + " (all logs are high priority)");
				mListener.onDelete(file);
				file.delete();
			}
		}
	}
	
	private File writeFileToFS(Context ctx, Properties prop) {
		File logFile = ctx.getFileStreamPath(LogCacheUtil.generateS3LogFileName(prop));
		mListener.onAdd(logFile);
		if (Common.DEBUG) {  //Only print log when debug flag is true.
			StringBuilder sb = new StringBuilder();
			int i = 1;
			for (File file: mFiles){
				if (sb.length() != 0)
					sb.append(", ");
				sb.append(i).append(".").append(file.getName());
				i++;
			}
			Log.d(TAG, "============= Cached file queue =============");
			Log.d(TAG, sb.toString());
		}
		return logFile;
	}
	
	/**
	 * Write log into file system, and put file into cache queue.
	 * @param ctx context
	 * @param tag dropbox tag
	 * @return the wrapper class instance of the target log file
	 * @throws Exception
	 */
	public S3EntryFile putS3LogToCache(Context ctx, Properties prop) throws Exception{
		init(ctx);
		trimToFit();
		return new S3EntryFile(writeFileToFS(ctx, prop), mListener);
	}
	
	/**
	 * get files from cache
	 */
	public S3EntryFile [] getFiles(Context ctx) {
		init(ctx);
		S3EntryFile [] entryFiles = new S3EntryFile [mFiles.size()];
		for(int i=0; i<mFiles.size(); i++) {
			try {
				entryFiles[i] = new S3EntryFile(mFiles.get(i), mListener);
			} catch (UReportException e) {
				e.printStackTrace();
			}
		}
		return entryFiles;
	}
	
	/**
	 * For backward compatible, if there are cached files whose name is start with "report-",
	 * then we change its name to 'currentTimeMillis@DropboxTag.bin'.
	 * @param ctx
	 */
	private void renameOldCachedFile(Context ctx) {
		if (!EngineeringPreference.getS3SentinelValue(ctx)) {
			File logFolder = ctx.getFilesDir();
			DataInputStream in = null;
			if(logFolder != null && logFolder.isDirectory()){
				File[] files = logFolder.listFiles();
				//[CQG #25413240] REX, 2013/06/24
				if (files != null && files.length != 0) {
					for (File file : files) {
						if (file.getName().startsWith("report-")) {
							try {
								in = new DataInputStream(new FileInputStream(file));
								String tag = in.readUTF();
								if (tag != null) {
									String newName = LogCacheUtil.generateS3LogFileName(tag);
									Log.d(TAG, "[renameOldCachedFile] From " + file.getName() + " to " + newName);
									file.renameTo(new File(logFolder.getAbsolutePath() + "/" + newName));
								} else {
									Log.d(TAG, "[renameOldCachedFile] tag is null, delete the file directly.");
									file.delete();  //if tag is null, then delete the file.
								}
							} catch (IOException e) {
								e.printStackTrace();
							} finally {
								if(in != null)
									try { in.close(); } catch (IOException e) {	e.printStackTrace();}
							}
						}
					}
				}
			}
			EngineeringPreference.setS3SentinelValue(ctx, true);
		}
	}
	
	private class S3FileChangedListener implements FileChangdListener {
		/**
		 * only maintain mFiles
		 */
		public void onAdd(File file) {
			if(file != null) {
				mFiles.add(file);
				Collections.sort(mFiles);
				Log.d(TAG, "[onAdd] file " + file.getAbsolutePath() + " is added to the list !");
			}
		}
		
		/**
		 * only maintain mFiles 
		 */
		public void onDelete(File file) {
			if(file != null) {
				if (!file.exists())
					Log.d(TAG,"WARNING!!, file " + file.getAbsolutePath() + " doesn't exist.");
				
				File targetFileInList = null;
				for(File f : mFiles){
					if(f != null && f.compareTo(file) == 0){
						targetFileInList = f;
						break;
					}
				}
				
				if(targetFileInList != null) {
					mFiles.remove(targetFileInList);
					Log.d(TAG, "[onDelete] file " + file.getAbsolutePath() + " is removed from the list !");
				}
			}
		}
	}
}
