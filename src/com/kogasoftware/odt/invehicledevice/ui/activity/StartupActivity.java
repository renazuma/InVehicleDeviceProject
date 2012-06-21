package com.kogasoftware.odt.invehicledevice.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.kogasoftware.odt.invehicledevice.service.startupservice.StartupService;


public class StartupActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		finish(); // 必ずfinishする
		startService(new Intent(this, StartupService.class));
	}
}
