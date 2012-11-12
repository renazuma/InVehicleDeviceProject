package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.thread;

import java.util.concurrent.Semaphore;

import android.content.Intent;
import android.util.Log;

import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientException;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.Writer;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.service.logservice.UploadThread;

public class ServiceProviderReceiveThread extends Thread implements
		EventDispatcher.OnStartNewOperationListener {
	@SuppressWarnings("unused")
	private static final String TAG = ServiceProviderReceiveThread.class
			.getSimpleName();
	protected final InVehicleDeviceService service;
	protected final Semaphore serviceProviderReceiveSemaphore = new Semaphore(0);

	public ServiceProviderReceiveThread(InVehicleDeviceService service) {
		this.service = service;
	}

	@Override
	public void onStartNewOperation() {
		serviceProviderReceiveSemaphore.release();
	}

	private void onSucceed(final ServiceProvider serviceProvider) {
		service.getLocalStorage().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.serviceProvider = serviceProvider;
				localData.serviceProviderInitialized = true;
			}
		});

		sendUpdateCredentialsBroadcast(serviceProvider);
	}

	public void sendUpdateCredentialsBroadcast(ServiceProvider serviceProvider) {
		// ログアップロード用のサービスに認証情報を送信
		Log.i(TAG, "sendUpdateCredentialsBroadcast");
		Intent intent = new Intent(UploadThread.ACTION_UPDATE_CREDENTIALS);
		intent.putExtra(SharedPreferencesKeys.AWS_ACCESS_KEY_ID,
				serviceProvider.getLogAccessKeyIdAws().or(""));
		intent.putExtra(SharedPreferencesKeys.AWS_SECRET_ACCESS_KEY,
				serviceProvider.getLogSecretAccessKeyAws().or(""));
		service.sendBroadcast(intent);
	}

	private void receive() {
		service.getApiClient().getServiceProvider(
				new ApiClientCallback<ServiceProvider>() {
					@Override
					public void onException(int reqkey, ApiClientException ex) {
					}

					@Override
					public void onFailed(int reqkey, int statusCode,
							String response) {
					}

					@Override
					public void onSucceed(int reqkey, int statusCode,
							ServiceProvider serviceProvider) {
						ServiceProviderReceiveThread.this
								.onSucceed(serviceProvider);
					}
				});
	}

	@Override
	public void run() {
		try {
			while (true) {
				// 受信通知があるまで待つ
				serviceProviderReceiveSemaphore.acquire();
				receive();
			}
		} catch (InterruptedException e) {
			// 正常終了
		}
	}
}
