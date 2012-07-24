package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.util.concurrent.BlockingQueue;

public class UploadThread extends Thread {
	public UploadThread(LogService logService,
			File dataDirectory, BlockingQueue<File> compressedLogFiles) {
	}
}
