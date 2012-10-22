package com.kogasoftware.odt.invehicledevice.apiclient.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.kogasoftware.odt.invehicledevice.apiclient.model.base.Model;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.ServiceProviderBase;

public class ServiceProvider extends ServiceProviderBase {
	private static final long serialVersionUID = 8456722052053624083L;

	public static class ReservationTimeLimit {
		protected Integer operatorWeb = 0;
		protected Integer androidApp = 0;

		public Integer getAndroidApp() {
			return Model.wrapNull(androidApp);
		}

		public void setAndroidApp(Integer androidApp) {
			this.androidApp = Model.wrapNull(androidApp);
		}

		public Integer getOperatorWeb() {
			return Model.wrapNull(operatorWeb);
		}

		public void setOperatorWeb(Integer operatorWeb) {
			this.operatorWeb = Model.wrapNull(operatorWeb);
		}
	}

	public static ReservationTimeLimit parseReservationTimeLimit(
			ServiceProvider serviceProvider) throws JSONException {
		JSONObject jsonObject = new JSONObject(
				serviceProvider.getReservationTimeLimit());
		ReservationTimeLimit reservationTimeLimit = new ReservationTimeLimit();
		reservationTimeLimit.setAndroidApp(jsonObject.getInt("android_app"));
		reservationTimeLimit.setOperatorWeb(jsonObject.getInt("operator_web"));
		return reservationTimeLimit;
	}
}
