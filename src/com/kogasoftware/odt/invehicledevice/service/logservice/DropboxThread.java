package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.util.concurrent.BlockingQueue;

public class DropboxThread extends Thread {
	public DropboxThread(LogService logService, File dataDirectory,
			BlockingQueue<File> rawLogFiles) {
	}
}
