package com.htc.feedback.reportagent.pomelo;

import java.io.IOException;
import java.util.Properties;
import com.htc.xps.pomelo.log.HandsetLogPKT;

interface UploadHelper {
	abstract void putReport(byte [] data, Properties prop) throws IOException;
	public boolean putReport(String tag, HandsetLogPKT envelope);
}
