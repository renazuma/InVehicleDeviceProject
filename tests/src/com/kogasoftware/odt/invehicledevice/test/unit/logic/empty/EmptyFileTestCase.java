package com.kogasoftware.odt.invehicledevice.test.unit.logic.empty;

import java.io.File;

import junit.framework.TestCase;

import com.kogasoftware.odt.invehicledevice.logic.empty.EmptyFile;

public class EmptyFileTestCase extends TestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * 何もしない
	 */
	public void testDelete() {
		File f = new EmptyFile();
		boolean e = f.exists();
		assertFalse(f.delete());
		assertEquals(e, f.exists());
	}

	/**
	 * 何もしない
	 */
	public void testDeleteOnExit() {
		File f = new EmptyFile();
		f.deleteOnExit();
		fail("stub!");
	}

	/**
	 * 普通の使い方をして非検査例外がおきないことをチェック
	 */
	public void testEmptyFile() throws Exception {
		File f = new EmptyFile();
		f.exists();
		f.canRead();
		f.getAbsoluteFile();
		f.delete();
		f.mkdir();
	}

	/**
	 * 何もしない
	 */
	public void testRenameTo() {
		File f = new EmptyFile();
		File f2 = new File(f + ".renamed");
		assertFalse(f2.exists());

		boolean e = f.exists();
		assertFalse(f.renameTo(f2));
		assertEquals(e, f.exists());
		assertFalse(f2.exists());
	}
}
