package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.GZIPInputStream;

import junitx.framework.ComparableAssert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;
import com.kogasoftware.odt.invehicledevice.service.logservice.CompressThread;

import android.os.Environment;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;

public class CompressThreadTestCase extends AndroidTestCase {
	Charset c = Charsets.UTF_8;
	BlockingQueue<File> inputFiles = new LinkedBlockingQueue<File>();
	BlockingQueue<File> outputFiles = new LinkedBlockingQueue<File>();
	Thread ct;

	public void setUp() {
		ct = new CompressThread(getContext(), inputFiles, outputFiles);
	}

	public void tearDown() throws Exception {
		try {
			if (ct != null) {
				ct.interrupt();
			}
		} finally {
			super.tearDown();
		}
	}

	public void testStart() throws Exception {
		long s = 1000;
		ct.start();

		StringBuilder d1 = new StringBuilder("compress data");
		for (int i = 0; i < 500; ++i) {
			d1.append("data" + i);
		}

		ByteArrayOutputStream d2 = new ByteArrayOutputStream();
		for (int i = 0; i < 16 * 1024; ++i) {
			d2.write((byte) i);
		}
		d2.close();

		File f1 = new File(Environment.getExternalStorageDirectory(), "CompressThreadTestCase.fuga");
		File fe = new File(Environment.getExternalStorageDirectory(), "CompressThreadTestCase.empty");
		File f2 = getContext().getFileStreamPath("CompressThreadTestCase.foobar");

		FileUtils.writeByteArrayToFile(f1, d1.toString().getBytes(c));
		FileUtils.touch(fe);
		FileUtils.writeByteArrayToFile(f2, d2.toByteArray());
		Long f1Length = f1.length();
		Long f2Length = f2.length();

		inputFiles.add(f1);
		inputFiles.add(fe);
		inputFiles.add(f2);
		Thread.sleep(s);
		Thread.sleep(s);
		assertEquals(0, inputFiles.size());
		assertEquals(2, outputFiles.size());
		File of1 = outputFiles.poll();
		File of2 = outputFiles.poll();
		assertFalse(f1.exists());
		assertFalse(fe.exists());
		assertFalse(f2.exists());
		assertEquals(f1 + CompressThread.COMPRESSED_FILE_SUFFIX, of1.toString());
		assertEquals(f2 + CompressThread.COMPRESSED_FILE_SUFFIX, of2.toString());

		ComparableAssert.assertLesser(f1Length, of1.length());
		ComparableAssert.assertLesser(f2Length, of2.length());

		Closer closer = Closer.create();
		try {
			GZIPInputStream is1 = closer.register(new GZIPInputStream(new ByteArrayInputStream(
					FileUtils.readFileToByteArray(of1))));
			GZIPInputStream is2 = closer.register(new GZIPInputStream(new ByteArrayInputStream(
					FileUtils.readFileToByteArray(of2))));
			MoreAsserts.assertEquals(d1.toString().getBytes(c),
					ByteStreams.toByteArray(is1));
			MoreAsserts.assertEquals(d2.toByteArray(),
					ByteStreams.toByteArray(is2));
		} catch (Throwable e) {
			throw closer.rethrow(e);
		} finally {
			closer.close();
		}
	}
}
