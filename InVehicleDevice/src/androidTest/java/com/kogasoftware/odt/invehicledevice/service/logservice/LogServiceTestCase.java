package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.mockito.ArgumentCaptor;

import com.kogasoftware.odt.invehicledevice.service.logservice.LogService;
import com.kogasoftware.odt.invehicledevice.service.logservice.SplitFileOutputStream;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.test.ServiceTestCase;

import static org.mockito.Mockito.*;

public class LogServiceTestCase extends ServiceTestCase<LogService> {
	File d;

	public LogServiceTestCase() {
		super(LogService.class);
	}

	public void setUp() throws Exception {
		super.setUp();
		d = new File(Environment.getExternalStorageDirectory(), "test");
		try {
			FileUtils.deleteDirectory(d);
		} catch (IOException e) {
		}
		assertTrue(d.mkdirs());
	}
	
	public void testStartLog() throws InterruptedException {
		startService(new Intent());
		LogService s = getService();
		s.onCreate();
		assertTrue(s.startLog(mock(SplitFileOutputStream.class), mock(SplitFileOutputStream.class)));
		s.onDestroy();
		assertFalse(s.startLog(mock(SplitFileOutputStream.class), mock(SplitFileOutputStream.class)));
		Thread.sleep(5000);
	}
	
	public void testShutdown() {
		Context context = mock(Context.class);
		(new LogService.ShutdownBroadcastReceiver()).onReceive(context, new Intent());
		ArgumentCaptor<Intent> intentCapture = ArgumentCaptor.forClass(Intent.class);
		verify(context).stopService(intentCapture.capture());
		Intent intent = intentCapture.getValue();
		assertEquals(LogService.class.getName(), intent.getComponent().getClassName());
	}
	
	public void testWaitForDataDirectory() throws IOException,
			InterruptedException {
		File tmp1 = getContext().getDir("foo", 0755);
		if (tmp1.exists()) {
			FileUtils.forceDelete(tmp1);
		}

		final File d = new File(tmp1, "foo");
		assertTrue(tmp1.mkdir());
		assertTrue(d.mkdir());
		assertTrue(d.setReadOnly());

		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					LogService.waitForDataDirectory(d);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		};
		t.start();
		t.join((int) (LogService.CHECK_DEVICE_INTERVAL_MILLIS * 1.2));
		assertTrue(t.isAlive());
		assertTrue(d.setWritable(true));
		t.join((int) (LogService.CHECK_DEVICE_INTERVAL_MILLIS * 1.2));
		assertFalse(t.isAlive());
	}

	public void testGetCompressedLogFiles() throws Exception {
		File f1 = new File(d, "a.txt").getCanonicalFile();
		File f2 = new File(d, "b.gz").getCanonicalFile();
		File f3 = new File(d, "c.gza").getCanonicalFile();
		File f4 = new File(d, "dgz").getCanonicalFile();
		File f5 = new File(d, "e.gz").getCanonicalFile();

		f1.createNewFile();
		f2.createNewFile();
		f3.createNewFile();
		f4.createNewFile();
		f5.createNewFile();

		List<File> fs = LogService.getCompressedLogFiles(d);
		List<File> cfs = new LinkedList<File>();
		for (File f : fs) {
			cfs.add(f.getCanonicalFile());
		}
		assertEquals(2, cfs.size());
		assertTrue(cfs.contains(f2));
		assertTrue(cfs.contains(f5));
	}

	public void testGetRawLogFiles() throws Exception {
		File f1 = new File(d, "a.log").getCanonicalFile();
		File f2 = new File(d, "b.log").getCanonicalFile();
		File f3 = new File(d, "c.loga").getCanonicalFile();
		File f4 = new File(d, ".log").getCanonicalFile();
		File f5 = new File(d, "e.xlog").getCanonicalFile();

		f1.createNewFile();
		f2.createNewFile();
		f3.createNewFile();
		f4.createNewFile();
		f5.createNewFile();

		List<File> fs = LogService.getRawLogFiles(d);
		List<File> cfs = new LinkedList<File>();
		for (File f : fs) {
			cfs.add(f.getCanonicalFile());
		}

		assertEquals(3, cfs.size());
		assertTrue(cfs.contains(f1));
		assertTrue(cfs.contains(f2));
		assertTrue(cfs.contains(f4));
	}

	public void testGetDropBoxLogFiles() throws Exception {
		assertEquals("_dropbox_", LogService.DROPBOX_FILE_TAG); // DROPBOX_FILE_TAGが変更されたら下記のファイル名も変更する
		File f1 = new File(d, "dropbox.log").getCanonicalFile();
		File f2 = new File(d, "_dropbox.log").getCanonicalFile();
		File f3 = new File(d, "dropbox_.log").getCanonicalFile();
		File f4 = new File(d, "_dropbox_.log").getCanonicalFile(); // OK
		File f5 = new File(d, "a_dropbox_e.log").getCanonicalFile(); // OK
		File f6 = new File(d, "b_dropbox_f.log.gz").getCanonicalFile();
		File f7 = new File(d, "0_dropbox_x.alog").getCanonicalFile();
		File f8 = new File(d, "test.log").getCanonicalFile();

		f1.createNewFile();
		f2.createNewFile();
		f3.createNewFile();
		f4.createNewFile();
		f5.createNewFile();
		f6.createNewFile();
		f7.createNewFile();
		f8.createNewFile();

		List<File> fs = LogService.getDropBoxLogFiles(d);
		List<File> cfs = new LinkedList<File>();
		for (File f : fs) {
			cfs.add(f.getCanonicalFile());
		}

		assertEquals(2, cfs.size());
		assertTrue(cfs.contains(f4));
		assertTrue(cfs.contains(f5));
	}
}
