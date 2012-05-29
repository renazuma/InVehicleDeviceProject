package com.kogasoftware.odt.invehicledevice.ui.modalview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterPlatformPhaseEvent;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;

public class ArrivalCheckModalView extends ModalView {
	public static class ShowEvent {
	}

	public ArrivalCheckModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.arrival_check_modal_view);
		setCloseOnClick(R.id.arrival_check_close_button);
	}

	@Subscribe
	public void show(ShowEvent event) {
		Button arrivalButton = (Button) findViewById(R.id.arrival_button);
		for (OperationSchedule operationSchedule : getCommonLogic().getCurrentOperationSchedule().asSet()) {
			for (Platform platform : operationSchedule.getPlatform().asSet()) {
				TextView commentTextView = (TextView) findViewById(R.id.arrival_check_comment_text_view);
				commentTextView.setText(platform.getName() + "に到着します。");
			}
		}
		
		arrivalButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				getCommonLogic().postEvent(new EnterPlatformPhaseEvent());
				hide();
			}
		});
		super.show();
	}
}
