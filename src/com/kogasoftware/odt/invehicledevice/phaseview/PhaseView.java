package com.kogasoftware.odt.invehicledevice.phaseview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.event.EnterFinishPhaseEvent;
import com.kogasoftware.odt.invehicledevice.event.EnterPlatformPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.Logic;
import com.kogasoftware.odt.invehicledevice.logic.LogicLoadThread;
import com.kogasoftware.odt.invehicledevice.modalview.ModalView;

public class PhaseView extends FrameLayout {
	private Logic logic = new Logic();

	public PhaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Subscribe
	public void enterDrivePhase(EnterDrivePhaseEvent event) {
		setVisibility(View.GONE);
	}

	@Subscribe
	public void enterFinishPhase(EnterFinishPhaseEvent event) {
		setVisibility(View.GONE);
	}

	@Subscribe
	public void enterPlatformPhase(EnterPlatformPhaseEvent event) {
		setVisibility(View.GONE);
	}

	protected Logic getLogic() {
		return logic;
	}

	protected void setContentView(int resourceId) {
		LayoutInflater layoutInflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.addView(layoutInflater.inflate(resourceId, null),
				new ModalView.LayoutParams(ModalView.LayoutParams.FILL_PARENT,
						ModalView.LayoutParams.FILL_PARENT));

		TypedArray typedArray = getContext().obtainStyledAttributes(
				new int[] { android.R.attr.background });
		int backgroundColor = typedArray.getColor(0, Color.WHITE);
		setBackgroundColor(backgroundColor);
	}

	@Subscribe
	public void setLogic(LogicLoadThread.CompleteEvent event) {
		this.logic = event.logic;
	}
}