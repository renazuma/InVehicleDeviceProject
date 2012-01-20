package com.kogasoftware.odt.invehicledevice.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.TestCase;

import com.google.android.testing.nativedriver.client.AndroidNativeDriver;
import com.google.android.testing.nativedriver.client.AndroidNativeDriverBuilder;

public class NativeDriverTestCase extends TestCase {
	final private static Object serverStartedLock = new Object();
	final private static AtomicBoolean serverStarted = new AtomicBoolean(false);

	protected AndroidNativeDriver getDriver() {
		try {
			synchronized (serverStartedLock) {
				if (!serverStarted.get()) {
					// テスト対象を起動
					Runtime r = Runtime.getRuntime();
					Process p = r
							.exec("am instrument "
									+ " com.kogasoftware.odt.invehicledevice/com.google.android.testing.nativedriver.server.ServerInstrumentation");
					p.waitFor();
					// テスト対象の起動を待つ
					for (Integer retry = 0; retry < 100; ++retry) {
						Socket s = new Socket();
						try {
							s.connect(new InetSocketAddress("localhost", 54129));

							// no IOException
							serverStarted.set(true);
							break;
						} catch (IOException e) {
						} finally {
							s.close();
						}
						Thread.sleep(200);
					}
				}
				if (!serverStarted.get()) {
					throw new RuntimeException(
							"instrument server connection failed");
				}
				return new AndroidNativeDriverBuilder().withDefaultServer()
						.build();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
