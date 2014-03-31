package com.kogasoftware.odt.invehicledevice.testutil;

import android.os.Bundle;
import android.test.InstrumentationTestRunner;

public class DexMakerInstrumentationTestRunner extends
		InstrumentationTestRunner {
	@Override
	public void onCreate(Bundle arguments) {
		super.onCreate(arguments);
		System.setProperty("dexmaker.dexcache", getTargetContext()
				.getCacheDir().toString());
	}
}
