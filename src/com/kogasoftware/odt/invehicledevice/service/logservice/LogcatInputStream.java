package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.FilterInputStream;
import java.io.IOException;

public class LogcatInputStream extends FilterInputStream {
	private final Process process;
	
	public LogcatInputStream() throws IOException {
		this(Runtime.getRuntime().exec("logcat -v threadtime"));
	}

	private LogcatInputStream(Process process) {
		super(process.getInputStream());
		this.process = process;
	}

	public void close() throws IOException {
		try {
			process.destroy();
		} finally {
			super.close();
		}
	}
}
