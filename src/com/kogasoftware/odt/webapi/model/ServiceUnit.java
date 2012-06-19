package com.kogasoftware.odt.webapi.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public class ServiceUnit extends Model {
	private static final long serialVersionUID = 6315044430545442817L;

	public ServiceUnit() {
	}

	public ServiceUnit(JSONObject jsonObject) throws JSONException {
		try {
			fillMembers(this, jsonObject);
		} catch (ParseException e) {
			throw new JSONException(e.toString() + "\n" + ExceptionUtils.getStackTrace(e));
		}
	}

	public static void fillMembers(ServiceUnit model, JSONObject jsonObject) throws JSONException, ParseException {
		model.setActivatedAt(parseOptionalDate(jsonObject, "activated_at"));
		model.setCreatedAt(parseDate(jsonObject, "created_at"));
		model.setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		model.setDriverId(parseOptionalInteger(jsonObject, "driver_id"));
		model.setId(parseInteger(jsonObject, "id"));
		model.setInVehicleDeviceId(parseOptionalInteger(jsonObject, "in_vehicle_device_id"));
		model.setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		model.setUnitAssignmentId(parseOptionalInteger(jsonObject, "unit_assignment_id"));
		model.setUpdatedAt(parseDate(jsonObject, "updated_at"));
		model.setVehicleId(parseOptionalInteger(jsonObject, "vehicle_id"));
		model.setDriver(Driver.parse(jsonObject, "driver"));
		model.setInVehicleDevice(InVehicleDevice.parse(jsonObject, "in_vehicle_device"));
		model.setOperationRecords(OperationRecord.parseList(jsonObject, "operation_records"));
		model.setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
		model.setUnitAssignment(UnitAssignment.parse(jsonObject, "unit_assignment"));
		model.setVehicle(Vehicle.parse(jsonObject, "vehicle"));
	}

	public static Optional<ServiceUnit> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<ServiceUnit> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.of(new ServiceUnit(jsonObject));
	}

	public static LinkedList<ServiceUnit> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<ServiceUnit>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<ServiceUnit> parseList(JSONArray jsonArray) throws JSONException, ParseException {
		LinkedList<ServiceUnit> models = new LinkedList<ServiceUnit>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new ServiceUnit(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	@Override
	protected JSONObject toJSONObject(Boolean recursive, Integer depth) throws JSONException {
		depth++;
		if (depth > MAX_RECURSE_DEPTH) {
			return new JSONObject();
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("activated_at", toJSON(getActivatedAt().orNull()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
		jsonObject.put("driver_id", toJSON(getDriverId().orNull()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("in_vehicle_device_id", toJSON(getInVehicleDeviceId().orNull()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("unit_assignment_id", toJSON(getUnitAssignmentId().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("vehicle_id", toJSON(getVehicleId().orNull()));
		if (getDriver().isPresent()) {
			if (recursive) {
				jsonObject.put("driver", getDriver().get().toJSONObject(true, depth));
			} else {
				jsonObject.put("driver_id", toJSON(getDriver().get().getId()));
			}
		}
		if (getInVehicleDevice().isPresent()) {
			if (recursive) {
				jsonObject.put("in_vehicle_device", getInVehicleDevice().get().toJSONObject(true, depth));
			} else {
				jsonObject.put("in_vehicle_device_id", toJSON(getInVehicleDevice().get().getId()));
			}
		}
		if (getOperationRecords().size() > 0 && recursive) {
			jsonObject.put("operation_records", toJSON(getOperationRecords(), true, depth));
		}
		if (getServiceProvider().isPresent()) {
			if (recursive) {
				jsonObject.put("service_provider", getServiceProvider().get().toJSONObject(true, depth));
			} else {
				jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
			}
		}
		if (getUnitAssignment().isPresent()) {
			if (recursive) {
				jsonObject.put("unit_assignment", getUnitAssignment().get().toJSONObject(true, depth));
			} else {
				jsonObject.put("unit_assignment_id", toJSON(getUnitAssignment().get().getId()));
			}
		}
		if (getVehicle().isPresent()) {
			if (recursive) {
				jsonObject.put("vehicle", getVehicle().get().toJSONObject(true, depth));
			} else {
				jsonObject.put("vehicle_id", toJSON(getVehicle().get().getId()));
			}
		}
		return jsonObject;
	}

	private void writeObject(ObjectOutputStream objectOutputStream)
			throws IOException {
		try {
			objectOutputStream.writeObject(toJSONObject(true).toString());
		} catch (JSONException e) {
			throw new IOException(e);
		}
	}

	private void readObject(ObjectInputStream objectInputStream)
		throws IOException, ClassNotFoundException {
		Object object = objectInputStream.readObject();
		if (!(object instanceof String)) {
			return;
		}
		String jsonString = (String) object;
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			fillMembers(this, jsonObject);
		} catch (JSONException e) {
			throw new IOException(e);
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	@Override
	public ServiceUnit cloneByJSON() throws JSONException {
		return new ServiceUnit(toJSONObject(true));
	}

	private Optional<Date> activatedAt = Optional.absent();

	public Optional<Date> getActivatedAt() {
		return wrapNull(activatedAt);
	}

	public void setActivatedAt(Optional<Date> activatedAt) {
		this.activatedAt = wrapNull(activatedAt);
	}

	public void setActivatedAt(Date activatedAt) {
		this.activatedAt = Optional.fromNullable(activatedAt);
	}

	public void clearActivatedAt() {
		this.activatedAt = Optional.absent();
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> deletedAt = Optional.absent();

	public Optional<Date> getDeletedAt() {
		return wrapNull(deletedAt);
	}

	public void setDeletedAt(Optional<Date> deletedAt) {
		this.deletedAt = wrapNull(deletedAt);
	}

	public void setDeletedAt(Date deletedAt) {
		this.deletedAt = Optional.fromNullable(deletedAt);
	}

	public void clearDeletedAt() {
		this.deletedAt = Optional.absent();
	}

	private Optional<Integer> driverId = Optional.absent();

	public Optional<Integer> getDriverId() {
		return wrapNull(driverId);
	}

	public void setDriverId(Optional<Integer> driverId) {
		this.driverId = wrapNull(driverId);
	}

	public void setDriverId(Integer driverId) {
		this.driverId = Optional.fromNullable(driverId);
	}

	public void clearDriverId() {
		this.driverId = Optional.absent();
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	private Optional<Integer> inVehicleDeviceId = Optional.absent();

	public Optional<Integer> getInVehicleDeviceId() {
		return wrapNull(inVehicleDeviceId);
	}

	public void setInVehicleDeviceId(Optional<Integer> inVehicleDeviceId) {
		this.inVehicleDeviceId = wrapNull(inVehicleDeviceId);
	}

	public void setInVehicleDeviceId(Integer inVehicleDeviceId) {
		this.inVehicleDeviceId = Optional.fromNullable(inVehicleDeviceId);
	}

	public void clearInVehicleDeviceId() {
		this.inVehicleDeviceId = Optional.absent();
	}

	private Optional<Integer> serviceProviderId = Optional.absent();

	public Optional<Integer> getServiceProviderId() {
		return wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Optional<Integer> serviceProviderId) {
		this.serviceProviderId = wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Integer serviceProviderId) {
		this.serviceProviderId = Optional.fromNullable(serviceProviderId);
	}

	public void clearServiceProviderId() {
		this.serviceProviderId = Optional.absent();
	}

	private Optional<Integer> unitAssignmentId = Optional.absent();

	public Optional<Integer> getUnitAssignmentId() {
		return wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Optional<Integer> unitAssignmentId) {
		this.unitAssignmentId = wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Integer unitAssignmentId) {
		this.unitAssignmentId = Optional.fromNullable(unitAssignmentId);
	}

	public void clearUnitAssignmentId() {
		this.unitAssignmentId = Optional.absent();
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
		this.vehicleId = wrapNull(vehicleId);
	}

	public void setVehicleId(Integer vehicleId) {
		this.vehicleId = Optional.fromNullable(vehicleId);
	}

	public void clearVehicleId() {
		this.vehicleId = Optional.absent();
	}

	private Optional<Driver> driver = Optional.absent();

	public Optional<Driver> getDriver() {
		return wrapNull(driver);
	}

	public void setDriver(Optional<Driver> driver) {
		this.driver = wrapNull(driver);
	}

	public void setDriver(Driver driver) {
		this.driver = Optional.fromNullable(driver);
	}

	public void clearDriver() {
		this.driver = Optional.absent();
	}

	private Optional<InVehicleDevice> inVehicleDevice = Optional.absent();

	public Optional<InVehicleDevice> getInVehicleDevice() {
		return wrapNull(inVehicleDevice);
	}

	public void setInVehicleDevice(Optional<InVehicleDevice> inVehicleDevice) {
		this.inVehicleDevice = wrapNull(inVehicleDevice);
	}

	public void setInVehicleDevice(InVehicleDevice inVehicleDevice) {
		this.inVehicleDevice = Optional.fromNullable(inVehicleDevice);
	}

	public void clearInVehicleDevice() {
		this.inVehicleDevice = Optional.absent();
	}

	private LinkedList<OperationRecord> operationRecords = new LinkedList<OperationRecord>();

	public List<OperationRecord> getOperationRecords() {
		return new LinkedList<OperationRecord>(wrapNull(operationRecords));
	}

	public void setOperationRecords(List<OperationRecord> operationRecords) {
		this.operationRecords = new LinkedList<OperationRecord>(wrapNull(operationRecords));
	}

	public void clearOperationRecords() {
		this.operationRecords = new LinkedList<OperationRecord>();
	}

	private Optional<ServiceProvider> serviceProvider = Optional.absent();

	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = Optional.fromNullable(serviceProvider);
	}

	public void clearServiceProvider() {
		this.serviceProvider = Optional.absent();
	}

	private Optional<UnitAssignment> unitAssignment = Optional.absent();

	public Optional<UnitAssignment> getUnitAssignment() {
		return wrapNull(unitAssignment);
	}

	public void setUnitAssignment(Optional<UnitAssignment> unitAssignment) {
		this.unitAssignment = wrapNull(unitAssignment);
	}

	public void setUnitAssignment(UnitAssignment unitAssignment) {
		this.unitAssignment = Optional.fromNullable(unitAssignment);
	}

	public void clearUnitAssignment() {
		this.unitAssignment = Optional.absent();
	}

	private Optional<Vehicle> vehicle = Optional.absent();

	public Optional<Vehicle> getVehicle() {
		return wrapNull(vehicle);
	}

	public void setVehicle(Optional<Vehicle> vehicle) {
		this.vehicle = wrapNull(vehicle);
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = Optional.fromNullable(vehicle);
	}

	public void clearVehicle() {
		this.vehicle = Optional.absent();
	}
}
