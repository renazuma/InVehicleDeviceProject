package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.nio.charset.Charset;
import java.util.Date;

import junitx.framework.StringAssert;

import com.google.common.base.Charsets;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.service.logservice.LogcatInputStream;
import com.kogasoftware.odt.invehicledevice.testutil.TestUtil;

import android.test.AndroidTestCase;
import android.util.Log;

public class LogcatInputStreamTestCase extends AndroidTestCase {
	LogcatInputStream lis;
	Charset c = Charsets.UTF_8;

	public void testRead() throws Exception {
		long s = 200;
		lis = new LogcatInputStream();
		
		String message = "メッセージ" + (new Date()).getTime() + "_" + Math.random();
		String l1 = new String(TestUtil.readWithNonBlock(lis, s), c);
		StringAssert.assertNotContains(message, l1);
		Log.v("foo", message);
		String l2 = new String(TestUtil.readWithNonBlock(lis, s), c);
		StringAssert.assertContains(message, l2);
		
		String tag = "タグ" + (new Date()).getTime() + "_" + Math.random();
		String l3 = new String(TestUtil.readWithNonBlock(lis, s), c);
		StringAssert.assertNotContains(message, l3);
		StringAssert.assertNotContains(tag, l3);
		Log.v(tag, "bar");
		String l4 = new String(TestUtil.readWithNonBlock(lis, s), c);
		StringAssert.assertNotContains(message, l4);
		StringAssert.assertContains(tag, l4);
	}

	public void tearDown() throws Exception {
		try {
			Closeables.closeQuietly(lis);
		} finally {
			super.tearDown();
		}
	}
}
