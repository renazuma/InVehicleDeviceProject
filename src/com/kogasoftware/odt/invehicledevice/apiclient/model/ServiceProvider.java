package com.kogasoftware.odt.invehicledevice.apiclient.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.Model;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.ServiceProviderBase;

public class ServiceProvider extends ServiceProviderBase {
	private static final long serialVersionUID = 8456722052053624083L;

	@JsonProperty JsonNode reservationTimeLimit = Model.getObjectMapper().createObjectNode();
	@JsonProperty JsonNode timeBufferRatio = Model.getObjectMapper().createObjectNode();

	public static class ReservationTimeLimit {
		protected Integer operatorWeb = 0;
		protected Integer androidApp = 0;

		public Integer getAndroidApp() {
			return Model.wrapNull(androidApp);
		}

		public void setAndroidApp(Integer androidApp) {
			this.androidApp = Model.wrapNull(androidApp);
		}
	}
}
