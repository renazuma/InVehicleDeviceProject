package com.kogasoftware.odt.invehicledevice.modal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;

public class StartCheckModal extends Modal {
	public static class ShowEvent {
	}

	public StartCheckModal(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.start_check_modal);

		Button startButton = (Button) findViewById(R.id.start_button);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				getLogic().enterDriveStatus();
			}
		});
	}

	@Subscribe
	public void show(ShowEvent event) {
		super.show();
	}
}
