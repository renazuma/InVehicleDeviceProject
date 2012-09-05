package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice.backgroundtask;

import org.mockito.ArgumentCaptor;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundtask.ServiceProviderReceiveThread;
import com.kogasoftware.odt.invehicledevice.service.logservice.UploadThread;
import com.kogasoftware.odt.webapi.model.ServiceProvider;

import android.content.Intent;
import android.test.AndroidTestCase;
import static org.mockito.Mockito.*;

public class ServiceProviderReceiveThreadTestCase extends AndroidTestCase {
	ServiceProviderReceiveThread sprt;
	InVehicleDeviceService s;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		s = mock(InVehicleDeviceService.class);
		sprt = new ServiceProviderReceiveThread(s);
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			if (sprt != null) {
				sprt.interrupt();
			}
		} finally {
			super.tearDown();
		}
	}

	public void testSendUpdateCredentialsBroadcast() {
		ServiceProvider sp = new ServiceProvider();
		sp.setLogAccessKeyIdAws("テストID");
		sp.setLogSecretAccessKeyAws("テストKey");
		
		sprt.sendUpdateCredentialsBroadcast(sp);

		ArgumentCaptor<Intent> intentArgument = ArgumentCaptor
				.forClass(Intent.class);
		verify(s).sendBroadcast(intentArgument.capture());
		Intent i = intentArgument.getValue();
		assertEquals(UploadThread.ACTION_UPDATE_CREDENTIALS, i.getAction());
		assertEquals(sp.getLogAccessKeyIdAws().get(),
				i.getStringExtra(SharedPreferencesKeys.AWS_ACCESS_KEY_ID));
		assertEquals(sp.getLogSecretAccessKeyAws().get(),
				i.getStringExtra(SharedPreferencesKeys.AWS_SECRET_ACCESS_KEY));
	}
}
