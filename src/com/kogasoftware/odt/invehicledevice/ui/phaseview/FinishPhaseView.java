package com.kogasoftware.odt.invehicledevice.ui.phaseview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterFinishPhaseEvent;

public class FinishPhaseView extends PhaseView {
	public FinishPhaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.finish_phase_view);
	}

	@Override
	public void enterFinishPhase(EnterFinishPhaseEvent event) {
		setVisibility(View.VISIBLE);
	}
}
