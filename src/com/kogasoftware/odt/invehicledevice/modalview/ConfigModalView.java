package com.kogasoftware.odt.invehicledevice.modalview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;

public class ConfigModalView extends ModalView {
	public static class ShowEvent {
	}

	public ConfigModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setContentView(R.layout.config_modal);
		Button stopCheckButton = (Button) findViewById(R.id.stop_check_button);
		stopCheckButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				getLogic().showStopCheckModalView();
			}
		});

		Button pauseButton = (Button) findViewById(R.id.pause_button);
		pauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				getLogic().pause();
				hide();
			}
		});
	}

	@Subscribe
	public void show(ShowEvent event) {
		super.show();
	}
}
