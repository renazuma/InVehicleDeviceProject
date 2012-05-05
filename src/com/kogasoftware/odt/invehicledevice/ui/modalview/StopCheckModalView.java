package com.kogasoftware.odt.invehicledevice.ui.modalview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.event.StopEvent;

public class StopCheckModalView extends ModalView {
	public static class ShowEvent {
	}

	public StopCheckModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.stop_check_modal_view);
		setCloseOnClick(R.id.stop_check_close_button);

		Button stopButton = (Button) findViewById(R.id.stop_button);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				getCommonLogic().postEvent(new StopEvent());
			}
		});
	}

	@Subscribe
	public void show(ShowEvent event) {
		super.show();
	}
}
