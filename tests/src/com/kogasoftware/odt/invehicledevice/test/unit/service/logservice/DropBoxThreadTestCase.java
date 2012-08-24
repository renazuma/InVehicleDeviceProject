package com.kogasoftware.odt.invehicledevice.test.unit.service.logservice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONObject;

import com.google.common.io.ByteStreams;
import com.kogasoftware.odt.invehicledevice.service.logservice.DropBoxThread;

import android.content.Context;
import android.os.DropBoxManager;
import android.test.AndroidTestCase;

public class DropBoxThreadTestCase extends AndroidTestCase {
	DropBoxManager dbm;
	ByteArrayOutputStream baos;
	DropBoxThread dbt;
	File d;

	public void setUp() throws Exception {
		super.setUp();
		dbm = (DropBoxManager) getContext().getSystemService(
				Context.DROPBOX_SERVICE);
		dbt = new DropBoxThread(baos, getContext());
		dbt.start();
	}

	public void tearDown() throws Exception {
		try {
			if (dbt != null) {
				dbt.interrupt();
			}
		} finally {
			super.tearDown();
		}
	}

	public void testOutputFormat() throws Exception {
		dbm.addText("hello", "world");
		Thread.sleep(10 * 1000);
		String s = baos.toString();
		new JSONObject(s);
	}
}
