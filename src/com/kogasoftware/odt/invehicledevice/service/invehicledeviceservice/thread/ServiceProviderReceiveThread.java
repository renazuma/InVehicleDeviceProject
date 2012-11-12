package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.thread;

import java.util.concurrent.Semaphore;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientException;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.Writer;
import com.kogasoftware.odt.invehicledevice.service.logservice.ILogService;

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
		service.getLocalStorage().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.serviceProvider = serviceProvider;
				localData.serviceProviderInitialized = true;
			}
		});

		setLogUploadServerCredentials(serviceProvider);
	}

	public void setLogUploadServerCredentials(
			final ServiceProvider serviceProvider) {
		// ログアップロード用のサービスに認証情報を送信
		Log.i(TAG, "sendUpdateCredentialsBroadcast()");

		HandlerThread handlerThread = new HandlerThread(
				"setLogUploadServerCredentials") {
			final ServiceConnection serviceConnection = new ServiceConnection() {
				@Override
				public void onServiceConnected(ComponentName componentName,
						IBinder binder) {
					Log.i(TAG, "onServiceConnected()");
					ILogService logService = ILogService.Stub
							.asInterface(binder);
					try {
						logService.setServerUploadCredentials(serviceProvider
								.getLogAccessKeyIdAws().or(""), serviceProvider
								.getLogSecretAccessKeyAws().or(""));
					} catch (RemoteException e) {
						Log.w(TAG, e);
					}
					service.unbindService(this);
					quit();
				}

				@Override
				public void onServiceDisconnected(ComponentName componentName) {
					Log.i(TAG, "onServiceDisconnected()");
					quit();
				}
			};

			@Override
			protected void onLooperPrepared() {
				Log.i(TAG, "onLooperPrepared()");
				service.bindService(new Intent(ILogService.class.getName()),
						serviceConnection, Context.BIND_AUTO_CREATE);
			}
		};

		handlerThread.start();
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
