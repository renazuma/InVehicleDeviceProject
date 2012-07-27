package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundthread;

import java.util.concurrent.Semaphore;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKey;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.ServiceProvider;

public class ServiceProviderReceiveThread extends Thread implements
		InVehicleDeviceService.OnStartNewOperationListener {
	private static final String TAG = ServiceProviderReceiveThread.class
			.getSimpleName();
	protected final InVehicleDeviceService service;
	protected final Semaphore serviceProviderReceiveSemaphore = new Semaphore(0);

	public ServiceProviderReceiveThread(InVehicleDeviceService service) {
		this.service = service;
		service.addOnStartNewOperationListener(this);
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
		// ログアップロード用のサービスに渡すのに、SharedPreferencesを使う
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(service).edit();
		editor.putString(SharedPreferencesKey.AWS_ACCESS_KEY_ID, "");
		editor.putString(SharedPreferencesKey.AWS_SECRET_ACCESS_KEY, "");
		editor.commit();
	}

	private void receive() {
		service.getDataSource().getServiceProvider(
				new WebAPICallback<ServiceProvider>() {
					@Override
					public void onException(int reqkey, WebAPIException ex) {
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
			while (!Thread.currentThread().isInterrupted()) {
				// 受信通知があるまで待つ
				serviceProviderReceiveSemaphore.acquire();
				receive();
				Thread.sleep(10 * 1000);
			}
		} catch (InterruptedException e) {
			// 正常終了
		}
	}
}
