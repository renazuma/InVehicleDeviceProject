package com.kogasoftware.odt.webapi.model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public class PassengerRecord extends Model {
	private static final long serialVersionUID = 5541609164994013038L;

	public PassengerRecord() {
	}

	public PassengerRecord(JSONObject jsonObject) throws JSONException, ParseException {
		setArrivalOperationScheduleId(parseOptionalInteger(jsonObject, "arrival_operation_schedule_id"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setDepartureOperationScheduleId(parseOptionalInteger(jsonObject, "departure_operation_schedule_id"));
		setGetOffTime(parseOptionalDate(jsonObject, "get_off_time"));
		setGetOffTimeOffline(parseOptionalBoolean(jsonObject, "get_off_time_offline"));
		setGetOnTime(parseOptionalDate(jsonObject, "get_on_time"));
		setGetOnTimeOffline(parseOptionalBoolean(jsonObject, "get_on_time_offline"));
		setId(parseInteger(jsonObject, "id"));
		setPassengerCount(parseInteger(jsonObject, "passenger_count"));
		setPayment(parseOptionalInteger(jsonObject, "payment"));
		setReservationId(parseOptionalInteger(jsonObject, "reservation_id"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setStatus(parseInteger(jsonObject, "status"));
		setTimestamp(parseOptionalDate(jsonObject, "timestamp"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setArrivalOperationSchedule(OperationSchedule.parse(jsonObject, "arrival_operation_schedule"));
		if (getArrivalOperationSchedule().isPresent()) {
			setArrivalOperationScheduleId(getArrivalOperationSchedule().get().getId());
		}
		setDepartureOperationSchedule(OperationSchedule.parse(jsonObject, "departure_operation_schedule"));
		if (getDepartureOperationSchedule().isPresent()) {
			setDepartureOperationScheduleId(getDepartureOperationSchedule().get().getId());
		}
		setReservation(Reservation.parse(jsonObject, "reservation"));
		if (getReservation().isPresent()) {
			setReservationId(getReservation().get().getId());
		}
		setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
		if (getServiceProvider().isPresent()) {
			setServiceProviderId(getServiceProvider().get().getId());
		}
	}

	public static Optional<PassengerRecord> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<PassengerRecord> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.of(new PassengerRecord(jsonObject));
	}

	public static LinkedList<PassengerRecord> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<PassengerRecord>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<PassengerRecord> parseList(JSONArray jsonArray) throws JSONException, ParseException {
		LinkedList<PassengerRecord> models = new LinkedList<PassengerRecord>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new PassengerRecord(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("arrival_operation_schedule_id", toJSON(getArrivalOperationScheduleId().orNull()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
		jsonObject.put("departure_operation_schedule_id", toJSON(getDepartureOperationScheduleId().orNull()));
		jsonObject.put("get_off_time", toJSON(getGetOffTime().orNull()));
		jsonObject.put("get_off_time_offline", toJSON(getGetOffTimeOffline().orNull()));
		jsonObject.put("get_on_time", toJSON(getGetOnTime().orNull()));
		jsonObject.put("get_on_time_offline", toJSON(getGetOnTimeOffline().orNull()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("passenger_count", toJSON(getPassengerCount()));
		jsonObject.put("payment", toJSON(getPayment().orNull()));
		jsonObject.put("reservation_id", toJSON(getReservationId().orNull()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("status", toJSON(getStatus()));
		jsonObject.put("timestamp", toJSON(getTimestamp().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));

		if (getArrivalOperationSchedule().isPresent()) {
			jsonObject.put("arrival_operation_schedule_id", toJSON(getArrivalOperationSchedule().get().getId()));
		}

		if (getDepartureOperationSchedule().isPresent()) {
			jsonObject.put("departure_operation_schedule_id", toJSON(getDepartureOperationSchedule().get().getId()));
		}

		if (getReservation().isPresent()) {
			jsonObject.put("reservation_id", toJSON(getReservation().get().getId()));
		}

		if (getServiceProvider().isPresent()) {
			jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
		}
		return jsonObject;
	}

	@Override
	public PassengerRecord clone() {
		return SerializationUtils.clone(this);
	}

	private Optional<Integer> arrivalOperationScheduleId = Optional.absent();

	public Optional<Integer> getArrivalOperationScheduleId() {
		return wrapNull(arrivalOperationScheduleId);
	}

	public void setArrivalOperationScheduleId(Optional<Integer> arrivalOperationScheduleId) {
		this.arrivalOperationScheduleId = wrapNull(arrivalOperationScheduleId);
	}

	public void setArrivalOperationScheduleId(Integer arrivalOperationScheduleId) {
		this.arrivalOperationScheduleId = Optional.fromNullable(arrivalOperationScheduleId);
	}

	public void clearArrivalOperationScheduleId() {
		this.arrivalOperationScheduleId = Optional.absent();
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> deletedAt = Optional.absent();

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
		this.deletedAt = Optional.absent();
	}

	private Optional<Integer> departureOperationScheduleId = Optional.absent();

	public Optional<Integer> getDepartureOperationScheduleId() {
		return wrapNull(departureOperationScheduleId);
	}

	public void setDepartureOperationScheduleId(Optional<Integer> departureOperationScheduleId) {
		this.departureOperationScheduleId = wrapNull(departureOperationScheduleId);
	}

	public void setDepartureOperationScheduleId(Integer departureOperationScheduleId) {
		this.departureOperationScheduleId = Optional.fromNullable(departureOperationScheduleId);
	}

	public void clearDepartureOperationScheduleId() {
		this.departureOperationScheduleId = Optional.absent();
	}

	private Optional<Date> getOffTime = Optional.absent();

	public Optional<Date> getGetOffTime() {
		return wrapNull(getOffTime);
	}

	public void setGetOffTime(Optional<Date> getOffTime) {
		this.getOffTime = wrapNull(getOffTime);
	}

	public void setGetOffTime(Date getOffTime) {
		this.getOffTime = Optional.fromNullable(getOffTime);
	}

	public void clearGetOffTime() {
		this.getOffTime = Optional.absent();
	}

	private Optional<Boolean> getOffTimeOffline = Optional.absent();

	public Optional<Boolean> getGetOffTimeOffline() {
		return wrapNull(getOffTimeOffline);
	}

	public void setGetOffTimeOffline(Optional<Boolean> getOffTimeOffline) {
		this.getOffTimeOffline = wrapNull(getOffTimeOffline);
	}

	public void setGetOffTimeOffline(Boolean getOffTimeOffline) {
		this.getOffTimeOffline = Optional.fromNullable(getOffTimeOffline);
	}

	public void clearGetOffTimeOffline() {
		this.getOffTimeOffline = Optional.absent();
	}

	private Optional<Date> getOnTime = Optional.absent();

	public Optional<Date> getGetOnTime() {
		return wrapNull(getOnTime);
	}

	public void setGetOnTime(Optional<Date> getOnTime) {
		this.getOnTime = wrapNull(getOnTime);
	}

	public void setGetOnTime(Date getOnTime) {
		this.getOnTime = Optional.fromNullable(getOnTime);
	}

	public void clearGetOnTime() {
		this.getOnTime = Optional.absent();
	}

	private Optional<Boolean> getOnTimeOffline = Optional.absent();

	public Optional<Boolean> getGetOnTimeOffline() {
		return wrapNull(getOnTimeOffline);
	}

	public void setGetOnTimeOffline(Optional<Boolean> getOnTimeOffline) {
		this.getOnTimeOffline = wrapNull(getOnTimeOffline);
	}

	public void setGetOnTimeOffline(Boolean getOnTimeOffline) {
		this.getOnTimeOffline = Optional.fromNullable(getOnTimeOffline);
	}

	public void clearGetOnTimeOffline() {
		this.getOnTimeOffline = Optional.absent();
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	private Integer passengerCount = 0;

	public Integer getPassengerCount() {
		return wrapNull(passengerCount);
	}

	public void setPassengerCount(Integer passengerCount) {
		this.passengerCount = wrapNull(passengerCount);
	}

	private Optional<Integer> payment = Optional.absent();

	public Optional<Integer> getPayment() {
		return wrapNull(payment);
	}

	public void setPayment(Optional<Integer> payment) {
		this.payment = wrapNull(payment);
	}

	public void setPayment(Integer payment) {
		this.payment = Optional.fromNullable(payment);
	}

	public void clearPayment() {
		this.payment = Optional.absent();
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

	private Optional<Integer> serviceProviderId = Optional.absent();

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
		this.serviceProviderId = Optional.absent();
	}

	private Integer status = 0;

	public Integer getStatus() {
		return wrapNull(status);
	}

	public void setStatus(Integer status) {
		this.status = wrapNull(status);
	}

	private Optional<Date> timestamp = Optional.absent();

	public Optional<Date> getTimestamp() {
		return wrapNull(timestamp);
	}

	public void setTimestamp(Optional<Date> timestamp) {
		this.timestamp = wrapNull(timestamp);
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = Optional.fromNullable(timestamp);
	}

	public void clearTimestamp() {
		this.timestamp = Optional.absent();
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Optional<OperationSchedule> arrivalOperationSchedule = Optional.absent();

	public Optional<OperationSchedule> getArrivalOperationSchedule() {
		return wrapNull(arrivalOperationSchedule);
	}

	public void setArrivalOperationSchedule(Optional<OperationSchedule> arrivalOperationSchedule) {
		this.arrivalOperationSchedule = wrapNull(arrivalOperationSchedule);
	}

	public void setArrivalOperationSchedule(OperationSchedule arrivalOperationSchedule) {
		this.arrivalOperationSchedule = Optional.fromNullable(arrivalOperationSchedule);
	}

	public void clearArrivalOperationSchedule() {
		this.arrivalOperationSchedule = Optional.absent();
	}

	private Optional<OperationSchedule> departureOperationSchedule = Optional.absent();

	public Optional<OperationSchedule> getDepartureOperationSchedule() {
		return wrapNull(departureOperationSchedule);
	}

	public void setDepartureOperationSchedule(Optional<OperationSchedule> departureOperationSchedule) {
		this.departureOperationSchedule = wrapNull(departureOperationSchedule);
	}

	public void setDepartureOperationSchedule(OperationSchedule departureOperationSchedule) {
		this.departureOperationSchedule = Optional.fromNullable(departureOperationSchedule);
	}

	public void clearDepartureOperationSchedule() {
		this.departureOperationSchedule = Optional.absent();
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

	private Optional<ServiceProvider> serviceProvider = Optional.absent();

	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = Optional.fromNullable(serviceProvider);
	}

	public void clearServiceProvider() {
		this.serviceProvider = Optional.absent();
	}
}
