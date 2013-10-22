package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import android.test.AndroidTestCase;

import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.service.logservice.SplitFileOutputStream;

public class SplitFileOutputStreamTestCase extends AndroidTestCase {

	SplitFileOutputStream sfos;
	File d;
	BlockingQueue<File> files = new LinkedBlockingQueue<File>();

	@Override
	public void setUp() throws Exception {
		super.setUp();
		d = getContext().getExternalFilesDir("test1");
	}

	@Override
	public void tearDown() throws Exception {
		try {
			Closeables.close(sfos, false);
		} finally {
			super.tearDown();
		}
	}
	
	public void testGetElapsedMillisSinceFirstWrite() throws Exception {
		Stopwatch sw = new Stopwatch();
		int unit = 100;
		
		sfos = new SplitFileOutputStream(d, "test10", files);
		Thread.sleep(unit);
		assertEquals(0, sfos.getElapsedMillisSinceFirstWrite().intValue());
		
		sw.start();
		sfos.write(new byte[] { '1', '2', '3' });
		assertEquals(sw.elapsed(TimeUnit.MILLISECONDS) / unit, sfos.getElapsedMillisSinceFirstWrite() / unit);
		Thread.sleep(unit);
		assertEquals(sw.elapsed(TimeUnit.MILLISECONDS) / unit, sfos.getElapsedMillisSinceFirstWrite() / unit);
		Thread.sleep(unit);
		assertEquals(sw.elapsed(TimeUnit.MILLISECONDS) / unit, sfos.getElapsedMillisSinceFirstWrite() / unit);
		sw.reset();
		
		sfos.split();
		Thread.sleep(unit);
		assertEquals(0, sfos.getElapsedMillisSinceFirstWrite().intValue());

		sw.start();
		sfos.write('A');
		sfos.write('B');
		assertEquals(sw.elapsed(TimeUnit.MILLISECONDS) / unit, sfos.getElapsedMillisSinceFirstWrite() / unit);
		Thread.sleep(unit);
		assertEquals(sw.elapsed(TimeUnit.MILLISECONDS) / unit, sfos.getElapsedMillisSinceFirstWrite() / unit);
		sw.reset();
		
		sfos.split();
		assertEquals(0, sfos.getElapsedMillisSinceFirstWrite().intValue());
		
		sw.start();
		sfos.write("foobar".getBytes(Charsets.UTF_8));
		assertEquals(sw.elapsed(TimeUnit.MILLISECONDS) / unit, sfos.getElapsedMillisSinceFirstWrite() / unit);
		Thread.sleep(unit);
		assertEquals(sw.elapsed(TimeUnit.MILLISECONDS) / unit, sfos.getElapsedMillisSinceFirstWrite() / unit);
	}

	public void testSplit() throws Exception {
		sfos = new SplitFileOutputStream(d, "test10", files);
		assertEquals(0, sfos.getCount().intValue());
		
		sfos.write(new byte[] { '1', '2', '3' });
		assertEquals(3, sfos.getCount().intValue());
		assertEquals(0, files.size());
		sfos.split();
		assertEquals(0, sfos.getCount().intValue());
		assertEquals(1, files.size());
		
		sfos.split();
		assertEquals(0, sfos.getCount().intValue());
		assertEquals(1, files.size());

		sfos.write('A');
		assertEquals(1, sfos.getCount().intValue());
		sfos.write('B');
		assertEquals(2, sfos.getCount().intValue());
		assertEquals(1, files.size());
		sfos.split();
		assertEquals(0, sfos.getCount().intValue());
		assertEquals(2, files.size());

		sfos.close();

		assertEquals("123", FileUtils.readFileToString(files.poll()));
		assertEquals("AB", FileUtils.readFileToString(files.poll()));
	}
}
