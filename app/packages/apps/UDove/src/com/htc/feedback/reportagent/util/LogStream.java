package com.htc.feedback.reportagent.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.util.zip.ZipEntry;
//import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.os.Bundle;
import android.os.IBinder;
//import android.os.ServiceManager;
import android.os.RemoteException;
import javax.crypto.spec.IvParameterSpec;
import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.Log;

/**
 *   Log Stream has 16 bytes signature at the beginning of log file. The following two bytes are separately represented 
 * version and isEncrypted. Two bytes after isEncrypted are reserved and filled with zero in version 1.
 * concatenateOutputStream(), isLogStream(), and concatenateInputStream() are the key functions for users to output / input 
 * the specific log stream.
 *   The current Log Stream is encrypted.
 * @author pittwu
 *
 */

/**
 * {@exthide}
 */
public final class LogStream {
	
	private LogStream() {}
	
	public static interface SeedGetter {
		byte[] get();
	}
	
	private static final String TAG = "LogStream";
	private static final int VERSION = 3;
	private static final String ZIP_FILE_ENTITY = "file_entity";
    private static final int SIG0 = 0x6716e3aa, SIG1 = 0x11d74057,
    						SIG2 = 0x82a6b677, SIG3 = 0xc3b907ca;
    private static final int ENTITY_BUFFER_SIZE = 4096; // bytes
    private static final int HEAD_BUFFER_SIZE = 20; // bytes;
	
	/**
	 * This function is CipherOutputStream related. OutputStream, out, will be written signature( 16 bytes),
	 * version( 1 byte) and isEncrypted( 1 byte).
	 * @param out
	 * @return Log OutputStream which concatenates OutputStream, out. 
	 */
	public static OutputStream concatenateOutputStream(OutputStream out, boolean doEncryption, byte [] aesKey, byte[] iv) throws GeneralSecurityException, IOException {
		if(out == null)
			return null;
		
		OutputStream lastOS = null;
		BufferedOutputStream bos = new BufferedOutputStream(out, HEAD_BUFFER_SIZE);
		DataOutputStream headDos = new DataOutputStream(bos);
		writeLogStreamSignature(headDos);	// log stream signature
		headDos.write(VERSION);		// version
		headDos.writeBoolean(true);	// isEncrypted
		headDos.write(0); 			// reserved
		headDos.write(0); 			// reserved
		headDos.flush();
		headDos = null;
		
		bos = new BufferedOutputStream(out, ENTITY_BUFFER_SIZE);
		
		// TODO: Although there is no plain text case now, it probably will be enabled for error report.
		OutputStream cos = null;	// CipherOutputStream;
		if(doEncryption)
			cos = CipherStream.getCipherOutputStream(bos, aesKey, VERSION, iv);
		else
			cos = bos;
		
    	if(cos != null) {
			ZipOutputStream zos = new ZipOutputStream(cos);
			if(zos != null) { // sy, Fix CQG issue, 20130429
				ZipEntry zentry = new ZipEntry(ZIP_FILE_ENTITY);
				zos.putNextEntry(zentry);
				lastOS = zos;
			}
		}
 
		return lastOS;
    }
	
