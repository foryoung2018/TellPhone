package com.htc.feedback.reportagent.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.htc.feedback.reportagent.util.UReportException;

public class PositionRetrievingInputStream extends FilterInputStream {

	protected int mPos;
	
	public PositionRetrievingInputStream(InputStream in) {
		super(in);
	}
	
    public int read() throws IOException {
    	int res = super.read();
    	if(res > 0)
    		mPos += res;
        return res;
    }
    
    public int read(byte[] buffer, int offset, int count) throws IOException {
        int res = super.read(buffer, offset, count);
        if(res > 0)
        	mPos += res;
        return res;
    }
    
	public int getPosition() {
		return mPos;
	}
}
