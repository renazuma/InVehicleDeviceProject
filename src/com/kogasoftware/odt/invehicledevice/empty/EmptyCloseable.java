package com.kogasoftware.odt.invehicledevice.empty;

import java.io.Closeable;

/** FindBugs警告避け用クラス */
public class EmptyCloseable implements Closeable {
	@Override
	public void close() {
	}
}
