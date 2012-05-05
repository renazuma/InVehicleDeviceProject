package com.kogasoftware.odt.invehicledevice.ui.modalview;

import android.content.Context;
import android.util.AttributeSet;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.event.PauseEvent;

public class PauseModalView extends ModalView {
	public PauseModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.pause_modal_view);
		setCloseOnClick(R.id.pause_cancel_button);
	}

	@Subscribe
	public void show(PauseEvent event) {
		super.show();
	}
}
