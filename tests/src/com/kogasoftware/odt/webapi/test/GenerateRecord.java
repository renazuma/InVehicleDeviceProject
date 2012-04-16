package com.kogasoftware.odt.webapi.test;

import java.util.Date;

import com.kogasoftware.odt.webapi.model.Demand;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.UnitAssignment;
import com.kogasoftware.odt.webapi.model.User;
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
	
	public OperationSchedule createOperationSchedule(final UnitAssignment unitAssignment, final Platform platform, final Date arrivalEstimate, final Date departureEstimate) throws Exception {
		SyncCall<OperationSchedule> sc = new SyncCall<OperationSchedule>() {
			@Override
			public int run() throws Exception {
				OperationSchedule obj = new OperationSchedule();
				obj.setUnitAssignment(unitAssignment);
				obj.setPlatform(platform);
				obj.setArrivalEstimate(arrivalEstimate);
				obj.setDepartureEstimate(departureEstimate);

				return api.createOperationSchedule(obj, this);
			}
		};

		return sc.getResult();		
	}

	public Reservation createReservation(final User user, final Demand demand, final UnitAssignment unitAssignment, 
			final Platform arrivalPlatform, final OperationSchedule arrivalSchedule, final Date arrivalTime, 
			final Platform departurePlatform, final OperationSchedule departureSchedule, final Date departureTime, final int payment) throws Exception {
		SyncCall<Reservation> sc = new SyncCall<Reservation>() {
			@Override
			public int run() throws Exception {
				Reservation obj = new Reservation();
				obj.setUser(user);
				obj.setDemand(demand);
				obj.setUnitAssignment(unitAssignment);
				obj.setArrivalPlatform(arrivalPlatform);
				obj.setArrivalTime(arrivalTime);
				obj.setArrivalSchedule(arrivalSchedule);
				obj.setDeparturePlatform(departurePlatform);
				obj.setDepartureTime(departureTime);
				obj.setDepartureSchedule(departureSchedule);
				if (payment > 0) {
					obj.setPayment(payment);
				}
				obj.setServiceProvider(master.getServiceProvider());
				obj.setOperator(master.getOperator());
				
				return api.createReservation(obj, this);
			}
		};

		return sc.getResult();		
	}
	
	public Demand createDemand(final User user, final UnitAssignment unitAssignment, 
			final Platform arrivalPlatform, final Date arrivalTime, 
			final Platform departurePlatform, final Date departureTime, final int payment) throws Exception {
		SyncCall<Demand> sc = new SyncCall<Demand>() {
			@Override
			public int run() throws Exception {
				Demand obj = new Demand();
				obj.setUser(user);
				obj.setUnitAssignment(unitAssignment);
				obj.setArrivalPlatform(arrivalPlatform);
				obj.setArrivalTime(arrivalTime);
				obj.setDeparturePlatform(departurePlatform);
				obj.setDepartureTime(departureTime);
				obj.setServiceProvider(master.getServiceProvider());
				
				return api.createDemand(obj, this);
			}
		};

		return sc.getResult();		
	}
	
	public UnitAssignment createUnitAssignment(final String name) throws Exception {
		SyncCall<UnitAssignment> sc = new SyncCall<UnitAssignment>() {
			@Override
			public int run() throws Exception {
				UnitAssignment obj = new UnitAssignment();
				obj.setName(name);
				obj.setServiceProvider(master.getServiceProvider());

				return api.createUnitAssignment(obj, this);
			}
		};

		return sc.getResult();		
	}

} 
