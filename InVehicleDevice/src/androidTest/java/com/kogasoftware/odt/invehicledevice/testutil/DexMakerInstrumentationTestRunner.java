package com.kogasoftware.odt.invehicledevice.testutil;

import com.google.android.apps.common.testing.testrunner.GoogleInstrumentationTestRunner;

import android.os.Bundle;

public class DexMakerInstrumentationTestRunner extends
		GoogleInstrumentationTestRunner {
	@Override
	public void onCreate(Bundle arguments) {
		super.onCreate(arguments);
		System.setProperty("dexmaker.dexcache", getTargetContext()
				.getCacheDir().toString());
	}
}
