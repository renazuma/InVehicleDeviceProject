package com.kogasoftware.odt.invehicledevice.modal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;

public class ConfigModal extends Modal {
	public static class ShowEvent {
	}

	public ConfigModal(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setContentView(R.layout.config_modal);
		Button stopCheckButton = (Button) findViewById(R.id.stop_check_button);
		stopCheckButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				getLogic().showStopCheckModal();
			}
		});

		Button pauseButton = (Button) findViewById(R.id.pause_button);
		pauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				getLogic().showPauseModal();
				hide();
			}
		});
	}

	@Subscribe
	public void show(ShowEvent event) {
		super.show();
	}
}
