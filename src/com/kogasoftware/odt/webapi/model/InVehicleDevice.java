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

public class InVehicleDevice extends Model {
	private static final long serialVersionUID = 2051608828621861232L;
	public static final String JSON_NAME = "in_vehicle_device";
	public static final String CONTROLLER_NAME = "in_vehicle_devices";

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

	public InVehicleDevice() {
	}

	public InVehicleDevice(JSONObject jsonObject) throws JSONException, ParseException {
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setId(parseLong(jsonObject, "id"));
		setModelName(parseString(jsonObject, "model_name"));
		setServiceProviderId(parseOptionalLong(jsonObject, "service_provider_id"));
		setTypeNumber(parseString(jsonObject, "type_number"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setServiceUnits(ServiceUnit.parseList(jsonObject, "service_units"));
		setVehicleNotifications(VehicleNotification.parseList(jsonObject, "vehicle_notifications"));
	}

	public static Optional<InVehicleDevice> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<InVehicleDevice>absent();
		}
		return Optional.<InVehicleDevice>of(new InVehicleDevice(jsonObject.getJSONObject(key)));
	}

	public static List<InVehicleDevice> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<InVehicleDevice>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		List<InVehicleDevice> models = new LinkedList<InVehicleDevice>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new InVehicleDevice(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	public static class ResponseConverter implements
			WebAPI.ResponseConverter<InVehicleDevice> {
		@Override
		public InVehicleDevice convert(byte[] rawResponse) throws JSONException, ParseException {
			return new InVehicleDevice(new JSONObject(new String(rawResponse)));
		}
	}

	public static class ListResponseConverter implements
			WebAPI.ResponseConverter<List<InVehicleDevice>> {
		@Override
		public List<InVehicleDevice> convert(byte[] rawResponse) throws JSONException,
				ParseException {
			JSONArray array = new JSONArray(new String(rawResponse));
			List<InVehicleDevice> models = new LinkedList<InVehicleDevice>();
			for (Integer i = 0; i < array.length(); ++i) {
				if (array.isNull(i)) {
					continue;
				}
				JSONObject object = array.getJSONObject(i);
				models.add(new InVehicleDevice(object));
			}
			return models;
		}
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("model_name", toJSON(getModelName()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("type_number", toJSON(getTypeNumber()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("service_units", toJSON(getServiceUnits()));
		jsonObject.put("vehicle_notifications", toJSON(getVehicleNotifications()));
		return jsonObject;
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

	private Long id = 0L;

	public Long getId() {
		return wrapNull(id);
	}

	public void setId(Long id) {
		this.id = wrapNull(id);
	}

	private String modelName = "";

	public String getModelName() {
		return wrapNull(modelName);
	}

	public void setModelName(String modelName) {
		this.modelName = wrapNull(modelName);
	}

	private Optional<Long> serviceProviderId = Optional.<Long>absent();

	public Optional<Long> getServiceProviderId() {
		return wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Optional<Long> serviceProviderId) {
		this.serviceProviderId = wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Long serviceProviderId) {
		this.serviceProviderId = Optional.fromNullable(serviceProviderId);
	}

	public void clearServiceProviderId() {
		this.serviceProviderId = Optional.<Long>absent();
	}

	private String typeNumber = "";

	public String getTypeNumber() {
		return wrapNull(typeNumber);
	}

	public void setTypeNumber(String typeNumber) {
		this.typeNumber = wrapNull(typeNumber);
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private List<ServiceUnit> serviceUnits = new LinkedList<ServiceUnit>();

	public List<ServiceUnit> getServiceUnits() {
		return new LinkedList<ServiceUnit>(wrapNull(serviceUnits));
	}

	public void setServiceUnits(List<ServiceUnit> serviceUnits) {
		this.serviceUnits = new LinkedList<ServiceUnit>(wrapNull(serviceUnits));
	}

	public void clearServiceUnits() {
		this.serviceUnits = new LinkedList<ServiceUnit>();
	}

	private List<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>();

	public List<VehicleNotification> getVehicleNotifications() {
		return new LinkedList<VehicleNotification>(wrapNull(vehicleNotifications));
	}

	public void setVehicleNotifications(List<VehicleNotification> vehicleNotifications) {
		this.vehicleNotifications = new LinkedList<VehicleNotification>(wrapNull(vehicleNotifications));
	}

	public void clearVehicleNotifications() {
		this.vehicleNotifications = new LinkedList<VehicleNotification>();
	}
}
