package com.kogasoftware.odt.invehicledevice.test.util;

import android.content.Context;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.ui.phaseview.PhaseView;

public class TestPhaseView extends PhaseView {
	public TestPhaseView(Context context, InVehicleDeviceService service) {
		super(context, service);
	}
}
