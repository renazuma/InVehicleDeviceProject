package com.kogasoftware.odt.webapi.model.base;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;
import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPI.ResponseConverter;
import com.kogasoftware.odt.webapi.model.*;

@SuppressWarnings("unused")
public abstract class VehicleNotificationBase extends Model {
	private static final long serialVersionUID = 118686875032817512L;
	public static final ResponseConverter<VehicleNotification> RESPONSE_CONVERTER = new ResponseConverter<VehicleNotification>() {
		@Override
		public VehicleNotification convert(byte[] rawResponse) throws JSONException {
			return parse(WebAPI.parseJSONObject(rawResponse));
		}
	};
	public static final ResponseConverter<List<VehicleNotification>> LIST_RESPONSE_CONVERTER = new ResponseConverter<List<VehicleNotification>>() {
		@Override
		public List<VehicleNotification> convert(byte[] rawResponse) throws JSONException {
			return parseList(WebAPI.parseJSONArray(rawResponse));
		}
	};
	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}
	@Override
	public void fill(JSONObject jsonObject) throws JSONException {
		setBody(parseString(jsonObject, "body"));
		setBodyRuby(parseOptionalString(jsonObject, "body_ruby"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setEventAt(parseOptionalDate(jsonObject, "event_at"));
		setId(parseInteger(jsonObject, "id"));
		setInVehicleDeviceId(parseInteger(jsonObject, "in_vehicle_device_id"));
		setNotificationKind(parseInteger(jsonObject, "notification_kind"));
		setOffline(parseOptionalBoolean(jsonObject, "offline"));
		setOperatorId(parseOptionalInteger(jsonObject, "operator_id"));
		setReadAt(parseOptionalDate(jsonObject, "read_at"));
		setReservationId(parseOptionalInteger(jsonObject, "reservation_id"));
		setResponse(parseOptionalInteger(jsonObject, "response"));
		setInVehicleDevice(InVehicleDevice.parse(jsonObject, "in_vehicle_device"));
		setOperator(Operator.parse(jsonObject, "operator"));
		setReservation(Reservation.parse(jsonObject, "reservation"));

		setUpdatedAt(parseDate(jsonObject, "updated_at"));
	}

	public static Optional<VehicleNotification> parse(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return Optional.of(parse(jsonObject.getJSONObject(key)));
	}

	public static VehicleNotification parse(JSONObject jsonObject) throws JSONException {
		VehicleNotification model = new VehicleNotification();
		model.fill(jsonObject);
		return model;
	}

	public static LinkedList<VehicleNotification> parseList(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return new LinkedList<VehicleNotification>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<VehicleNotification> parseList(JSONArray jsonArray) throws JSONException {
		LinkedList<VehicleNotification> models = new LinkedList<VehicleNotification>();
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
		jsonObject.put("body", toJSON(getBody()));
		jsonObject.put("body_ruby", toJSON(getBodyRuby()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("event_at", toJSON(getEventAt()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("in_vehicle_device_id", toJSON(getInVehicleDeviceId()));
		jsonObject.put("notification_kind", toJSON(getNotificationKind()));
		jsonObject.put("offline", toJSON(getOffline()));
		jsonObject.put("operator_id", toJSON(getOperatorId()));
		jsonObject.put("read_at", toJSON(getReadAt()));
		jsonObject.put("reservation_id", toJSON(getReservationId()));
		jsonObject.put("response", toJSON(getResponse()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		if (getInVehicleDevice().isPresent()) {
			if (recursive) {
				jsonObject.put("in_vehicle_device", getInVehicleDevice().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("in_vehicle_device_id", toJSON(getInVehicleDevice().get().getId()));
			}
		}
		if (getOperator().isPresent()) {
			if (recursive) {
				jsonObject.put("operator", getOperator().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("operator_id", toJSON(getOperator().get().getId()));
			}
		}
		if (getReservation().isPresent()) {
			if (recursive) {
				jsonObject.put("reservation", getReservation().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("reservation_id", toJSON(getReservation().get().getId()));
			}
		}
		return jsonObject;
	}

	@Override
	public VehicleNotification cloneByJSON() throws JSONException {
		return parse(toJSONObject(true));
	}

	private String body = "";

	public String getBody() {
		return wrapNull(body);
	}

	public void setBody(String body) {
		refreshUpdatedAt();
		this.body = wrapNull(body);
	}

	private Optional<String> bodyRuby = Optional.absent();

	public Optional<String> getBodyRuby() {
		return wrapNull(bodyRuby);
	}

	public void setBodyRuby(Optional<String> bodyRuby) {
		refreshUpdatedAt();
		this.bodyRuby = wrapNull(bodyRuby);
	}

	public void setBodyRuby(String bodyRuby) {
		setBodyRuby(Optional.fromNullable(bodyRuby));
	}

	public void clearBodyRuby() {
		setBodyRuby(Optional.<String>absent());
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		refreshUpdatedAt();
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> eventAt = Optional.absent();

	public Optional<Date> getEventAt() {
		return wrapNull(eventAt);
	}

	public void setEventAt(Optional<Date> eventAt) {
		refreshUpdatedAt();
		this.eventAt = wrapNull(eventAt);
	}

	public void setEventAt(Date eventAt) {
		setEventAt(Optional.fromNullable(eventAt));
	}

	public void clearEventAt() {
		setEventAt(Optional.<Date>absent());
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		refreshUpdatedAt();
		this.id = wrapNull(id);
	}

	private Integer inVehicleDeviceId = 0;

	public Integer getInVehicleDeviceId() {
		return wrapNull(inVehicleDeviceId);
	}

	public void setInVehicleDeviceId(Integer inVehicleDeviceId) {
		refreshUpdatedAt();
		this.inVehicleDeviceId = wrapNull(inVehicleDeviceId);
	}

	private Integer notificationKind = 0;

	public Integer getNotificationKind() {
		return wrapNull(notificationKind);
	}

	public void setNotificationKind(Integer notificationKind) {
		refreshUpdatedAt();
		this.notificationKind = wrapNull(notificationKind);
	}

	private Optional<Boolean> offline = Optional.absent();

	public Optional<Boolean> getOffline() {
		return wrapNull(offline);
	}

	public void setOffline(Optional<Boolean> offline) {
		refreshUpdatedAt();
		this.offline = wrapNull(offline);
	}

	public void setOffline(Boolean offline) {
		setOffline(Optional.fromNullable(offline));
	}

	public void clearOffline() {
		setOffline(Optional.<Boolean>absent());
	}

	private Optional<Integer> operatorId = Optional.absent();

	public Optional<Integer> getOperatorId() {
		return wrapNull(operatorId);
	}

	public void setOperatorId(Optional<Integer> operatorId) {
		refreshUpdatedAt();
		this.operatorId = wrapNull(operatorId);
	}

	public void setOperatorId(Integer operatorId) {
		setOperatorId(Optional.fromNullable(operatorId));
	}

	public void clearOperatorId() {
		setOperatorId(Optional.<Integer>absent());
	}

	private Optional<Date> readAt = Optional.absent();

	public Optional<Date> getReadAt() {
		return wrapNull(readAt);
	}

	public void setReadAt(Optional<Date> readAt) {
		refreshUpdatedAt();
		this.readAt = wrapNull(readAt);
	}

	public void setReadAt(Date readAt) {
		setReadAt(Optional.fromNullable(readAt));
	}

	public void clearReadAt() {
		setReadAt(Optional.<Date>absent());
	}

	private Optional<Integer> reservationId = Optional.absent();

	public Optional<Integer> getReservationId() {
		return wrapNull(reservationId);
	}

	public void setReservationId(Optional<Integer> reservationId) {
		refreshUpdatedAt();
		this.reservationId = wrapNull(reservationId);
	}

	public void setReservationId(Integer reservationId) {
		setReservationId(Optional.fromNullable(reservationId));
	}

	public void clearReservationId() {
		setReservationId(Optional.<Integer>absent());
	}

	private Optional<Integer> response = Optional.absent();

	public Optional<Integer> getResponse() {
		return wrapNull(response);
	}

	public void setResponse(Optional<Integer> response) {
		refreshUpdatedAt();
		this.response = wrapNull(response);
	}

	public void setResponse(Integer response) {
		setResponse(Optional.fromNullable(response));
	}

	public void clearResponse() {
		setResponse(Optional.<Integer>absent());
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
		setInVehicleDevice(Optional.fromNullable(inVehicleDevice));
	}

	public void clearInVehicleDevice() {
		setInVehicleDevice(Optional.<InVehicleDevice>absent());
	}

	private Optional<Operator> operator = Optional.<Operator>absent();

	public Optional<Operator> getOperator() {
		return wrapNull(operator);
	}

	public void setOperator(Optional<Operator> operator) {
		this.operator = wrapNull(operator);
	}

	public void setOperator(Operator operator) {
		setOperator(Optional.fromNullable(operator));
	}

	public void clearOperator() {
		setOperator(Optional.<Operator>absent());
	}

	private Optional<Reservation> reservation = Optional.<Reservation>absent();

	public Optional<Reservation> getReservation() {
		return wrapNull(reservation);
	}

	public void setReservation(Optional<Reservation> reservation) {
		this.reservation = wrapNull(reservation);
	}

	public void setReservation(Reservation reservation) {
		setReservation(Optional.fromNullable(reservation));
	}

	public void clearReservation() {
		setReservation(Optional.<Reservation>absent());
	}
}
