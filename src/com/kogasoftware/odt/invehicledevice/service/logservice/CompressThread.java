package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.util.concurrent.BlockingQueue;

public class CompressThread extends Thread {
	public CompressThread(LogService logService,
			File dataDirectory, BlockingQueue<File> rawLogFiles,
			BlockingQueue<File> compressedLogFiles) {
	}
}
