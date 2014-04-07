package com.kogasoftware.odt.invehicledevice.ui.activity;

import android.app.Activity;
import android.content.Intent;

import com.kogasoftware.odt.invehicledevice.service.startupservice.StartupService;

public class StartupActivity extends Activity {
	@Override
	protected void onStart() {
		super.onStart();
		startService(new Intent(this, StartupService.class));
		finish();
	}
}
