package com.kogasoftware.odt.invehicledevice.ui.phaseview;

import android.content.Context;
import android.view.View;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;

public class FinishPhaseView extends PhaseView {
	public FinishPhaseView(Context context, InVehicleDeviceService service) {
		super(context, service);
		setContentView(R.layout.finish_phase_view);
	}

	@Override
	public void onEnterFinishPhase() {
		setVisibility(View.VISIBLE);
	}
}
