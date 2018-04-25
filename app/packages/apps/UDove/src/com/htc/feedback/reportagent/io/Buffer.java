package com.htc.feedback.reportagent.io;

import com.htc.feedback.reportagent.util.UReportException;

public class Buffer {
	protected byte [] mBuf;
	protected int mOffset;
	protected int mLength;
	
	public Buffer(byte[] _buf, int _offset, int _length) throws UReportException {
		if(_buf == null)
			throw new UReportException("_buf is null");
		else if(_offset < 0 || _offset >= _buf.length)
			throw new UReportException("_buf = "+_buf+", _offset = "+_offset+", length = "+_buf.length);
		else if(_length < 0 || _length > _buf.length)
			throw new UReportException("_buf = "+_buf+", _length = "+_length+", length = "+_buf.length);
		else if((_offset + _length) > _buf.length)
			throw new UReportException("[offset + length] _buf = "+_buf+", _length = "+_length+", length = "+_buf.length);
		
		mBuf = _buf;
		mOffset = _offset;
		mLength = _length;
	}
	
	public void release() {
		mBuf = null;
		mOffset = 0;
		mLength = 0;
	}
	
	public byte [] getBuffer() { return mBuf; }
	public int getOffset() { return mOffset; }
	public int getLength() { return mLength; }
}