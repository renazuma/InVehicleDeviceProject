package com.kogasoftware.odt.invehicledevice.modal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;

public class StopCheckModal extends Modal {
	public static class ShowEvent {
	}

	public StopCheckModal(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.stop_check_modal);

		Button stopButton = (Button) findViewById(R.id.stop_button);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				getLogic().showStopModal();
			}
		});
	}

	@Subscribe
	public void show(ShowEvent event) {
		super.show();
	}
}
