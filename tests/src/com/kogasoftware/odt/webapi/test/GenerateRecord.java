package com.kogasoftware.odt.webapi.test;

import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class GenerateRecord {
	private GenerateMaster master;
	private WebTestAPI api;

	public GenerateRecord(GenerateMaster master) {
		this.master = master;
		this.api = master.getTestAPI();
	}
	
	public VehicleNotification createVehicleNotification(final String msg) throws Exception {
		SyncCall<VehicleNotification> sc = new SyncCall<VehicleNotification>() {
			@Override
			public int run() throws Exception {
				VehicleNotification obj = new VehicleNotification();
				obj.setInVehicleDevice(master.getInVehicleDevice());
				obj.setOperator(master.getOperator());
				obj.setBody(msg);

				return api.createVehicleNotification(obj, this);
			}
		};

		return sc.getResult();
	}
}
