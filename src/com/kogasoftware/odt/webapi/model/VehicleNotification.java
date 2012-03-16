package com.kogasoftware.odt.webapi.model;

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
	private static final long serialVersionUID = 6046564506822777431L;
	public static final String JSON_NAME = "vehicle_notification";
	public static final String CONTROLLER_NAME = "vehicle_notifications";

	public static class URL {
		public static final String ROOT = "/" + CONTROLLER_NAME;
		public static final String CREATE = "/" + CONTROLLER_NAME + "/create";
		public static final String INDEX = "/" + CONTROLLER_NAME + "/index";
	}

	public VehicleNotification() {
	}

	public VehicleNotification(JSONObject jsonObject) throws JSONException,
			ParseException {
		setBody(parseString(jsonObject, "body"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setId(parseInteger(jsonObject, "id"));
		setInVehicleDeviceId(parseInteger(jsonObject, "in_vehicle_device_id"));
		setOperatorId(parseInteger(jsonObject, "operator_id"));
		setReadAt(parseDate(jsonObject, "read_at"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
	}

	public static class ResponseConverter implements
			WebAPI.ResponseConverter<VehicleNotification> {
		@Override
		public VehicleNotification convert(byte[] rawResponse)
				throws JSONException, ParseException {
			return new VehicleNotification(new JSONObject(new String(
					rawResponse)));
		}
	}

	public static class ListResponseConverter implements
			WebAPI.ResponseConverter<List<VehicleNotification>> {
		@Override
		public List<VehicleNotification> convert(byte[] rawResponse)
				throws JSONException, ParseException {
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
		return jsonObject;
	}

	private Optional<String> body = Optional.<String> absent();

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
		this.body = Optional.<String> absent();
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		errorIfNull(createdAt);
		this.createdAt = wrapNull(createdAt);
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		errorIfNull(id);
		this.id = wrapNull(id);
	}

	private Integer inVehicleDeviceId = 0;

	public Integer getInVehicleDeviceId() {
		return wrapNull(inVehicleDeviceId);
	}

	public void setInVehicleDeviceId(Integer inVehicleDeviceId) {
		errorIfNull(inVehicleDeviceId);
		this.inVehicleDeviceId = wrapNull(inVehicleDeviceId);
	}

	private Integer operatorId = 0;

	public Integer getOperatorId() {
		return wrapNull(operatorId);
	}

	public void setOperatorId(Integer operatorId) {
		errorIfNull(operatorId);
		this.operatorId = wrapNull(operatorId);
	}

	private Optional<Date> readAt = Optional.<Date> absent();

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
		this.readAt = Optional.<Date> absent();
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		errorIfNull(updatedAt);
		this.updatedAt = wrapNull(updatedAt);
	}
}
