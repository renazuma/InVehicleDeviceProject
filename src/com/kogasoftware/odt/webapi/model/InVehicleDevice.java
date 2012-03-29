package com.kogasoftware.odt.webapi.model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public class InVehicleDevice extends Model {
	private static final long serialVersionUID = 6869197652221069050L;

	public InVehicleDevice() {
	}

	public InVehicleDevice(JSONObject jsonObject) throws JSONException, ParseException {
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setId(parseInteger(jsonObject, "id"));
		setModelName(parseString(jsonObject, "model_name"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
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

	public static LinkedList<InVehicleDevice> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<InVehicleDevice>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		LinkedList<InVehicleDevice> models = new LinkedList<InVehicleDevice>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new InVehicleDevice(jsonArray.getJSONObject(i)));
		}
		return models;
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

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	private String modelName = "";

	public String getModelName() {
		return wrapNull(modelName);
	}

	public void setModelName(String modelName) {
		this.modelName = wrapNull(modelName);
	}

	private Optional<Integer> serviceProviderId = Optional.<Integer>absent();

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
		this.serviceProviderId = Optional.<Integer>absent();
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

	private LinkedList<ServiceUnit> serviceUnits = new LinkedList<ServiceUnit>();

	public LinkedList<ServiceUnit> getServiceUnits() {
		return new LinkedList<ServiceUnit>(wrapNull(serviceUnits));
	}

	public void setServiceUnits(LinkedList<ServiceUnit> serviceUnits) {
		this.serviceUnits = new LinkedList<ServiceUnit>(wrapNull(serviceUnits));
	}

	public void clearServiceUnits() {
		this.serviceUnits = new LinkedList<ServiceUnit>();
	}

	private LinkedList<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>();

	public LinkedList<VehicleNotification> getVehicleNotifications() {
		return new LinkedList<VehicleNotification>(wrapNull(vehicleNotifications));
	}

	public void setVehicleNotifications(LinkedList<VehicleNotification> vehicleNotifications) {
		this.vehicleNotifications = new LinkedList<VehicleNotification>(wrapNull(vehicleNotifications));
	}

	public void clearVehicleNotifications() {
		this.vehicleNotifications = new LinkedList<VehicleNotification>();
	}
}
