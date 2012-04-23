package com.kogasoftware.odt.invehicledevice.modalview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.event.UpdateOperationScheduleCompleteEvent;

public class ScheduleChangedModalView extends ModalView {
	public ScheduleChangedModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.schedule_changed_modal_view);
		Button scheduleConfirmButton = (Button) findViewById(R.id.schedule_confirm_button);
		scheduleConfirmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getLogic().showScheduleModalView();
				hide();
			}
		});
	}

	@Override
	public void hide() {
		super.hide();
	}

	@Subscribe
	public void show(UpdateOperationScheduleCompleteEvent event) {
		super.show();
	}
}
