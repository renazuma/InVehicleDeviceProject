package com.kogasoftware.odt.invehicledevice.ui.modalview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.event.PauseCancelledEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.PauseEvent;

public class PauseModalView extends ModalView {
	public PauseModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.pause_modal_view);
		findViewById(R.id.pause_cancel_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						getCommonLogic().postEvent(new PauseCancelledEvent());
						hide();
					}
				});
	}

	@Subscribe
	public void show(PauseEvent event) {
		super.show();
	}
}
