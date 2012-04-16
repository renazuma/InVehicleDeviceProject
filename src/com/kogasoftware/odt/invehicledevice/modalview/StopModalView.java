package com.kogasoftware.odt.invehicledevice.modalview;

import android.content.Context;
import android.util.AttributeSet;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;

public class StopModalView extends ModalView {
	public static class ShowEvent {
	}

	public StopModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.stop_modal);
	}

	@Subscribe
	public void show(ShowEvent event) {
		super.show();
	}
}
