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

public class VehicleNotification extends Model {
	private static final long serialVersionUID = 2497894772363662733L;

	public VehicleNotification() {
	}

	public VehicleNotification(JSONObject jsonObject) throws JSONException, ParseException {
		setBody(parseOptionalString(jsonObject, "body"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setId(parseInteger(jsonObject, "id"));
		setInVehicleDeviceId(parseInteger(jsonObject, "in_vehicle_device_id"));
		setOperatorId(parseInteger(jsonObject, "operator_id"));
		setReadAt(parseOptionalDate(jsonObject, "read_at"));
		setResponse(parseOptionalInteger(jsonObject, "response"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setInVehicleDevice(InVehicleDevice.parse(jsonObject, "in_vehicle_device"));
		if (getInVehicleDevice().isPresent()) {
			setInVehicleDeviceId(getInVehicleDevice().get().getId());
		}
		setOperator(Operator.parse(jsonObject, "operator"));
		if (getOperator().isPresent()) {
			setOperatorId(getOperator().get().getId());
		}
	}

	public static Optional<VehicleNotification> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<VehicleNotification>absent();
		}
		return Optional.<VehicleNotification>of(new VehicleNotification(jsonObject.getJSONObject(key)));
	}

	public static LinkedList<VehicleNotification> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<VehicleNotification>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<VehicleNotification> parseList(JSONArray jsonArray) throws JSONException, ParseException {
		LinkedList<VehicleNotification> models = new LinkedList<VehicleNotification>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new VehicleNotification(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("body", toJSON(getBody().orNull()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("in_vehicle_device_id", toJSON(getInVehicleDeviceId()));
		jsonObject.put("operator_id", toJSON(getOperatorId()));
		jsonObject.put("read_at", toJSON(getReadAt().orNull()));
		jsonObject.put("response", toJSON(getResponse().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("in_vehicle_device", toJSON(getInVehicleDevice()));
		if (getInVehicleDevice().isPresent()) {
			jsonObject.put("in_vehicle_device_id", toJSON(getInVehicleDevice().get().getId()));
		}
		jsonObject.put("operator", toJSON(getOperator()));
		if (getOperator().isPresent()) {
			jsonObject.put("operator_id", toJSON(getOperator().get().getId()));
		}
		return jsonObject;
	}

	private Optional<String> body = Optional.<String>absent();

	public Optional<String> getBody() {
		return wrapNull(body);
	}

	public void setBody(Optional<String> body) {
		this.body = wrapNull(body);
	}

	public void setBody(String body) {
		this.body = Optional.fromNullable(body);
	}

	public void clearBody() {
		this.body = Optional.<String>absent();
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = wrapNull(createdAt);
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	private Integer inVehicleDeviceId = 0;

	public Integer getInVehicleDeviceId() {
		return wrapNull(inVehicleDeviceId);
	}

	public void setInVehicleDeviceId(Integer inVehicleDeviceId) {
		this.inVehicleDeviceId = wrapNull(inVehicleDeviceId);
	}

	private Integer operatorId = 0;

	public Integer getOperatorId() {
		return wrapNull(operatorId);
	}

	public void setOperatorId(Integer operatorId) {
		this.operatorId = wrapNull(operatorId);
	}

	private Optional<Date> readAt = Optional.<Date>absent();

	public Optional<Date> getReadAt() {
		return wrapNull(readAt);
	}

	public void setReadAt(Optional<Date> readAt) {
		this.readAt = wrapNull(readAt);
	}

	public void setReadAt(Date readAt) {
		this.readAt = Optional.fromNullable(readAt);
	}

	public void clearReadAt() {
		this.readAt = Optional.<Date>absent();
	}

	private Optional<Integer> response = Optional.<Integer>absent();

	public Optional<Integer> getResponse() {
		return wrapNull(response);
	}

	public void setResponse(Optional<Integer> response) {
		this.response = wrapNull(response);
	}

	public void setResponse(Integer response) {
		this.response = Optional.fromNullable(response);
	}

	public void clearResponse() {
		this.response = Optional.<Integer>absent();
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
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

	private Optional<Operator> operator = Optional.<Operator>absent();

	public Optional<Operator> getOperator() {
		return wrapNull(operator);
	}

	public void setOperator(Optional<Operator> operator) {
		this.operator = wrapNull(operator);
	}

	public void setOperator(Operator operator) {
		this.operator = Optional.<Operator>fromNullable(operator);
	}

	public void clearOperator() {
		this.operator = Optional.<Operator>absent();
	}
}
