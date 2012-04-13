package com.kogasoftware.odt.invehicledevice.modal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;

public class PauseModal extends Modal {
	public static class ShowEvent {
	}

	public PauseModal(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.pause_modal);
		findViewById(R.id.pause_cancel_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						getLogic().cancelPause();
					}
				});
	}

	@Subscribe
	public void show(ShowEvent event) {
		super.show();
	}
}
