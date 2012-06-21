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

public class VehicleNotification extends Model {
	private static final long serialVersionUID = 1878705192607302047L;

	public VehicleNotification() {
	}

	public VehicleNotification(JSONObject jsonObject) throws JSONException {
		try {
			fillMembers(this, jsonObject);
		} catch (ParseException e) {
			throw new JSONException(e.toString() + "\n" + ExceptionUtils.getStackTrace(e));
		}
	}

	public static void fillMembers(VehicleNotification model, JSONObject jsonObject) throws JSONException, ParseException {
		model.setBody(parseString(jsonObject, "body"));
		model.setBodyRuby(parseOptionalString(jsonObject, "body_ruby"));
		model.setCreatedAt(parseDate(jsonObject, "created_at"));
		model.setEventAt(parseOptionalDate(jsonObject, "event_at"));
		model.setId(parseInteger(jsonObject, "id"));
		model.setInVehicleDeviceId(parseInteger(jsonObject, "in_vehicle_device_id"));
		model.setNotificationKind(parseInteger(jsonObject, "notification_kind"));
		model.setOffline(parseOptionalBoolean(jsonObject, "offline"));
		model.setOperatorId(parseOptionalInteger(jsonObject, "operator_id"));
		model.setReadAt(parseOptionalDate(jsonObject, "read_at"));
		model.setReservationId(parseOptionalInteger(jsonObject, "reservation_id"));
		model.setResponse(parseOptionalInteger(jsonObject, "response"));
		model.setUpdatedAt(parseDate(jsonObject, "updated_at"));
		model.setInVehicleDevice(InVehicleDevice.parse(jsonObject, "in_vehicle_device"));
		model.setOperator(Operator.parse(jsonObject, "operator"));
		model.setReservation(Reservation.parse(jsonObject, "reservation"));
	}