	/**
	 * This function is CipherIutputStream related. InputStream, in, will be read signature( 16 bytes),
	 * version( 1 byte) and isEncrypted( 1 byte).
	 * @param in
	 * @return Log InputStream which concatenates InputStream, in.
	 */
	public static InputStream concatenateInputStream(InputStream in, byte[] aesKey, byte[] iv) throws GeneralSecurityException, IOException {
		if(in == null)
			return null;
		
		InputStream lastIS = null;
		// retrieve head information
		BufferedInputStream bis = new BufferedInputStream(in, HEAD_BUFFER_SIZE);
		DataInputStream headDis = new DataInputStream(bis);
		if(isLogStreamInternal(headDis)) {
			int version = headDis.read();						// version
			boolean isEncrypted = headDis.readBoolean();		// encryption flag
			int reserved1 = headDis.read();						// reserved
			int reserved2 = headDis.read();						// reserved
			if(Common.DEBUG) 
				Log.d(TAG, "version: "+version+", isEncrypted: "+isEncrypted+", reserved1: "+reserved1+", reserved2: "+reserved2);
			
			headDis = null;
			
			// TODO: Although there is no plain text case now, it probably will be enabled for error report.
			if(version == 3) {
				bis = new BufferedInputStream(in, ENTITY_BUFFER_SIZE);
				InputStream cis = null;	// CipherInputStream
				if(isEncrypted)
					if(aesKey != null)
						cis = CipherStream.getCipherInputStream(bis, aesKey, version, iv);
					else
						Log.d(TAG,"[concatenateInputStream] aesKey is null");
				else
					cis = bis;
				
				if(cis != null) {
					SafeZipInputStream zis = new SafeZipInputStream(cis);
					ZipEntry zEntry = null;
					if(zis != null) { // sy, Fix CQG issue, 20130429
						while((zEntry = zis.getNextEntry()) != null) {
							if(ZIP_FILE_ENTITY.equals(zEntry.getName())) {
								lastIS = zis;
								break;
							}
						}
					}
				}
				if(lastIS == null)
					Log.d(TAG,"InputStream is null ???");
			}
			else
				Log.d(TAG,"wrong file version: "+version+", isEncrypted:"+isEncrypted);
		}
		
		return lastIS;
	}
	
	/**
	 * Check the Log Stream format with the signature in the head. InputStream is will be read 16 bytes after return.
	 * @param is
	 * @return true if InputStream, is, is a Log Stream. false if isn't.
	 * @throws IOException
	 */
	public static boolean isLogStream(InputStream is) throws IOException{
		if(is != null) {
			BufferedInputStream bis = new BufferedInputStream(is, HEAD_BUFFER_SIZE);
			DataInputStream dis = new DataInputStream(bis);
			return isLogStreamInternal(dis);
		}
		return false;
	}
	
	private static void writeLogStreamSignature(DataOutputStream out) throws IOException {
		out.writeInt(SIG0);
		out.writeInt(SIG1);
		out.writeInt(SIG2);
		out.writeInt(SIG3);
	}
	
	private static boolean isLogStreamInternal(DataInputStream dis) throws IOException{
		int sig0=0, sig1=0, sig2=0, sig3=0;
		if(dis != null) {
			sig0 = dis.readInt();
			sig1 = dis.readInt();
			sig2 = dis.readInt();
			sig3 = dis.readInt();
		}
		
		if(sig0 == SIG0 && sig1 == SIG1 && sig2 == SIG2 && sig3 == SIG3)
			return true;
		else
			return false;
	}
	
	/**
	 * Utilities of CipherInputStream and CipherOutputStream for Log Stream
	 * @author pittwu
	 *
	 */
	private static final class CipherStream {
		public static CipherOutputStream getCipherOutputStream(OutputStream os, byte [] aesKey, int versionCode, byte[] iv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException,InvalidAlgorithmParameterException {
			SecretKeySpec aeskeySpec = new SecretKeySpec(aesKey, "AES");
			Cipher aesCipher = generateCipher(aeskeySpec, Cipher.ENCRYPT_MODE, versionCode, iv);
			if (aesCipher != null)
				return new CipherOutputStream(os, aesCipher);
			return null;
		}
		

		public static CipherInputStream getCipherInputStream(InputStream is, byte [] aesKey, int versionCode, byte[] iv) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException {
			SecretKeySpec aeskeySpec = new SecretKeySpec(aesKey, "AES");
			Cipher aesCipher = generateCipher(aeskeySpec, Cipher.DECRYPT_MODE, versionCode, iv);
			if (aesCipher != null)
				return new CipherInputStream(is, aesCipher);
			return null;
		}
		
	    private static Cipher generateCipher(SecretKeySpec aeskeySpec, int mode, int versionCode, byte[] iv) throws NoSuchAlgorithmException, NoSuchPaddingException,InvalidAlgorithmParameterException ,InvalidKeyException, NoSuchProviderException {
	    	Cipher aesCipher = null;
	    	if (versionCode == 3) {
	    		aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
	    		IvParameterSpec ivSpec = new IvParameterSpec(iv);
				aesCipher.init(mode, aeskeySpec, ivSpec);
				if(Common.SECURITY_DEBUG) Log.d(TAG,"[generateCipher] Version: " + versionCode + ", use CBC");
	    	}
	    	return aesCipher;
	    }
	}
}

