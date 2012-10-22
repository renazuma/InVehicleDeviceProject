package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundtask;

import java.util.concurrent.Semaphore;

import android.content.Intent;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.service.logservice.UploadThread;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientException;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceProvider;

public class ServiceProviderReceiveThread extends Thread implements
		EventDispatcher.OnStartNewOperationListener {
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
		service.getLocalDataSource().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.serviceProvider = serviceProvider;
				localData.serviceProviderInitializedSign.release();
			}
		});

		sendUpdateCredentialsBroadcast(serviceProvider);
	}

	public void sendUpdateCredentialsBroadcast(ServiceProvider serviceProvider) {
		// ログアップロード用のサービスに認証情報を送信
		Intent intent = new Intent(UploadThread.ACTION_UPDATE_CREDENTIALS);
		intent.putExtra(SharedPreferencesKeys.AWS_ACCESS_KEY_ID,
				serviceProvider.getLogAccessKeyIdAws().or(""));
		intent.putExtra(SharedPreferencesKeys.AWS_SECRET_ACCESS_KEY,
				serviceProvider.getLogSecretAccessKeyAws().or(""));
		service.sendBroadcast(intent);
	}

	private void receive() {
		service.getRemoteDataSource().getServiceProvider(
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
		// 最初の一度は必ず受信する
		serviceProviderReceiveSemaphore.release();
		try {
			service.getEventDispatcher().addOnStartNewOperationListener(this);
			while (true) {
				// 受信通知があるまで待つ
				serviceProviderReceiveSemaphore.acquire();
				receive();
			}
		} catch (InterruptedException e) {
			// 正常終了
		} finally {
			service.getEventDispatcher().removeOnStartNewOperationListener(this);
		}
	}
}
