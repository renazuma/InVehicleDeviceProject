package com.kogasoftware.odt.invehicledevice.testapiclient;

import java.io.Closeable;
import java.util.Date;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Demand;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Driver;
import com.kogasoftware.odt.invehicledevice.apiclient.model.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.UnmergedOperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ReservationUser;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnit;
import com.kogasoftware.odt.invehicledevice.apiclient.model.UnitAssignment;
import com.kogasoftware.odt.invehicledevice.apiclient.model.User;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Vehicle;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;

public class GenerateRecord implements Closeable {
	private final GenerateMaster master;
	private final InVehicleDeviceTestApiClient api;

	public GenerateRecord(GenerateMaster master) {
		this.master = master;
		this.api = master.getTestAPI();
	}

	public VehicleNotification createVehicleNotification(final String msg)
			throws Exception {
		SyncCall<VehicleNotification> sc = new SyncCall<VehicleNotification>() {
			@Override
			public int run() throws Exception {
				VehicleNotification obj = new VehicleNotification();
				obj.setBody(msg);
				return api.createVehicleNotification(obj, this);
			}
		};

		return sc.getResult();
	}

	public UnmergedOperationSchedule createOperationSchedule(
			final UnitAssignment unitAssignment, final Platform platform,
			final Date operationDate) throws Exception {
		SyncCall<UnmergedOperationSchedule> sc = new SyncCall<UnmergedOperationSchedule>() {
			@Override
			public int run() throws Exception {
				UnmergedOperationSchedule obj = new UnmergedOperationSchedule();
				obj.setPlatform(platform);

				return api.createOperationSchedule(obj, this);
			}
		};

		return sc.getResult();
	}

	public Reservation createReservation(final User user, final Demand demand,
			final UnitAssignment unitAssignment,
			final Platform departurePlatform,
			final UnmergedOperationSchedule departureSchedule,
			final Date departureTime, final Platform arrivalPlatform,
			final UnmergedOperationSchedule arrivalSchedule, final Date arrivalTime,
			final int payment) throws Exception {
		SyncCall<Reservation> sc = new SyncCall<Reservation>() {
			@Override
			public int run() throws Exception {
				Reservation obj = new Reservation();
				obj.setUserId(user.getId());
				obj.setFellowUsers(Lists.newArrayList(user));
				obj.setArrivalScheduleId(arrivalSchedule.getId());
				obj.setDepartureScheduleId(departureSchedule.getId());

				return api.createReservation(obj, this);
			}
		};

		return sc.getResult();
	}

	public Demand createDemand(final User user,
			final UnitAssignment unitAssignment,
			final Platform departurePlatform, final Date departureTime,
			final Platform arrivalPlatform, final Date arrivalTime,
			final int payment) throws Exception {
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
				obj.setServiceProviderId(master.getServiceProvider().getId());

				return api.createDemand(obj, this);
			}
		};

		return sc.getResult();
	}

	public UnitAssignment createUnitAssignment(final String name)
			throws Exception {
		SyncCall<UnitAssignment> sc = new SyncCall<UnitAssignment>() {
			@Override
			public int run() throws Exception {
				UnitAssignment obj = new UnitAssignment();
				obj.setName(name);
				obj.setServiceProviderId(master.getServiceProvider().getId());

				return api.createUnitAssignment(obj, this);
			}
		};

		return sc.getResult();
	}

	public ServiceUnit createServiceUnit(final Driver driver,
			final Vehicle vehicle, final InVehicleDevice inVehicleDevice,
			final UnitAssignment unitAssignment, final Date activatedAt)
			throws Exception {
		SyncCall<ServiceUnit> sc = new SyncCall<ServiceUnit>() {
			@Override
			public int run() throws Exception {
				ServiceUnit obj = new ServiceUnit();
				obj.setDriver(driver);
				obj.setVehicle(vehicle);
				obj.setInVehicleDevice(inVehicleDevice);
				obj.setUnitAssignment(unitAssignment);
				obj.setActivatedAt(activatedAt);
				obj.setServiceProviderId(master.getServiceProvider().getId());

				return api.createServiceUnit(obj, this);
			}
		};

		return sc.getResult();
	}

	public PassengerRecord createPassengerRecord(final Reservation res,
			final User user, final UnmergedOperationSchedule departureSchedule,
			final UnmergedOperationSchedule arrivalSchedule, final int payment)
			throws Exception {
		SyncCall<PassengerRecord> sc = new SyncCall<PassengerRecord>() {
			@Override
			public int run() throws Exception {
				PassengerRecord obj = new PassengerRecord();
				obj.setReservation(res);
				obj.setUser(user);
				obj.setArrivalOperationScheduleId(arrivalSchedule.getId());
				obj.setDepartureOperationScheduleId(departureSchedule.getId());

				return api.createPassengerRecord(obj, this);
			}
		};

		return sc.getResult();
	}

	public OperationRecord createOperationRecord(
			final UnmergedOperationSchedule operationSchedule, final Date arrivedAt,
			final boolean arrivedAtOffline, final Date departedAt,
			final boolean departedAtOffline) throws Exception {
		SyncCall<OperationRecord> sc = new SyncCall<OperationRecord>() {
			@Override
			public int run() throws Exception {
				OperationRecord obj = new OperationRecord();
				obj.setArrivedAt(arrivedAt);
				obj.setDepartedAt(departedAt);
				return api.createOperationRecord(obj, this);
			}
		};

		return sc.getResult();
	}

	public ReservationUser createReservationUser(
			final Reservation likeReservation, final User user)
			throws Exception {
		SyncCall<ReservationUser> sc = new SyncCall<ReservationUser>() {
			@Override
			public int run() throws Exception {
				ReservationUser obj = new ReservationUser();
				obj.setLikeReservationId(likeReservation.getId());
				obj.setUserId(user.getId());
				obj.setLikeReservationType(likeReservation.getClass()
						.getSimpleName());
				return api.createReservationUser(obj, this);
			}
		};

		return sc.getResult();
	}

	@Override
	public void close() {
		api.close();
		master.close();
	}
}
