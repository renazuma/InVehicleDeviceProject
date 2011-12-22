package com.kogasoftware.odt.invehicledevice;

import com.google.common.base.Preconditions;

public class Mutablizer<T> {
	private T t = null;

	public void set(T t) {
		this.t = t;
	}

	public T get() {
		return t;
	}

	public Mutablizer(T t) {
		Preconditions.checkNotNull(t);
		this.t = t;
	}
}
