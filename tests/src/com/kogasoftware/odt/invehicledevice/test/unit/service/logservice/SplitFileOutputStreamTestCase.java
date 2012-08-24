package com.kogasoftware.odt.invehicledevice.test.unit.service.logservice;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.test.AndroidTestCase;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.service.logservice.SplitFileOutputStream;

public class SplitFileOutputStreamTestCase extends AndroidTestCase {

	SplitFileOutputStream sfos;
	File d;
	BlockingQueue<File> files = new LinkedBlockingQueue<File>();

	@Override
	public void setUp() throws Exception {
		super.setUp();
		d = getContext().getFilesDir();
	}

	@Override
	public void tearDown() throws Exception {
		try {
			Closeables.closeQuietly(sfos);
		} finally {
			super.tearDown();
		}
	}
	
	public void testLogMaxBytes() throws Exception {
		Integer timeoutSeconds = 60 * 60;
		Integer maxBytes = 3;
		sfos = new SplitFileOutputStream(d, "test10", files, timeoutSeconds,
				maxBytes);
		String outputString = "0123456789ab";
		byte[] outputBytes = outputString.getBytes(Charsets.UTF_8);
		sfos.write(outputBytes);
		assertEquals(outputBytes.length / maxBytes, files.size());
		sfos.close();
		assertEquals(outputBytes.length / maxBytes + 1, files.size());
		Thread.sleep(1000);
		
		List<FileReader> readers = new LinkedList<FileReader>();
		readers.add(new FileReader(files.take()));
		readers.add(new FileReader(files.take()));
		readers.add(new FileReader(files.take()));
		readers.add(new FileReader(files.take()));
		
		assertEquals("012", CharStreams.toString(readers.get(0)));
		assertEquals("345", CharStreams.toString(readers.get(1)));
		assertEquals("678", CharStreams.toString(readers.get(2)));
		assertEquals("9ab", CharStreams.toString(readers.get(3)));
		
		for (Closeable c : readers) {
			Closeables.closeQuietly(c);
		}
	}

	public void xtestLogTimeout() throws Exception {
		Integer timeoutSeconds = 1;
		Integer maxBytes = 2;
		sfos = new SplitFileOutputStream(d, "test1", files, timeoutSeconds,
				maxBytes);
		
		sfos.write('A');
		Thread.sleep(timeoutSeconds * 1000 / 2);
		assertEquals(0, files.size());
		Thread.sleep(timeoutSeconds * 1000);
		assertEquals(1, files.size());
		Thread.sleep(timeoutSeconds * 1000);
		assertEquals(1, files.size());
		
		sfos.write("BCD".getBytes(Charsets.UTF_8));
		Thread.sleep(timeoutSeconds * 1000 / 2);
		assertEquals(2, files.size());
		Thread.sleep(timeoutSeconds * 1000);
		assertEquals(3, files.size());
		Thread.sleep(timeoutSeconds * 1000);
		assertEquals(3, files.size());
		
		File f1 = files.take();
		assertEquals("A", CharStreams.toString((new FileReader(f1))));
		File f2 = files.take();
		assertEquals("BC", CharStreams.toString((new FileReader(f2))));
		File f3 = files.take();
		assertEquals("D", CharStreams.toString((new FileReader(f3))));
	}
}

