package com.kogasoftware.odt.webapi.model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public class ServiceUnit extends Model {
	private static final long serialVersionUID = 1044403248402443893L;

	public ServiceUnit() {
	}

	public ServiceUnit(JSONObject jsonObject) throws JSONException, ParseException {
		setActivatedAt(parseOptionalDate(jsonObject, "activated_at"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setId(parseInteger(jsonObject, "id"));
		setUnitAssignmentId(parseOptionalInteger(jsonObject, "unit_assignment_id"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setVehicleId(parseInteger(jsonObject, "vehicle_id"));
		setDriver(Driver.parse(jsonObject, "driver"));
		if (getDriver().isPresent()) {
			setDriverId(getDriver().get().getId());
		}
		setInVehicleDevice(InVehicleDevice.parse(jsonObject, "in_vehicle_device"));
		if (getInVehicleDevice().isPresent()) {
			setInVehicleDeviceId(getInVehicleDevice().get().getId());
		}
		setOperationRecords(OperationRecord.parseList(jsonObject, "operation_records"));
	}

	public static Optional<ServiceUnit> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<ServiceUnit>absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<ServiceUnit> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.<ServiceUnit>of(new ServiceUnit(jsonObject));
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
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("activated_at", toJSON(getActivatedAt().orNull()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("unit_assignment_id", toJSON(getUnitAssignmentId().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("vehicle_id", toJSON(getVehicleId()));
		jsonObject.put("driver", toJSON(getDriver()));
		if (getDriver().isPresent()) {
			jsonObject.put("driver_id", toJSON(getDriver().get().getId()));
		}
		jsonObject.put("in_vehicle_device", toJSON(getInVehicleDevice()));
		if (getInVehicleDevice().isPresent()) {
			jsonObject.put("in_vehicle_device_id", toJSON(getInVehicleDevice().get().getId()));
		}
		jsonObject.put("operation_records", toJSON(getOperationRecords()));
		return jsonObject;
	}

	private Optional<Date> activatedAt = Optional.<Date>absent();

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
		this.activatedAt = Optional.<Date>absent();
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> deletedAt = Optional.<Date>absent();

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
		this.deletedAt = Optional.<Date>absent();
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	private Optional<Integer> unitAssignmentId = Optional.<Integer>absent();

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
		this.unitAssignmentId = Optional.<Integer>absent();
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Integer vehicleId = 0;

	public Integer getVehicleId() {
		return wrapNull(vehicleId);
	}

	public void setVehicleId(Integer vehicleId) {
		this.vehicleId = wrapNull(vehicleId);
	}

	private Optional<Driver> driver = Optional.<Driver>absent();

	public Optional<Driver> getDriver() {
		return wrapNull(driver);
	}

	public void setDriver(Optional<Driver> driver) {
		this.driver = wrapNull(driver);
	}

	public void setDriver(Driver driver) {
		this.driver = Optional.<Driver>fromNullable(driver);
	}

	public void clearDriver() {
		this.driver = Optional.<Driver>absent();
	}

	private Optional<InVehicleDevice> inVehicleDevice = Optional.<InVehicleDevice>absent();

	public Optional<InVehicleDevice> getInVehicleDevice() {
		return wrapNull(inVehicleDevice);
	}

	public void setInVehicleDevice(Optional<InVehicleDevice> inVehicleDevice) {
		this.inVehicleDevice = wrapNull(inVehicleDevice);
	}

	public void setInVehicleDevice(InVehicleDevice inVehicleDevice) {
		this.inVehicleDevice = Optional.<InVehicleDevice>fromNullable(inVehicleDevice);
	}

	public void clearInVehicleDevice() {
		this.inVehicleDevice = Optional.<InVehicleDevice>absent();
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
}
