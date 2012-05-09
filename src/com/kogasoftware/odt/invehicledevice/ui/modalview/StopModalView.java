package com.kogasoftware.odt.invehicledevice.ui.modalview;

import android.content.Context;
import android.util.AttributeSet;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.event.StopEvent;

public class StopModalView extends ModalView {
	public StopModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.stop_modal_view);
	}

	@Subscribe
	public void show(StopEvent event) {
		super.show();
	}
}
