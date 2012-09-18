package com.kogasoftware.odt.invehicledevice.test.unit.service.logservice;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import android.content.Context;
import android.os.DropBoxManager;
import android.test.AndroidTestCase;
import android.util.Base64;

import com.google.common.base.Charsets;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.service.logservice.DropBoxThread;
import com.kogasoftware.odt.invehicledevice.service.logservice.SplitFileOutputStream;

public class DropBoxThreadTestCase extends AndroidTestCase {
	DropBoxManager dbm;
	SplitFileOutputStream sfos;
	DropBoxThread dbt;
	File d;
	BlockingQueue<File> files;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		dbm = (DropBoxManager) getContext().getSystemService(
				Context.DROPBOX_SERVICE);
		files = new LinkedBlockingQueue<File>();
		d = getContext().getExternalFilesDir("test2");
		FileUtils.deleteDirectory(d);
		d.mkdirs();
	}

	@Override
	public void tearDown() throws Exception {
		try {
			if (dbt != null) {
				dbt.interrupt();
			}
			if (sfos != null) {
				Closeables.closeQuietly(sfos);
			}
		} finally {
			super.tearDown();
		}
	}

	List<JSONObject> read(File file) throws Exception {
		assertNotNull(file);
		List<JSONObject> l = new LinkedList<JSONObject>();
		for (String s : FileUtils.readFileToString(file, DropBoxThread.CHARSET)
				.split(DropBoxThread.DELIMITER)) {
			if (s.replaceAll("^[\r\n\t ]+", "").isEmpty()) {
				continue;
			}
			l.add(new JSONObject(s));
		}
		return l;
	}

	String decode(String base64) {
		return new String(Base64.decode(base64, 0), Charsets.UTF_8);
	}

	public void testSplitBytes() throws Exception {
		Long splitBytes = 2000L;
		Long timeoutMillis = 50000L;
		Long checkIntervalMillis = 0L;
		sfos = new SplitFileOutputStream(d, "test2", files);
		dbt = new DropBoxThread(getContext(), sfos, splitBytes, timeoutMillis,
				checkIntervalMillis);
		dbm.addData("test1", new byte[splitBytes.intValue()], 0);
		dbt.dumpToFile();
		files.clear();

		{ // no split
			assertEquals(0, files.size());
			dbm.addText("test1", "test1dataa");
			dbt.dumpToFile();
			assertEquals(0, files.size());
			dbm.addText("test1", "test1datab");
			dbt.dumpToFile();
			assertEquals(0, files.size());
			dbm.addText("test1", "test1datac");
			dbt.dumpToFile();
			assertEquals(0, files.size());
			dbm.addData("test1", new byte[splitBytes.intValue()], 0);
			dbt.dumpToFile();
			assertEquals(1, files.size());
		}

		List<JSONObject> l = read(files.poll());
		assertEquals("test1", l.get(0).get("tag"));
		assertEquals("test1dataa", decode(l.get(0).getString("contents")));
		assertEquals("test1", l.get(1).get("tag"));
		assertEquals("test1datab", decode(l.get(1).getString("contents")));
		assertEquals("test1", l.get(2).get("tag"));
		assertEquals("test1datac", decode(l.get(2).getString("contents")));
		assertEquals("test1", l.get(3).get("tag"));
		files.clear();

		{ // split
			assertEquals(0, files.size());
			dbm.addText("test2", "test2dataa");
			dbt.dumpToFile();
			assertEquals(0, files.size());
			dbm.addText("test2", "test2datab");
			dbt.dumpToFile();
			assertEquals(0, files.size());
			dbm.addData("test2", new byte[splitBytes.intValue()], 0);
			dbt.dumpToFile();
			assertEquals(1, files.size());

			dbm.addText("test3", "test3dataa");
			dbt.dumpToFile();
			assertEquals(1, files.size());
			dbm.addData("test3", new byte[splitBytes.intValue()], 0);
			dbt.dumpToFile();
			assertEquals(2, files.size());
		}

		l = read(files.poll());
		assertEquals("test2", l.get(0).get("tag"));
		assertEquals("test2dataa", decode(l.get(0).getString("contents")));
		assertEquals("test2", l.get(1).get("tag"));
		assertEquals("test2datab", decode(l.get(1).getString("contents")));
		assertEquals("test2", l.get(2).get("tag"));

		l = read(files.poll());
		assertEquals("test3", l.get(0).get("tag"));
		assertEquals("test3dataa", decode(l.get(0).getString("contents")));
		assertEquals("test3", l.get(1).get("tag"));
	}

	public void testSplitTimeout() throws Exception {
		Long splitBytes = 5000L;
		Long timeoutMillis = 500L;
		Long checkIntervalMillis = 5000L;
		sfos = new SplitFileOutputStream(d, "test2", files);
		dbt = new DropBoxThread(getContext(), sfos, splitBytes, timeoutMillis,
				checkIntervalMillis);

		Thread.sleep((long) (timeoutMillis * 1.1)); // clear
		files.clear();

		{ // no split
			assertEquals(0, files.size());
			dbm.addText("test1", "test1dataa");
			dbt.dumpToFile();
			assertEquals(0, files.size());
			dbm.addText("test1", "test1datab");
			dbt.dumpToFile();
			assertEquals(0, files.size());
			dbm.addText("test1", "test1datac");
			dbt.dumpToFile();
			assertEquals(0, files.size());
		}

		Thread.sleep((long) (timeoutMillis * 1.1)); // clear
		dbt.dumpToFile();
		List<JSONObject> l = read(files.poll());
		assertEquals("test1", l.get(0).get("tag"));
		assertEquals("test1dataa", decode(l.get(0).getString("contents")));
		assertEquals("test1", l.get(1).get("tag"));
		assertEquals("test1datab", decode(l.get(1).getString("contents")));
		assertEquals("test1", l.get(2).get("tag"));
		assertEquals("test1datac", decode(l.get(2).getString("contents")));
		files.clear();

		{ // split
			assertEquals(0, files.size());
			dbm.addText("test2", "test2dataa");
			dbt.dumpToFile();
			assertEquals(0, files.size());

			dbm.addText("test2", "test2datab");
			dbt.dumpToFile();
			assertEquals(0, files.size());

			dbm.addText("test2", "test2datac");
			dbt.dumpToFile();
			assertEquals(0, files.size());

			Thread.sleep((long) (timeoutMillis * 1.1));
			dbt.dumpToFile();
			assertEquals(1, files.size());

			dbm.addText("test3", "test3dataa");
			dbt.dumpToFile();
			assertEquals(1, files.size());

			dbm.addText("test3", "test3datab");
			dbt.dumpToFile();

			Thread.sleep((long) (timeoutMillis * 1.1));
			dbt.dumpToFile();
			assertEquals(2, files.size());
		}

		l = read(files.poll());
		assertEquals("test2", l.get(0).get("tag"));
		assertEquals("test2dataa", decode(l.get(0).getString("contents")));
		assertEquals("test2", l.get(1).get("tag"));
		assertEquals("test2datab", decode(l.get(1).getString("contents")));
		assertEquals("test2", l.get(2).get("tag"));
		assertEquals("test2datac", decode(l.get(2).getString("contents")));

		l = read(files.poll());
		assertEquals("test3", l.get(0).get("tag"));
		assertEquals("test3dataa", decode(l.get(0).getString("contents")));
		assertEquals("test3", l.get(1).get("tag"));
		assertEquals("test3datab", decode(l.get(1).getString("contents")));
	}

	public void testSplitTimeoutCheckInterval() throws Exception {
		Long splitBytes = 2000L;
		Long timeoutMillis = 200L;
		Long checkIntervalMillis = 1000L;
		assertTrue(checkIntervalMillis / timeoutMillis > 3);
		dbm.addData("test1", new byte[splitBytes.intValue()], 0);
		sfos = new SplitFileOutputStream(d, "test2", files);
		dbt = new DropBoxThread(getContext(), sfos, splitBytes, timeoutMillis,
				checkIntervalMillis);
		dbt.start();
		Thread.sleep((long) (checkIntervalMillis * 1.1)); // clear
		files.clear();

		{ // split by timeout
			dbm.addText("test1a", "test1dataa");
			Thread.sleep(timeoutMillis);
			assertEquals(0, files.size());
			dbm.addText("test1b", "test1datab");
			Thread.sleep(timeoutMillis);
			assertEquals(0, files.size());
			Thread.sleep(checkIntervalMillis * 2);
			assertEquals(1, files.size());

			dbm.addText("test1c", "test1datac");
			Thread.sleep(timeoutMillis);
			assertEquals(1, files.size());
			Thread.sleep(checkIntervalMillis * 2);
			assertEquals(2, files.size());
		}

		Thread.sleep((long) (timeoutMillis * 1.1)); // clear
		List<JSONObject> l = read(files.poll());
		assertEquals("test1a", l.get(0).get("tag"));
		assertEquals("test1dataa", decode(l.get(0).getString("contents")));
		assertEquals("test1b", l.get(1).get("tag"));
		assertEquals("test1datab", decode(l.get(1).getString("contents")));

		l = read(files.poll());
		assertEquals("test1c", l.get(0).get("tag"));
		assertEquals("test1datac", decode(l.get(0).getString("contents")));
	}

	public void testSplitBytesCheckInterval() throws Exception {
		Long splitBytes = 5000L;
		Long timeoutMillis = 10000L;
		Long checkIntervalMillis = 500L;
		dbm.addData("test1", new byte[splitBytes.intValue()], 0);
		sfos = new SplitFileOutputStream(d, "test2", files);
		dbt = new DropBoxThread(getContext(), sfos, splitBytes, timeoutMillis,
				checkIntervalMillis);
		dbt.start();
		Thread.sleep((long) (checkIntervalMillis * 1.2)); // clear
		files.clear();

		{ // split by bytes
			dbm.addText("test1a", "test1dataa");
			Thread.sleep((long) (checkIntervalMillis * 0.1));
			assertEquals(0, files.size());
			Thread.sleep(checkIntervalMillis);
			assertEquals(0, files.size());

			dbm.addText("test1b", "test1datab");
			Thread.sleep((long) (checkIntervalMillis * 0.1));
			assertEquals(0, files.size());
			Thread.sleep(checkIntervalMillis);
			assertEquals(0, files.size());

			dbm.addData("test1c", new byte[splitBytes.intValue()], 0);
			Thread.sleep((long) (checkIntervalMillis * 0.1));
			assertEquals(0, files.size());
			Thread.sleep(checkIntervalMillis);
			assertEquals(1, files.size());

			dbm.addText("test1d", "test1datad");
			Thread.sleep((long) (checkIntervalMillis * 0.1));
			assertEquals(1, files.size());
			Thread.sleep(checkIntervalMillis);
			assertEquals(1, files.size());

			dbm.addData("test1e", new byte[splitBytes.intValue()], 0);
			Thread.sleep((long) (checkIntervalMillis * 0.1));
			assertEquals(1, files.size());
			Thread.sleep(checkIntervalMillis);
			assertEquals(2, files.size());
		}

		List<JSONObject> l = read(files.poll());
		assertEquals("test1a", l.get(0).get("tag"));
		assertEquals("test1dataa", decode(l.get(0).getString("contents")));
		assertEquals("test1b", l.get(1).get("tag"));
		assertEquals("test1datab", decode(l.get(1).getString("contents")));
		assertEquals("test1c", l.get(2).get("tag"));

		l = read(files.poll());
		assertEquals("test1d", l.get(0).get("tag"));
		assertEquals("test1datad", decode(l.get(0).getString("contents")));
		assertEquals("test1e", l.get(1).get("tag"));
	}

	public void testInterrupt() throws Exception {
		Long splitBytes = 5000L;
		Long timeoutMillis = 5000L;
		Long checkIntervalMillis = 500L;
		dbm.addData("test1", new byte[splitBytes.intValue()], 0);
		sfos = new SplitFileOutputStream(d, "test2", files);
		dbt = new DropBoxThread(getContext(), sfos, splitBytes, timeoutMillis,
				checkIntervalMillis);
		dbt.start();
		Thread.sleep((long) (checkIntervalMillis * 1.1)); // clear
		files.clear();

		{ // split by interrupt
			dbm.addText("test1a", "test1dataa");
			dbm.addText("test1b", "test1datab");
			Thread.sleep(checkIntervalMillis * 2);
			assertEquals(0, files.size());
			dbt.interrupt();
			dbt.join();
			assertEquals(1, files.size());
		}

		Thread.sleep((long) (timeoutMillis * 1.1)); // clear
		List<JSONObject> l = read(files.poll());
		assertEquals("test1a", l.get(0).get("tag"));
		assertEquals("test1dataa", decode(l.get(0).getString("contents")));
		assertEquals("test1b", l.get(1).get("tag"));
		assertEquals("test1datab", decode(l.get(1).getString("contents")));
	}
}


