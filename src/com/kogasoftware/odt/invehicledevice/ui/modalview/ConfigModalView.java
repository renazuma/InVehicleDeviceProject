package com.kogasoftware.odt.invehicledevice.ui.modalview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.event.PauseEvent;

/**
 * 運行状態の設定画面
 */
public class ConfigModalView extends ModalView {
	public static class ShowEvent {
	}

	public ConfigModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.config_modal_view);
		Button stopCheckButton = (Button) findViewById(R.id.stop_check_button);
		stopCheckButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				getCommonLogic().postEvent(
						new StopCheckModalView.ShowEvent());
			}
		});

		Button pauseButton = (Button) findViewById(R.id.pause_button);
		pauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				getCommonLogic().postEvent(new PauseEvent());
				hide();
			}
		});
	}

	@Subscribe
	public void show(ShowEvent event) {
		show();
	}
}
