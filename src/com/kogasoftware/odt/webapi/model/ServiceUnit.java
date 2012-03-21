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
import com.kogasoftware.odt.webapi.WebAPI;

public class ServiceUnit extends Model {
	private static final long serialVersionUID = 4316996978330344796L;
	public static final String JSON_NAME = "service_unit";
	public static final String CONTROLLER_NAME = "service_units";

	public static class URL {
		public static final String ROOT = "/" + CONTROLLER_NAME;
		public static final String CREATE = "/" + CONTROLLER_NAME + "/create";
		public static final String DESTROY = "/" + CONTROLLER_NAME + "/destroy";
		public static final String EDIT = "/" + CONTROLLER_NAME + "/edit";
		public static final String INDEX = "/" + CONTROLLER_NAME + "/index";
		public static final String NEW = "/" + CONTROLLER_NAME + "/new";
		public static final String SHOW = "/" + CONTROLLER_NAME + "/show";
		public static final String UPDATE = "/" + CONTROLLER_NAME + "/update";
	}

	public ServiceUnit() {
	}

	public ServiceUnit(JSONObject jsonObject) throws JSONException, ParseException {
		setActivatedAt(parseOptionalDate(jsonObject, "activated_at"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setDriverId(parseLong(jsonObject, "driver_id"));
		setId(parseLong(jsonObject, "id"));
		setInVehicleDeviceId(parseOptionalLong(jsonObject, "in_vehicle_device_id"));
		setUnitAssignmentId(parseOptionalLong(jsonObject, "unit_assignment_id"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setVehicleId(parseLong(jsonObject, "vehicle_id"));
		setInVehicleDevice(new InVehicleDevice(jsonObject.getJSONObject("in_vehicle_device")));
		if (getInVehicleDevice().isPresent()) {
			setInVehicleDeviceId(getInVehicleDevice().get().getId());
		}
	}

	public static Optional<ServiceUnit> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<ServiceUnit>absent();
		}
		return Optional.<ServiceUnit>of(new ServiceUnit(jsonObject.getJSONObject(key)));
	}

	public static List<ServiceUnit> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<ServiceUnit>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		List<ServiceUnit> models = new LinkedList<ServiceUnit>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new ServiceUnit(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	public static class ResponseConverter implements
			WebAPI.ResponseConverter<ServiceUnit> {
		@Override
		public ServiceUnit convert(byte[] rawResponse) throws JSONException, ParseException {
			return new ServiceUnit(new JSONObject(new String(rawResponse)));
		}
	}

	public static class ListResponseConverter implements
			WebAPI.ResponseConverter<List<ServiceUnit>> {
		@Override
		public List<ServiceUnit> convert(byte[] rawResponse) throws JSONException,
				ParseException {
			JSONArray array = new JSONArray(new String(rawResponse));
			List<ServiceUnit> models = new LinkedList<ServiceUnit>();
			for (Integer i = 0; i < array.length(); ++i) {
				if (array.isNull(i)) {
					continue;
				}
				JSONObject object = array.getJSONObject(i);
				models.add(new ServiceUnit(object));
			}
			return models;
		}
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("activated_at", toJSON(getActivatedAt().orNull()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
		jsonObject.put("driver_id", toJSON(getDriverId()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("in_vehicle_device_id", toJSON(getInVehicleDeviceId().orNull()));
		jsonObject.put("unit_assignment_id", toJSON(getUnitAssignmentId().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("vehicle_id", toJSON(getVehicleId()));
		jsonObject.put("in_vehicle_device", toJSON(getInVehicleDevice()));
		if (getInVehicleDevice().isPresent()) {
			jsonObject.put("in_vehicle_device_id", toJSON(getInVehicleDevice().get().getId()));
		}
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

	private Long driverId = 0L;

	public Long getDriverId() {
		return wrapNull(driverId);
	}

	public void setDriverId(Long driverId) {
		this.driverId = wrapNull(driverId);
	}

	private Long id = 0L;

	public Long getId() {
		return wrapNull(id);
	}

	public void setId(Long id) {
		this.id = wrapNull(id);
	}

	private Optional<Long> inVehicleDeviceId = Optional.<Long>absent();

	public Optional<Long> getInVehicleDeviceId() {
		return wrapNull(inVehicleDeviceId);
	}

	public void setInVehicleDeviceId(Optional<Long> inVehicleDeviceId) {
		this.inVehicleDeviceId = wrapNull(inVehicleDeviceId);
	}

	public void setInVehicleDeviceId(Long inVehicleDeviceId) {
		this.inVehicleDeviceId = Optional.fromNullable(inVehicleDeviceId);
	}

	public void clearInVehicleDeviceId() {
		this.inVehicleDeviceId = Optional.<Long>absent();
	}

	private Optional<Long> unitAssignmentId = Optional.<Long>absent();

	public Optional<Long> getUnitAssignmentId() {
		return wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Optional<Long> unitAssignmentId) {
		this.unitAssignmentId = wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Long unitAssignmentId) {
		this.unitAssignmentId = Optional.fromNullable(unitAssignmentId);
	}

	public void clearUnitAssignmentId() {
		this.unitAssignmentId = Optional.<Long>absent();
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Long vehicleId = 0L;

	public Long getVehicleId() {
		return wrapNull(vehicleId);
	}

	public void setVehicleId(Long vehicleId) {
		this.vehicleId = wrapNull(vehicleId);
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
}
