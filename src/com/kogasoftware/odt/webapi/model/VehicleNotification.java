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

public class VehicleNotification extends Model {
	private static final long serialVersionUID = 7331053154935266069L;
	public static final String JSON_NAME = "vehicle_notification";
	public static final String CONTROLLER_NAME = "vehicle_notifications";

	public static class URL {
		public static final String ROOT = "/" + CONTROLLER_NAME;
		public static final String CREATE = "/" + CONTROLLER_NAME + "/create";
		public static final String INDEX = "/" + CONTROLLER_NAME + "/index";
	}

	public VehicleNotification() {
	}

	public VehicleNotification(JSONObject jsonObject) throws JSONException, ParseException {
		setBody(parseOptionalString(jsonObject, "body"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setId(parseLong(jsonObject, "id"));
		setInVehicleDeviceId(parseLong(jsonObject, "in_vehicle_device_id"));
		setOperatorId(parseLong(jsonObject, "operator_id"));
		setReadAt(parseOptionalDate(jsonObject, "read_at"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setInVehicleDevice(new InVehicleDevice(jsonObject.getJSONObject("in_vehicle_device")));
		if (getInVehicleDevice().isPresent()) {
			setInVehicleDeviceId(getInVehicleDevice().get().getId());
		}
		setOperator(new Operator(jsonObject.getJSONObject("operator")));
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

	public static List<VehicleNotification> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<VehicleNotification>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		List<VehicleNotification> models = new LinkedList<VehicleNotification>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new VehicleNotification(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	public static class ResponseConverter implements
			WebAPI.ResponseConverter<VehicleNotification> {
		@Override
		public VehicleNotification convert(byte[] rawResponse) throws JSONException, ParseException {
			return new VehicleNotification(new JSONObject(new String(rawResponse)));
		}
	}

	public static class ListResponseConverter implements
			WebAPI.ResponseConverter<List<VehicleNotification>> {
		@Override
		public List<VehicleNotification> convert(byte[] rawResponse) throws JSONException,
				ParseException {
			JSONArray array = new JSONArray(new String(rawResponse));
			List<VehicleNotification> models = new LinkedList<VehicleNotification>();
			for (Integer i = 0; i < array.length(); ++i) {
				if (array.isNull(i)) {
					continue;
				}
				JSONObject object = array.getJSONObject(i);
				models.add(new VehicleNotification(object));
			}
			return models;
		}
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

	private Long id = 0L;

	public Long getId() {
		return wrapNull(id);
	}

	public void setId(Long id) {
		this.id = wrapNull(id);
	}

	private Long inVehicleDeviceId = 0L;

	public Long getInVehicleDeviceId() {
		return wrapNull(inVehicleDeviceId);
	}

	public void setInVehicleDeviceId(Long inVehicleDeviceId) {
		this.inVehicleDeviceId = wrapNull(inVehicleDeviceId);
	}

	private Long operatorId = 0L;

	public Long getOperatorId() {
		return wrapNull(operatorId);
	}

	public void setOperatorId(Long operatorId) {
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
