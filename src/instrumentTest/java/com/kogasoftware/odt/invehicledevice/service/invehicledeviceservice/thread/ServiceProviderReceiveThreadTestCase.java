package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice.thread;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import android.content.Intent;
import android.test.AndroidTestCase;

import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.invehicledevice.apiclient.EmptyInVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.thread.ServiceProviderReceiveThread;

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

	public void testRun() throws Exception {
		InVehicleDeviceApiClient ds = new EmptyInVehicleDeviceApiClient() {
			@Override
			public int getServiceProvider(
					ApiClientCallback<ServiceProvider> callback) {
				ServiceProvider sp = new ServiceProvider();
				callback.onSucceed(0, 200, sp);
				return 0;
			}
		};
		when(s.getApiClient()).thenReturn(ds);
		when(s.getLocalStorage()).thenReturn(new LocalStorage());

		Integer m = 1000;
		sprt.start();
		Thread.sleep(m);
		verify(s, Mockito.times(1)).sendBroadcast(Mockito.<Intent> any());

		Thread.sleep(m);
		verify(s, Mockito.times(1)).sendBroadcast(Mockito.<Intent> any());

		ArgumentCaptor<Intent> intentArgument = ArgumentCaptor
				.forClass(Intent.class);
		sprt.onStartNewOperation();
		Thread.sleep(m);
		verify(s, Mockito.times(2)).sendBroadcast(intentArgument.capture());
		// assertEquals(LogService.ACTION_UPDATE_CREDENTIALS, intentArgument
		// .getValue().getAction());

		sprt.interrupt();
		sprt.join(m);
		assertFalse(sprt.isAlive());
	}

	public void testSendUpdateCredentialsBroadcast() {
		ServiceProvider sp = new ServiceProvider();
		sp.setLogAccessKeyIdAws("テストID");
		sp.setLogSecretAccessKeyAws("テストKey");

		// sprt.sendUpdateCredentialsBroadcast(sp);

		ArgumentCaptor<Intent> intentArgument = ArgumentCaptor
				.forClass(Intent.class);
		verify(s).sendBroadcast(intentArgument.capture());
		Intent i = intentArgument.getValue();
		// assertEquals(UploadThread.ACTION_UPDATE_CREDENTIALS, i.getAction());
		assertEquals(sp.getLogAccessKeyIdAws().get(),
				i.getStringExtra(SharedPreferencesKeys.AWS_ACCESS_KEY_ID));
		assertEquals(sp.getLogSecretAccessKeyAws().get(),
				i.getStringExtra(SharedPreferencesKeys.AWS_SECRET_ACCESS_KEY));
	}
}