	public static Optional<VehicleNotification> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<VehicleNotification> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.of(new VehicleNotification(jsonObject));
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
	protected JSONObject toJSONObject(Boolean recursive, Integer depth) throws JSONException {
		depth++;
		if (depth > MAX_RECURSE_DEPTH) {
			return new JSONObject();
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("body", toJSON(getBody()));
		jsonObject.put("body_ruby", toJSON(getBodyRuby().orNull()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("event_at", toJSON(getEventAt().orNull()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("in_vehicle_device_id", toJSON(getInVehicleDeviceId()));
		jsonObject.put("notification_kind", toJSON(getNotificationKind()));
		jsonObject.put("offline", toJSON(getOffline().orNull()));
		jsonObject.put("operator_id", toJSON(getOperatorId().orNull()));
		jsonObject.put("read_at", toJSON(getReadAt().orNull()));
		jsonObject.put("reservation_id", toJSON(getReservationId().orNull()));
		jsonObject.put("response", toJSON(getResponse().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		if (getInVehicleDevice().isPresent()) {
			if (recursive) {
				jsonObject.put("in_vehicle_device", getInVehicleDevice().get().toJSONObject(true, depth));
			} else {
				jsonObject.put("in_vehicle_device_id", toJSON(getInVehicleDevice().get().getId()));
			}
		}
		if (getOperator().isPresent()) {
			if (recursive) {
				jsonObject.put("operator", getOperator().get().toJSONObject(true, depth));
			} else {
				jsonObject.put("operator_id", toJSON(getOperator().get().getId()));
			}
		}
		if (getReservation().isPresent()) {
			if (recursive) {
				jsonObject.put("reservation", getReservation().get().toJSONObject(true, depth));
			} else {
				jsonObject.put("reservation_id", toJSON(getReservation().get().getId()));
			}
		}
		return jsonObject;
	}

	private void writeObject(ObjectOutputStream objectOutputStream)
			throws IOException {
		try {
			objectOutputStream.writeObject(toJSONObject(true).toString());
		} catch (JSONException e) {
			throw new IOException(e.toString() + "\n" + ExceptionUtils.getStackTrace(e));
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
			throw new IOException(e.toString() + "\n" + ExceptionUtils.getStackTrace(e));
		} catch (ParseException e) {
			throw new IOException(e.toString() + "\n" + ExceptionUtils.getStackTrace(e));
		}
	}

	@Override
	public VehicleNotification cloneByJSON() throws JSONException {
		return new VehicleNotification(toJSONObject(true));
	}

	private String body = "";

	public String getBody() {
		return wrapNull(body);
	}

	public void setBody(String body) {
		this.body = wrapNull(body);
	}

	private Optional<String> bodyRuby = Optional.absent();

	public Optional<String> getBodyRuby() {
		return wrapNull(bodyRuby);
	}

	public void setBodyRuby(Optional<String> bodyRuby) {
		this.bodyRuby = wrapNull(bodyRuby);
	}

	public void setBodyRuby(String bodyRuby) {
		this.bodyRuby = Optional.fromNullable(bodyRuby);
	}

	public void clearBodyRuby() {
		this.bodyRuby = Optional.absent();
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> eventAt = Optional.absent();

	public Optional<Date> getEventAt() {
		return wrapNull(eventAt);
	}

	public void setEventAt(Optional<Date> eventAt) {
		this.eventAt = wrapNull(eventAt);
	}

	public void setEventAt(Date eventAt) {
		this.eventAt = Optional.fromNullable(eventAt);
	}

	public void clearEventAt() {
		this.eventAt = Optional.absent();
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

	private Integer notificationKind = 0;

	public Integer getNotificationKind() {
		return wrapNull(notificationKind);
	}

	public void setNotificationKind(Integer notificationKind) {
		this.notificationKind = wrapNull(notificationKind);
	}

	private Optional<Boolean> offline = Optional.absent();

	public Optional<Boolean> getOffline() {
		return wrapNull(offline);
	}

	public void setOffline(Optional<Boolean> offline) {
		this.offline = wrapNull(offline);
	}

	public void setOffline(Boolean offline) {
		this.offline = Optional.fromNullable(offline);
	}

	public void clearOffline() {
		this.offline = Optional.absent();
	}

	private Optional<Integer> operatorId = Optional.absent();

	public Optional<Integer> getOperatorId() {
		return wrapNull(operatorId);
	}

	public void setOperatorId(Optional<Integer> operatorId) {
		this.operatorId = wrapNull(operatorId);
	}

	public void setOperatorId(Integer operatorId) {
		this.operatorId = Optional.fromNullable(operatorId);
	}

	public void clearOperatorId() {
		this.operatorId = Optional.absent();
	}

	private Optional<Date> readAt = Optional.absent();

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
		this.readAt = Optional.absent();
	}

	private Optional<Integer> reservationId = Optional.absent();

	public Optional<Integer> getReservationId() {
		return wrapNull(reservationId);
	}

	public void setReservationId(Optional<Integer> reservationId) {
		this.reservationId = wrapNull(reservationId);
	}

	public void setReservationId(Integer reservationId) {
		this.reservationId = Optional.fromNullable(reservationId);
	}

	public void clearReservationId() {
		this.reservationId = Optional.absent();
	}

	private Optional<Integer> response = Optional.absent();

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
		this.response = Optional.absent();
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
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

	private Optional<Operator> operator = Optional.absent();

	public Optional<Operator> getOperator() {
		return wrapNull(operator);
	}

	public void setOperator(Optional<Operator> operator) {
		this.operator = wrapNull(operator);
	}

	public void setOperator(Operator operator) {
		this.operator = Optional.fromNullable(operator);
	}

	public void clearOperator() {
		this.operator = Optional.absent();
	}

	private Optional<Reservation> reservation = Optional.absent();

	public Optional<Reservation> getReservation() {
		return wrapNull(reservation);
	}

	public void setReservation(Optional<Reservation> reservation) {
		this.reservation = wrapNull(reservation);
	}

	public void setReservation(Reservation reservation) {
		this.reservation = Optional.fromNullable(reservation);
	}

	public void clearReservation() {
		this.reservation = Optional.absent();
	}
}
