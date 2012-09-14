package com.kogasoftware.odt.invehicledevice.test.unit.empty;

import java.io.File;

import android.test.InstrumentationTestCase;

import com.kogasoftware.odt.invehicledevice.empty.EmptyFile;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;

public class EmptyFileTestCase extends InstrumentationTestCase {
	public void testSmallInstance() throws Exception {
		TestUtil.assertEmptyObject(getInstrumentation(), EmptyFile.class);
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

	/**
	 * 何もしない
	 */
	public void xtestDeleteOnExit() {
		File f = new EmptyFile();
		f.deleteOnExit();
		fail("stub!");
	}
}
