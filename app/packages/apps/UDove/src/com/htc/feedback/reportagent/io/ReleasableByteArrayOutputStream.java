package com.htc.feedback.reportagent.io;

import java.io.ByteArrayOutputStream;

import com.htc.feedback.reportagent.util.UReportException;

public class ReleasableByteArrayOutputStream extends ByteArrayOutputStream {
	
	public ReleasableByteArrayOutputStream() {
		super();
	}
	
	public ReleasableByteArrayOutputStream(int size) {
		super(size);
	}
	
	public Buffer getInnerBuffer() {
		Buffer ret = null;
		try {
			ret = new Buffer(buf, 0, count);
		} catch (UReportException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public void release() {
		buf = null;
	}
}
