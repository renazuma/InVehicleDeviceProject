package com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.tilepipeline;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.kogasoftware.odt.invehicledevice.ui.frametask.navigation.tilepipeline.TileBitmapCleaner;

import android.test.AndroidTestCase;

public class TileBitmapCleanerTestCase extends AndroidTestCase {
	public void testRun() throws Exception {
		File d = new File(getContext().getFilesDir(), "testx");
		FileUtils.deleteDirectory(d);
		FileUtils.forceMkdir(d);

		Integer limit = 5000;

		File f1 = new File(d, "test1");
		File f2 = new File(d, "test2");
		File f3 = new File(d, "test3");

		FileUtils.writeByteArrayToFile(f1, new byte[limit / 4 * 3]);
		FileUtils.writeByteArrayToFile(f2, new byte[limit / 4 * 3]);
		FileUtils.writeByteArrayToFile(f3, new byte[limit / 4 * 3]);

		TileBitmapCleaner cleaner = new TileBitmapCleaner(d, limit);
		cleaner.run();

		assertEquals(1,
				(f1.exists() ? 1 : 0) + (f2.exists() ? 1 : 0)
						+ (f3.exists() ? 1 : 0));
	}
}
