package com.kogasoftware.odt.invehicledevice.apiclient.model.base;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;
import com.kogasoftware.odt.apiclient.DefaultApiClient;
import com.kogasoftware.odt.apiclient.ApiClient.ResponseConverter;
import com.kogasoftware.odt.invehicledevice.apiclient.model.*;

@SuppressWarnings("unused")
public abstract class ServiceUnitBase extends Model {
	private static final long serialVersionUID = 376777390700395067L;
	public static final ResponseConverter<ServiceUnit> RESPONSE_CONVERTER = new ResponseConverter<ServiceUnit>() {
		@Override
		public ServiceUnit convert(byte[] rawResponse) throws JSONException {
			return parse(DefaultApiClient.parseJSONObject(rawResponse));
		}
	};
	public static final ResponseConverter<List<ServiceUnit>> LIST_RESPONSE_CONVERTER = new ResponseConverter<List<ServiceUnit>>() {
		@Override
		public List<ServiceUnit> convert(byte[] rawResponse) throws JSONException {
			return parseList(DefaultApiClient.parseJSONArray(rawResponse));
		}
	};
	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}
	@Override
	public void fill(JSONObject jsonObject) throws JSONException {
		setActivatedAt(parseOptionalDate(jsonObject, "activated_at"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setDriverId(parseOptionalInteger(jsonObject, "driver_id"));
		setId(parseInteger(jsonObject, "id"));
		setInVehicleDeviceId(parseOptionalInteger(jsonObject, "in_vehicle_device_id"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setUnitAssignmentId(parseOptionalInteger(jsonObject, "unit_assignment_id"));
		setVehicleId(parseOptionalInteger(jsonObject, "vehicle_id"));
		setDriver(Driver.parse(jsonObject, "driver"));
		setInVehicleDevice(InVehicleDevice.parse(jsonObject, "in_vehicle_device"));
		setOperationRecords(OperationRecord.parseList(jsonObject, "operation_records"));
		setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
		setUnitAssignment(UnitAssignment.parse(jsonObject, "unit_assignment"));
		setVehicle(Vehicle.parse(jsonObject, "vehicle"));

		setUpdatedAt(parseDate(jsonObject, "updated_at"));
	}

	public static Optional<ServiceUnit> parse(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return Optional.of(parse(jsonObject.getJSONObject(key)));
	}

	public static ServiceUnit parse(JSONObject jsonObject) throws JSONException {
		ServiceUnit model = new ServiceUnit();
		model.fill(jsonObject);
		return model;
	}

	public static LinkedList<ServiceUnit> parseList(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return new LinkedList<ServiceUnit>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<ServiceUnit> parseList(JSONArray jsonArray) throws JSONException {
		LinkedList<ServiceUnit> models = new LinkedList<ServiceUnit>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(parse(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	@Override
	protected JSONObject toJSONObject(Boolean recursive, Integer depth) throws JSONException {
		if (depth > MAX_RECURSE_DEPTH) {
			return new JSONObject();
		}
		Integer nextDepth = depth + 1;
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("activated_at", toJSON(getActivatedAt()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt()));
		jsonObject.put("driver_id", toJSON(getDriverId()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("in_vehicle_device_id", toJSON(getInVehicleDeviceId()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId()));
		jsonObject.put("unit_assignment_id", toJSON(getUnitAssignmentId()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("vehicle_id", toJSON(getVehicleId()));
		if (getDriver().isPresent()) {
			if (recursive) {
				jsonObject.put("driver", getDriver().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("driver_id", toJSON(getDriver().get().getId()));
			}
		}
		if (getInVehicleDevice().isPresent()) {
			if (recursive) {
				jsonObject.put("in_vehicle_device", getInVehicleDevice().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("in_vehicle_device_id", toJSON(getInVehicleDevice().get().getId()));
			}
		}
		if (getOperationRecords().size() > 0 && recursive) {
			jsonObject.put("operation_records", toJSON(getOperationRecords(), true, nextDepth));
		}
		if (getServiceProvider().isPresent()) {
			if (recursive) {
				jsonObject.put("service_provider", getServiceProvider().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
			}
		}
		if (getUnitAssignment().isPresent()) {
			if (recursive) {
				jsonObject.put("unit_assignment", getUnitAssignment().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("unit_assignment_id", toJSON(getUnitAssignment().get().getId()));
			}
		}
		if (getVehicle().isPresent()) {
			if (recursive) {
				jsonObject.put("vehicle", getVehicle().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("vehicle_id", toJSON(getVehicle().get().getId()));
			}
		}
		return jsonObject;
	}

	@Override
	public ServiceUnit cloneByJSON() throws JSONException {
		return parse(toJSONObject(true));
	}

	private Optional<Date> activatedAt = Optional.absent();

	public Optional<Date> getActivatedAt() {
		return wrapNull(activatedAt);
	}

	public void setActivatedAt(Optional<Date> activatedAt) {
		refreshUpdatedAt();
		this.activatedAt = wrapNull(activatedAt);
	}

	public void setActivatedAt(Date activatedAt) {
		setActivatedAt(Optional.fromNullable(activatedAt));
	}

	public void clearActivatedAt() {
		setActivatedAt(Optional.<Date>absent());
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		refreshUpdatedAt();
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> deletedAt = Optional.absent();

	public Optional<Date> getDeletedAt() {
		return wrapNull(deletedAt);
	}

	public void setDeletedAt(Optional<Date> deletedAt) {
		refreshUpdatedAt();
		this.deletedAt = wrapNull(deletedAt);
	}

	public void setDeletedAt(Date deletedAt) {
		setDeletedAt(Optional.fromNullable(deletedAt));
	}

	public void clearDeletedAt() {
		setDeletedAt(Optional.<Date>absent());
	}

	private Optional<Integer> driverId = Optional.absent();

	public Optional<Integer> getDriverId() {
		return wrapNull(driverId);
	}

	public void setDriverId(Optional<Integer> driverId) {
		refreshUpdatedAt();
		this.driverId = wrapNull(driverId);
	}

	public void setDriverId(Integer driverId) {
		setDriverId(Optional.fromNullable(driverId));
	}

	public void clearDriverId() {
		setDriverId(Optional.<Integer>absent());
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		refreshUpdatedAt();
		this.id = wrapNull(id);
	}

	private Optional<Integer> inVehicleDeviceId = Optional.absent();

	public Optional<Integer> getInVehicleDeviceId() {
		return wrapNull(inVehicleDeviceId);
	}

	public void setInVehicleDeviceId(Optional<Integer> inVehicleDeviceId) {
		refreshUpdatedAt();
		this.inVehicleDeviceId = wrapNull(inVehicleDeviceId);
	}

	public void setInVehicleDeviceId(Integer inVehicleDeviceId) {
		setInVehicleDeviceId(Optional.fromNullable(inVehicleDeviceId));
	}

	public void clearInVehicleDeviceId() {
		setInVehicleDeviceId(Optional.<Integer>absent());
	}

	private Optional<Integer> serviceProviderId = Optional.absent();

	public Optional<Integer> getServiceProviderId() {
		return wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Optional<Integer> serviceProviderId) {
		refreshUpdatedAt();
		this.serviceProviderId = wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Integer serviceProviderId) {
		setServiceProviderId(Optional.fromNullable(serviceProviderId));
	}

	public void clearServiceProviderId() {
		setServiceProviderId(Optional.<Integer>absent());
	}

	private Optional<Integer> unitAssignmentId = Optional.absent();

	public Optional<Integer> getUnitAssignmentId() {
		return wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Optional<Integer> unitAssignmentId) {
		refreshUpdatedAt();
		this.unitAssignmentId = wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Integer unitAssignmentId) {
		setUnitAssignmentId(Optional.fromNullable(unitAssignmentId));
	}

	public void clearUnitAssignmentId() {
		setUnitAssignmentId(Optional.<Integer>absent());
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Optional<Integer> vehicleId = Optional.absent();

	public Optional<Integer> getVehicleId() {
		return wrapNull(vehicleId);
	}

	public void setVehicleId(Optional<Integer> vehicleId) {
		refreshUpdatedAt();
		this.vehicleId = wrapNull(vehicleId);
	}

	public void setVehicleId(Integer vehicleId) {
		setVehicleId(Optional.fromNullable(vehicleId));
	}

	public void clearVehicleId() {
		setVehicleId(Optional.<Integer>absent());
	}

	private Optional<Driver> driver = Optional.<Driver>absent();

	public Optional<Driver> getDriver() {
		return wrapNull(driver);
	}

	public void setDriver(Optional<Driver> driver) {
		this.driver = wrapNull(driver);
	}

	public void setDriver(Driver driver) {
		setDriver(Optional.fromNullable(driver));
	}

	public void clearDriver() {
		setDriver(Optional.<Driver>absent());
	}

	private Optional<InVehicleDevice> inVehicleDevice = Optional.<InVehicleDevice>absent();

	public Optional<InVehicleDevice> getInVehicleDevice() {
		return wrapNull(inVehicleDevice);
	}

	public void setInVehicleDevice(Optional<InVehicleDevice> inVehicleDevice) {
		this.inVehicleDevice = wrapNull(inVehicleDevice);
	}

	public void setInVehicleDevice(InVehicleDevice inVehicleDevice) {
		setInVehicleDevice(Optional.fromNullable(inVehicleDevice));
	}

	public void clearInVehicleDevice() {
		setInVehicleDevice(Optional.<InVehicleDevice>absent());
	}

	private LinkedList<OperationRecord> operationRecords = new LinkedList<OperationRecord>();

	public List<OperationRecord> getOperationRecords() {
		return wrapNull(operationRecords);
	}

	public void setOperationRecords(Iterable<OperationRecord> operationRecords) {
		this.operationRecords = wrapNull(operationRecords);
	}

	public void clearOperationRecords() {
		setOperationRecords(new LinkedList<OperationRecord>());
	}

	private Optional<ServiceProvider> serviceProvider = Optional.<ServiceProvider>absent();

	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		setServiceProvider(Optional.fromNullable(serviceProvider));
	}

	public void clearServiceProvider() {
		setServiceProvider(Optional.<ServiceProvider>absent());
	}

	private Optional<UnitAssignment> unitAssignment = Optional.<UnitAssignment>absent();

	public Optional<UnitAssignment> getUnitAssignment() {
		return wrapNull(unitAssignment);
	}

	public void setUnitAssignment(Optional<UnitAssignment> unitAssignment) {
		this.unitAssignment = wrapNull(unitAssignment);
	}

	public void setUnitAssignment(UnitAssignment unitAssignment) {
		setUnitAssignment(Optional.fromNullable(unitAssignment));
	}

	public void clearUnitAssignment() {
		setUnitAssignment(Optional.<UnitAssignment>absent());
	}

	private Optional<Vehicle> vehicle = Optional.<Vehicle>absent();

	public Optional<Vehicle> getVehicle() {
		return wrapNull(vehicle);
	}

	public void setVehicle(Optional<Vehicle> vehicle) {
		this.vehicle = wrapNull(vehicle);
	}

	public void setVehicle(Vehicle vehicle) {
		setVehicle(Optional.fromNullable(vehicle));
	}

	public void clearVehicle() {
		setVehicle(Optional.<Vehicle>absent());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(activatedAt)
			.append(createdAt)
			.append(deletedAt)
			.append(driverId)
			.append(id)
			.append(inVehicleDeviceId)
			.append(serviceProviderId)
			.append(unitAssignmentId)
			.append(updatedAt)
			.append(vehicleId)
			.append(driver)
			.append(inVehicleDevice)
			.append(operationRecords)
			.append(serviceProvider)
			.append(unitAssignment)
			.append(vehicle)
			.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof ServiceUnitBase)) {
			return false;
		}
		ServiceUnitBase other = (ServiceUnitBase) obj;
		return new EqualsBuilder()
			.append(activatedAt, other.activatedAt)
			.append(createdAt, other.createdAt)
			.append(deletedAt, other.deletedAt)
			.append(driverId, other.driverId)
			.append(id, other.id)
			.append(inVehicleDeviceId, other.inVehicleDeviceId)
			.append(serviceProviderId, other.serviceProviderId)
			.append(unitAssignmentId, other.unitAssignmentId)
			.append(updatedAt, other.updatedAt)
			.append(vehicleId, other.vehicleId)
			.append(driver, other.driver)
			.append(inVehicleDevice, other.inVehicleDevice)
			.append(operationRecords, other.operationRecords)
			.append(serviceProvider, other.serviceProvider)
			.append(unitAssignment, other.unitAssignment)
			.append(vehicle, other.vehicle)
			.isEquals();
	}
}
