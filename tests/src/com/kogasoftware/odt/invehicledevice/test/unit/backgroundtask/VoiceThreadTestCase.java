package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.kogasoftware.odt.invehicledevice.backgroundtask.BackgroundTask;
import com.kogasoftware.odt.invehicledevice.backgroundtask.VoiceThread;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class VoiceThreadTestCase extends EmptyActivityInstrumentationTestCase2 {
	CommonLogic cl;
	StatusAccess sa;
	VoiceThread vt;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		vt = new VoiceThread(getInstrumentation().getTargetContext());
		sa = new StatusAccess(getActivity());
		cl = new CommonLogic(getActivity(), getActivityHandler(), sa);
	}

	@Override
	protected void tearDown() throws Exception {
		if (vt != null) {
			vt.interrupt();
		}
		if (cl != null) {
			cl.dispose();
		}
		super.tearDown();
	}

	public void testBackgroundTaskによってUiEventBusに登録される() throws Exception {
		final AtomicReference<BackgroundTask> bt = new AtomicReference<BackgroundTask>();
		final CountDownLatch cdl = new CountDownLatch(1);
		Thread t = new Thread() {
			@Override
			public void run() {
				bt.set(new BackgroundTask(cl,
						getInstrumentation().getTargetContext(), sa));
				cdl.countDown();
				bt.get().loop();
			}
		};
		t.start();
		assertTrue(cdl.await(5, TimeUnit.SECONDS));
		Thread.sleep(5000);
		assertEquals(cl.countRegisteredClass(VoiceThread.class).intValue(), 1);
		bt.get().quit();
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
