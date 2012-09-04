package com.kogasoftware.odt.invehicledevice.test.unit.service.logservice;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.kogasoftware.odt.invehicledevice.service.logservice.SendLogBroadcastReceiver;

import android.content.Intent;
import android.os.Environment;
import android.test.AndroidTestCase;

public class SendLogBroadcastReceiverTestCase extends AndroidTestCase {
	BlockingQueue<File> files = new LinkedBlockingQueue<File>();
	SendLogBroadcastReceiver slbr = new SendLogBroadcastReceiver(files);

	public void testOnReceive_IntentIsNull() {
		slbr.onReceive(getContext(), null);
		assertEquals(0, files.size());
	}

	public void testOnReceive_IntentExtrasIsNull() {
		Intent i = new Intent();
		slbr.onReceive(getContext(), i);
		assertEquals(0, files.size());
	}

	public void testOnReceive_NoKey() {
		Intent i = new Intent();
		i.putExtra("foo", "bar");
		slbr.onReceive(getContext(), i);
		assertEquals(0, files.size());
	}

	public void testOnReceive_FileNotFound() {
		Intent i = new Intent();
		i.putExtra(SendLogBroadcastReceiver.EXTRAS_KEY_LOG_FILE_NAME, "存在しない");
		slbr.onReceive(getContext(), i);
		assertEquals(0, files.size());
	}

	public void testOnReceive_Success() throws Exception {
		Intent i = new Intent();
		File f1 = File.createTempFile("foo", "bar",
				Environment.getExternalStorageDirectory());
		i.putExtra(SendLogBroadcastReceiver.EXTRAS_KEY_LOG_FILE_NAME, f1.toString());
		slbr.onReceive(getContext(), i);
		assertEquals(1, files.size());

		i = new Intent();
		i.putExtra(SendLogBroadcastReceiver.EXTRAS_KEY_LOG_FILE_NAME, "存在しない");
		slbr.onReceive(getContext(), i);
		assertEquals(1, files.size());
		
		File f2 = File.createTempFile("foo", "baz",
				Environment.getExternalStorageDirectory());
		i.putExtra(SendLogBroadcastReceiver.EXTRAS_KEY_LOG_FILE_NAME, f2.toString());
		slbr.onReceive(getContext(), i);
		assertEquals(2, files.size());
	}

	public void tearDown() throws Exception {
		try {
			files.clear();
		} finally {
			super.tearDown();
		}
	}
}
