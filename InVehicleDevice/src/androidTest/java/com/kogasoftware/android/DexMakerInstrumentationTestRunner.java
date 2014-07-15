package com.kogasoftware.android;

import android.os.Bundle;
import android.test.InstrumentationTestRunner;

/**
 * "https://code.google.com/p/dexmaker/issues/detail?id=2"
 */
public class DexMakerInstrumentationTestRunner
		extends
			InstrumentationTestRunner {
	@Override
	public void onCreate(Bundle arguments) {
		super.onCreate(arguments);
		System.setProperty("dexmaker.dexcache", getTargetContext()
				.getCacheDir().toString());
	}
}
