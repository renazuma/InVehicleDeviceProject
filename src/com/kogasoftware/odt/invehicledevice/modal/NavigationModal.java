package com.kogasoftware.odt.invehicledevice.modal;

import android.app.Activity;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.navigation.NavigationView;


public class NavigationModal extends Modal {
	final NavigationView navigationView;

	public NavigationModal(Activity activity) {
		super(activity, R.layout.navigation_modal);
		setId(R.id.navigation_modal);
		navigationView = (NavigationView)findViewById(R.id.navigation_view);
	}

	@Override
	public void show() {
		super.show();
	}

	public void onResumeActivity() {
		navigationView.onResumeActivity();
	}

	public void onPauseActivity() {
		navigationView.onPauseActivity();
	}
}
