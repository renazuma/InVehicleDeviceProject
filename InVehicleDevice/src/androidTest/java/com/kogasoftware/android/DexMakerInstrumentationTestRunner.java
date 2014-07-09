package com.kogasoftware.android;

import com.google.android.apps.common.testing.testrunner.GoogleInstrumentationTestRunner;

import android.os.Bundle;

/**
 * "https://code.google.com/p/dexmaker/issues/detail?id=2"
 */
public class DexMakerInstrumentationTestRunner extends
		GoogleInstrumentationTestRunner {
	@Override
	public void onCreate(Bundle arguments) {
		super.onCreate(arguments);
		System.setProperty("dexmaker.dexcache", getTargetContext()
				.getCacheDir().toString());
	}
}
