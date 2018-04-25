package com.htc.feedback.reportagent.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import android.content.Context;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.DropBoxManager;
import android.text.TextUtils;

import com.htc.feedback.ErrorReportPreference;
import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.Log;
import com.htc.feedback.reportagent.util.LogStream;

// ===========================================================
// == TODO: The followings are duplicate codes, they needs  ==
// == to be integrated between pomelo and S3 server         ==
// ===========================================================    
// begin-LogEntry
/**
 * This purpose of the LogEntry is to unify the access method between an File and DropBoxManager.Entry
 */
public class LogEntry {
	
	private static enum Type {
		TYPE_UNKNOWN,
		TYPE_DROPBOX,
		TYPE_FILE_PATH,
		TYPE_LOCALSOCKET,
		TYPE_LASTKMSG
	}
	
	private interface LogDelegate {
		public Type getType();
		public InputStream getInputStream() throws IOException;
		public void closeEntry();
	}
	
	private static class FileDelegate implements LogDelegate {
		private Type mType;
		private String mFilePath;
		private Context mContext;
		public FileDelegate(String filePath, Context ctx) {
			if(TextUtils.isEmpty(filePath))
				throw new IllegalArgumentException("(LogEntry) File Path is empty");			
			mType = Type.TYPE_FILE_PATH;
			mFilePath = filePath;
			mContext = ctx;
		}
		public Type getType() {
			return mType;
		}
		//For CQG issue,modified by Ricky,2012.09.27
		public InputStream getInputStream() throws IOException{
			FileInputStream fis = null;
			boolean isLogStream = false;
			try {
				fis = new FileInputStream(mFilePath);
				isLogStream = LogStream.isLogStream(fis);
			} catch (Exception e) {
			    Log.d(TAG, mFilePath+"isn't log stream");
			} finally {
				try {
					if (fis != null)
						fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(isLogStream) {
				fis = new FileInputStream(mFilePath);
				InputStream is = null;
				try {
					is = LogStream.concatenateInputStream(fis, ErrorReportPreference.getSecretKey(mContext), ErrorReportPreference.getIV(mContext));
				} catch (GeneralSecurityException e) {
					e.printStackTrace();
					// [CQG # 208]fixed by Ricky,2012.10.02
					try {
						if (fis != null) {
							fis.close();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				}
				return is;
			}
				
			return new FileInputStream(mFilePath);
		}
		public void closeEntry() {}
	}
	
	private static class DropBoxFileDelegate implements LogDelegate {
		private Type mType = Type.TYPE_UNKNOWN;
		private long mEntryTime;	// DropBoxManager.Entry's time
		private String mEntryTag;	// DropBoxManager.Entry's tag
		private DropBoxManager.Entry mEntry; // DropBoxManager.Entry
		private Context mCtx;
		private byte[] mKey;
		private byte[] mIv;
		public DropBoxFileDelegate(String tag, long time, Context ctx, byte[] key, byte[] iv) {
			if(tag == null)
				throw new IllegalArgumentException("(LogEntry) the tag of entry is null");
			if(time < 0)
				throw new IllegalArgumentException("(LogEntry) the time of entry is less than zero");
			if(ctx == null)
				throw new IllegalArgumentException("(LogEntry) the ctx is null");
			mType = Type.TYPE_DROPBOX;
			mEntryTime = time;
			mEntryTag = tag;
			mCtx = ctx;
			mKey = key;
			mIv = iv;
		}
		public Type getType() {
			return mType;
		}

		public InputStream getInputStream() throws IOException{
			boolean isLogStream = false;
			DropBoxManager.Entry entry = null;
			DropBoxManager manager= (DropBoxManager) mCtx.getSystemService(Context.DROPBOX_SERVICE);
			entry = manager.getNextEntry(mEntryTag, mEntryTime);					

			if(entry != null) {
				InputStream typeVerifiedIS = entry.getInputStream();
				if(typeVerifiedIS != null) {
					isLogStream = LogStream.isLogStream(typeVerifiedIS);
					typeVerifiedIS.close();
					typeVerifiedIS = null;
				}
				entry.close();
				entry = null;
			}
			
			InputStream tmpIS = null;
			InputStream is = null;
			if(mEntry != null)
				mEntry.close();
			mEntry = manager.getNextEntry(mEntryTag, mEntryTime);
			if(mEntry != null) {
				tmpIS = mEntry.getInputStream();
				if(isLogStream) {
					try {
						is = LogStream.concatenateInputStream(tmpIS, mKey, mIv);
					} catch (GeneralSecurityException e) { e.printStackTrace(); }
				}
				else {
					is = tmpIS;
				}
			}
			return is;
		}
		public void closeEntry() {
			if(mEntry != null)
				mEntry.close();
		}
	}

    private static class LocalSocketDelegate implements LogDelegate {
        private Type mType;
        private String mSocketName;
        LocalSocket mLocalSocket;
        public LocalSocketDelegate(String socketName) {
            if(TextUtils.isEmpty(socketName))
                throw new IllegalArgumentException("(LogEntry) socket name is empty");            
            mType = Type.TYPE_LOCALSOCKET;
            mSocketName = socketName;
        }
        
        public Type getType() {
            return mType;
        }

        /**
         * The implementation leverages the implementation of HtcErrorReportManager.writeKernelLog()
         */
        public InputStream getInputStream() throws IOException{
            if(mLocalSocket != null)
                mLocalSocket.close();
            mLocalSocket = new LocalSocket();
            
            for(int i=0 ; i<5 ;i++){
                mLocalSocket.connect(new LocalSocketAddress(mSocketName,LocalSocketAddress.Namespace.RESERVED)); 
                if(mLocalSocket.isConnected())
                    break;
                Log.d(TAG, "Connect socket fail, wait 0.5 second ...");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            if(!mLocalSocket.isConnected()){
                Log.d(TAG, "Cannot connect to socket!");
                return null;
            }
            
            return mLocalSocket.getInputStream();
        }
        
        public void closeEntry() {
            if(mLocalSocket != null) {
                try {
                    mLocalSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Fail to close local socket", e);
                }
            }
        }
        
    }
    
    /**
     * This is a decorator of FileDelegate or LocalSocketDelegate for last_kmsg file. 
     * The main idea is : if the file can't be read, use LocalSocket instead.
     */
    private static class LastkmsgDelegate implements LogDelegate {
        private String mFilePath;
        private LogDelegate mDecoratedLogEntry;
        private Context mContext;
        public LastkmsgDelegate(String filePath, Context ctx) {
            if(TextUtils.isEmpty(filePath))
                throw new IllegalArgumentException("(LastkmsgDelegate) File Path is empty");            
            mFilePath = filePath;
            mContext = ctx;
            
            if(checkIfReadable(mFilePath))
                mDecoratedLogEntry = new FileDelegate(mFilePath, mContext);
            else
                mDecoratedLogEntry = new LocalSocketDelegate("htc_dlk");
                
        }
        
        public Type getType() {
            if(mDecoratedLogEntry != null)
                return mDecoratedLogEntry.getType();
            else
                return Type.TYPE_UNKNOWN;
        }
        
        public InputStream getInputStream() throws IOException {
            if(mDecoratedLogEntry != null)
                return mDecoratedLogEntry.getInputStream();
            else
                return null;
        }
        
        public void closeEntry() {
            if(mDecoratedLogEntry != null)
                mDecoratedLogEntry.closeEntry();
        }
        
        private static boolean checkIfReadable(String filePath) {
            int firstByte = -1;
            if(!TextUtils.isEmpty(filePath)) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(filePath);
                    firstByte = fis.read();
                } catch (IOException e) {
                    Log.d(TAG, "Fail to read file "+filePath);
                } finally {
                    if(fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) { Log.d(TAG, "Fail to close file "+filePath); }
                    }
                }
            }
            return firstByte != -1;
        }
    }
    
    private static final String TAG = "LogEntry";
	private LogDelegate mLogDelegate;
	
	public LogEntry(String filePath, Context ctx) {
	    // From K44, reading /proc/last_kmsg causes exception "libcore.io.ErrnoException: read failed: EPERM (Operation not permitted)".
	    // The solution leverages HtcResetNotify's http://git.htc.com:8081/#/c/292242/2 which reads last_kmsg from socket.
	    if("/proc/last_kmsg".equals(filePath))
	        mLogDelegate = new LastkmsgDelegate(filePath, ctx);
	    else
	        mLogDelegate = new FileDelegate(filePath, ctx);
	}
	
	public LogEntry(String tag, long time, Context ctx, byte[] key, byte[] iv) {
		mLogDelegate = new DropBoxFileDelegate(tag, time, ctx, key, iv);
	}
	
	public InputStream getInputStream() throws IOException {
		if(mLogDelegate != null)
			return mLogDelegate.getInputStream();
		else 
			return null;
	}
	
	/**
	 * Close DropBoxManager.Entry only. For InputStream and file path, it should be closed by caller 
	 * if it has call getInputStream() before.
	 */
	public void closeEntry() {
		if(mLogDelegate != null)
			mLogDelegate.closeEntry();
	}
}
// end-LogEntry