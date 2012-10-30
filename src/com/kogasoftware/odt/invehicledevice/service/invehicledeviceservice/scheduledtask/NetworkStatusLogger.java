package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.scheduledtask;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.common.base.Optional;

public class NetworkStatusLogger implements Runnable {
	private static final String TAG = NetworkStatusLogger.class.getSimpleName();
	public static final Integer RUN_INTERVAL_MILLIS = 2 * 60 * 1000;
	private final ConnectivityManager connectivityManager;
	private final Optional<TelephonyManager> optionalTelephonyManager;

	public NetworkStatusLogger(ConnectivityManager connectivityManager,
			Optional<TelephonyManager> optionalTelephonyManager) {
		this.connectivityManager = connectivityManager;
		this.optionalTelephonyManager = optionalTelephonyManager;
	}

	@Override
	public void run() {
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		for (NetworkInfo networkInfo : connectivityManager.getAllNetworkInfo()) {
			StringBuilder message = new StringBuilder();
			message.append(networkInfo.getTypeName());
			String subTypeName = networkInfo.getSubtypeName();
			if (!subTypeName.isEmpty()) {
				message.append("/" + subTypeName);
			}
			if (networkInfo.equals(activeNetworkInfo)) {
				message.append(" active");
			}
			if (networkInfo.isAvailable()) {
				message.append(" available");
			}
			if (networkInfo.isConnected()) {
				message.append(" connected");
			}
			if (networkInfo.isConnectedOrConnecting()) {
				message.append(" connectedOrConnecting");
			}
			if (networkInfo.isFailover()) {
				message.append(" failover");
			}
			if (networkInfo.isRoaming()) {
				message.append(" roaming");
			}
			String reason = networkInfo.getReason();
			if (reason != null) {
				message.append(" reason=" + reason);
			}
			Log.i(TAG, message.toString());
		}
		for (TelephonyManager telephonyManager : optionalTelephonyManager
				.asSet()) {
			StringBuilder message = new StringBuilder();
			message.append("networkOperatorName="
					+ telephonyManager.getNetworkOperatorName() + " dataState=");
			int dataState = telephonyManager.getDataState();
			switch (dataState) {
			case TelephonyManager.DATA_CONNECTED:
				message.append("DATA_CONNECTED");
				break;
			case TelephonyManager.DATA_CONNECTING:
				message.append("DATA_CONNECTING");
				break;
			case TelephonyManager.DATA_DISCONNECTED:
				message.append("DATA_DISCONNECTED");
				break;
			case TelephonyManager.DATA_SUSPENDED:
				message.append("DATA_SUSPENDED");
				break;
			default:
				message.append(dataState);
				break;
			}
			Log.i(TAG, message.toString());
		}
	}
}