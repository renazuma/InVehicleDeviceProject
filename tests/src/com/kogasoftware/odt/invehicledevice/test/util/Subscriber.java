package com.kogasoftware.odt.invehicledevice.test.util;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.UiEventBus;

public class Subscriber<T> {
	final Class<T> c;
	public final CountDownLatch cdl = new CountDownLatch(1);
	public final Semaphore s = new Semaphore(0);
	public final List<T> l = new LinkedList<T>();

	protected Subscriber(Class<T> c) {
		this.c = c;
	}

	public static <T> Subscriber<T> of(Class<T> c) {
		Subscriber<T> s = new Subscriber<T>(c);
		return s;
	}

	public static <T> Subscriber<T> of(Class<T> c, CommonLogic cl) {
		Subscriber<T> s = new Subscriber<T>(c);
		cl.registerEventListener(s);
		return s;
	}

	public static <T> Subscriber<T> of(Class<T> c, UiEventBus ueb) {
		Subscriber<T> s = new Subscriber<T>(c);
		ueb.register(s);
		return s;
	}

	@Subscribe
	public void handle(Object object) {
		if (c.isInstance(object)) {
			l.add(c.cast(object));
			cdl.countDown();
			s.release();
		}
	}
}
