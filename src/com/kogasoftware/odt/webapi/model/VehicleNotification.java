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
	private static final long serialVersionUID = 3060550483008757877L;

	public VehicleNotification() {
	}

	public VehicleNotification(JSONObject jsonObject) throws JSONException, ParseException {
		setBody(parseString(jsonObject, "body"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setEventAt(parseOptionalDate(jsonObject, "event_at"));
		setId(parseInteger(jsonObject, "id"));
		setInVehicleDeviceId(parseInteger(jsonObject, "in_vehicle_device_id"));
		setNotificationKind(parseInteger(jsonObject, "notification_kind"));
		setOffline(parseOptionalBoolean(jsonObject, "offline"));
		setOperatorId(parseInteger(jsonObject, "operator_id"));
		setReadAt(parseOptionalDate(jsonObject, "read_at"));
		setReservationId(parseOptionalInteger(jsonObject, "reservation_id"));
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
		setReservation(Reservation.parse(jsonObject, "reservation"));
		if (getReservation().isPresent()) {
			setReservationId(getReservation().get().getId());
		}
	}

	public static Optional<VehicleNotification> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<VehicleNotification>absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<VehicleNotification> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.<VehicleNotification>of(new VehicleNotification(jsonObject));
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
		jsonObject.put("body", toJSON(getBody()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("event_at", toJSON(getEventAt().orNull()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("in_vehicle_device_id", toJSON(getInVehicleDeviceId()));
		jsonObject.put("notification_kind", toJSON(getNotificationKind()));
		jsonObject.put("offline", toJSON(getOffline().orNull()));
		jsonObject.put("operator_id", toJSON(getOperatorId()));
		jsonObject.put("read_at", toJSON(getReadAt().orNull()));
		jsonObject.put("reservation_id", toJSON(getReservationId().orNull()));
		jsonObject.put("response", toJSON(getResponse().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));

		if (getInVehicleDevice().isPresent()) {
			jsonObject.put("in_vehicle_device_id", toJSON(getInVehicleDevice().get().getId()));
		}

		if (getOperator().isPresent()) {
			jsonObject.put("operator_id", toJSON(getOperator().get().getId()));
		}

		if (getReservation().isPresent()) {
			jsonObject.put("reservation_id", toJSON(getReservation().get().getId()));
		}
		return jsonObject;
	}

	private String body = "";

	public String getBody() {
		return wrapNull(body);
	}

	public void setBody(String body) {
		this.body = wrapNull(body);
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> eventAt = Optional.<Date>absent();

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
		this.eventAt = Optional.<Date>absent();
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

	private Optional<Boolean> offline = Optional.<Boolean>absent();

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
		this.offline = Optional.<Boolean>absent();
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

	private Optional<Integer> reservationId = Optional.<Integer>absent();

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
		this.reservationId = Optional.<Integer>absent();
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

	private Optional<Reservation> reservation = Optional.<Reservation>absent();

	public Optional<Reservation> getReservation() {
		return wrapNull(reservation);
	}

	public void setReservation(Optional<Reservation> reservation) {
		this.reservation = wrapNull(reservation);
	}

	public void setReservation(Reservation reservation) {
		this.reservation = Optional.<Reservation>fromNullable(reservation);
	}

	public void clearReservation() {
		this.reservation = Optional.<Reservation>absent();
	}
}
