package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Charsets;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.service.logservice.LogcatThread;
import com.kogasoftware.odt.invehicledevice.service.logservice.SplitFileOutputStream;

import android.test.AndroidTestCase;
import android.test.MoreAsserts;

public class LogcatThreadTestCase extends AndroidTestCase {
	PipedOutputStream pos;
	PipedInputStream pis;
	LogcatThread lt;
	SplitFileOutputStream sfos;
	BlockingQueue<File> files;

	public void setUp() throws Exception {
		super.setUp();
		pos = new PipedOutputStream();
		pis = new PipedInputStream(pos);
		files = new LinkedBlockingQueue<File>();
		sfos = new SplitFileOutputStream(getContext().getExternalFilesDir(
				"test3"), "test3", files);
	}

	public void tearDown() throws Exception {
		try {
			if (lt != null) {
				lt.interrupt();
			}
			Closeables.closeQuietly(pos);
			Closeables.closeQuietly(pis);
		} finally {
			super.tearDown();
		}
	}

	public void testSplitBytes() throws Exception {
		Long s = 200L;
		Long maxBytes = 3L;
		Long timeoutMillis = 5000L;

		lt = new LogcatThread(sfos, pis, maxBytes, timeoutMillis);
		lt.start();

		pos.write(new byte[] { 0, 1, '\n' });
		pos.flush();
		Thread.sleep(s);
		assertEquals(1, files.size());
		MoreAsserts.assertEquals(new byte[] { 0, 1, '\n' },
				FileUtils.readFileToByteArray(files.poll()));

		pos.write(new byte[] { 0, 1, 2, '\n', 3 });
		pos.flush();
		Thread.sleep(s);
		assertEquals(1, files.size());
		MoreAsserts.assertEquals(new byte[] { 0, 1, 2, '\n' },
				FileUtils.readFileToByteArray(files.poll()));

		pos.write(new byte[] { 4 });
		pos.flush();
		Thread.sleep(s);
		assertEquals(0, files.size());

		pos.write(new byte[] { '\n' });
		pos.flush();
		Thread.sleep(s);
		assertEquals(1, files.size());
		MoreAsserts.assertEquals(new byte[] { 3, 4, '\n' },
				FileUtils.readFileToByteArray(files.poll()));

		pos.write(new byte[] { '\n', 5 });
		pos.flush();
		Thread.sleep(s);
		assertEquals(0, files.size());

		pos.write(new byte[] { 6, '\n' });
		pos.flush();
		Thread.sleep(s);
		assertEquals(1, files.size());
		MoreAsserts.assertEquals(new byte[] { '\n', 5, 6, '\n' },
				FileUtils.readFileToByteArray(files.poll()));
	}

	public void testSplitTimeout() throws Exception {
		Long maxBytes = 10000L;
		Long timeoutMillis = 1000L;

		lt = new LogcatThread(sfos, pis, maxBytes, timeoutMillis);
		lt.start();

		pos.write(new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 });
		pos.flush();
		Thread.sleep(timeoutMillis / 2);
		assertEquals(0, files.size());
		Thread.sleep(timeoutMillis);
		assertEquals(1, files.size());

		MoreAsserts.assertEquals(new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 },
				FileUtils.readFileToByteArray(files.poll()));

		byte[] b = "test文字列データ".getBytes(Charsets.UTF_16);
		pos.write(b);
		pos.flush();
		Thread.sleep(timeoutMillis / 2);
		assertEquals(0, files.size());
		Thread.sleep(timeoutMillis);
		assertEquals(1, files.size());

		MoreAsserts
				.assertEquals(b, FileUtils.readFileToByteArray(files.poll()));
	}
}
