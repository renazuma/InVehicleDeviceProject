package com.kogasoftware.odt.invehicledevice.test.util;

import android.content.Context;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ModalView;

public class TestModalView extends ModalView {
	public TestModalView(Context context, InVehicleDeviceService service) {
		super(context, service);
	}
}
