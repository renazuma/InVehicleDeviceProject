package com.kogasoftware.odt.invehicledevice.service.voiceservice;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.service.voiceservice.VoiceThread;
import com.kogasoftware.odt.invehicledevice.testutil.EmptyActivityInstrumentationTestCase2;

public class VoiceThreadTestCase extends EmptyActivityInstrumentationTestCase2 {
	LocalStorage sa;
	VoiceThread vt;
	BlockingQueue<String> bq;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		bq = new LinkedBlockingQueue<String>();
		vt = new VoiceThread(getInstrumentation().getTargetContext(), bq);
		sa = new LocalStorage();
	}

	@Override
	protected void tearDown() throws Exception {
		if (vt != null) {
			vt.interrupt();
		}
		super.tearDown();
	}

	public void xtestVoiceThread_1() throws Exception {
		fail("stub! / physical test required");
	}

	public void testSplit() {
		// 行分割
		Iterator<String> l;
		l = VoiceThread.split("", 3).iterator();
		assertFalse(l.hasNext());
		l = VoiceThread.split("\n", 3).iterator();
		assertFalse(l.hasNext());
		l = VoiceThread.split("\n\n\r\n", 3).iterator();
		assertFalse(l.hasNext());

		l = VoiceThread.split("a", 3).iterator();
		assertEquals("a", l.next());
		assertFalse(l.hasNext());

		l = VoiceThread.split("aa", 3).iterator();
		assertEquals("aa", l.next());
		assertFalse(l.hasNext());

		l = VoiceThread.split("aa\nbb", 3).iterator();
		assertEquals("aa", l.next());
		assertEquals("bb", l.next());
		assertFalse(l.hasNext());

		l = VoiceThread.split("aa\nbb\rcc", 3).iterator();
		assertEquals("aa", l.next());
		assertEquals("bb", l.next());
		assertEquals("cc", l.next());
		assertFalse(l.hasNext());

		l = VoiceThread.split("\raa\nbb\r\ncc\r\n", 3).iterator();
		assertEquals("aa", l.next());
		assertEquals("bb", l.next());
		assertEquals("cc", l.next());
		assertFalse(l.hasNext());

		// 句読点分割
		l = VoiceThread.split("\raaa\nbb、bb\r\nc、c\r\n", 3).iterator();
		assertEquals("aaa", l.next());
		assertEquals("bb", l.next());
		assertEquals("bb", l.next());
		assertEquals("c、c", l.next());
		assertFalse(l.hasNext());

		l = VoiceThread.split("\ra。\nb。b\r\nc　c\r\n", 2).iterator();
		assertEquals("a。", l.next());
		assertEquals("b", l.next());
		assertEquals("b", l.next());
		assertEquals("c", l.next());
		assertEquals("c", l.next());
		assertFalse(l.hasNext());

		// 句読点で分割しても足りない場合文字数で分割
		l = VoiceThread.split("\raaaa\nbbb\r\nc、cccc、c\r\n", 3).iterator();
		assertEquals("aaa", l.next());
		assertEquals("a", l.next());
		assertEquals("bbb", l.next());
		assertEquals("c", l.next());
		assertEquals("ccc", l.next());
		assertEquals("c", l.next());
		assertEquals("c", l.next());
		assertFalse(l.hasNext());

		l = VoiceThread.split("\raaa a\nbbb\r\nc、cccc、c\r\n", 2).iterator();
		assertEquals("aa", l.next());
		assertEquals("a", l.next());
		assertEquals("a", l.next());
		assertEquals("bb", l.next());
		assertEquals("b", l.next());
		assertEquals("c", l.next());
		assertEquals("cc", l.next());
		assertEquals("cc", l.next());
		assertEquals("c", l.next());
		assertFalse(l.hasNext());
	}
}
