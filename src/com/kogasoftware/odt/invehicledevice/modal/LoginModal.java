package com.kogasoftware.odt.invehicledevice.modal;

import android.content.Context;
import android.util.AttributeSet;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;

public class LoginModal extends Modal {
	public static class ShowEvent {
	}

	public LoginModal(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.pause_modal);
	}

	@Subscribe
	public void show(ShowEvent event) {
		super.show();
	}
}
