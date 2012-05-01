package com.kogasoftware.odt.invehicledevice.ui.modalview;

import android.content.Context;
import android.util.AttributeSet;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;

public class LoginModalView extends ModalView {
	public static class ShowEvent {
	}

	public LoginModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.pause_modal_view);
	}

	@Subscribe
	public void show(ShowEvent event) {
		super.show();
	}
}
