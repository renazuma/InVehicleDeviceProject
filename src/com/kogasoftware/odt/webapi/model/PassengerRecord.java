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

public class PassengerRecord extends Model {
	private static final long serialVersionUID = 1373556092440357354L;
	public static final String JSON_NAME = "passenger_record";
	public static final String CONTROLLER_NAME = "passenger_records";

	public static class URL {
		public static final String ROOT = "/" + CONTROLLER_NAME;
	}

	public PassengerRecord() {
	}

	public PassengerRecord(JSONObject jsonObject) throws JSONException, ParseException {
		setArrivalOperationScheduleId(parseOptionalLong(jsonObject, "arrival_operation_schedule_id"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setDepartureOperationScheduleId(parseLong(jsonObject, "departure_operation_schedule_id"));
		setGetOffTime(parseOptionalDate(jsonObject, "get_off_time"));
		setGetOnTime(parseDate(jsonObject, "get_on_time"));
		setHead(parseLong(jsonObject, "head"));
		setId(parseLong(jsonObject, "id"));
		setPayment(parseOptionalBoolean(jsonObject, "payment"));
		setReservationId(parseOptionalLong(jsonObject, "reservation_id"));
		setServiceProviderId(parseOptionalLong(jsonObject, "service_provider_id"));
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
	}

	public static Optional<PassengerRecord> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<PassengerRecord>absent();
		}
		return Optional.<PassengerRecord>of(new PassengerRecord(jsonObject.getJSONObject(key)));
	}

	public static List<PassengerRecord> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<PassengerRecord>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		List<PassengerRecord> models = new LinkedList<PassengerRecord>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new PassengerRecord(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	public static class ResponseConverter implements
			WebAPI.ResponseConverter<PassengerRecord> {
		@Override
		public PassengerRecord convert(byte[] rawResponse) throws JSONException, ParseException {
			return new PassengerRecord(new JSONObject(new String(rawResponse)));
		}
	}

	public static class ListResponseConverter implements
			WebAPI.ResponseConverter<List<PassengerRecord>> {
		@Override
		public List<PassengerRecord> convert(byte[] rawResponse) throws JSONException,
				ParseException {
			JSONArray array = new JSONArray(new String(rawResponse));
			List<PassengerRecord> models = new LinkedList<PassengerRecord>();
			for (Integer i = 0; i < array.length(); ++i) {
				if (array.isNull(i)) {
					continue;
				}
				JSONObject object = array.getJSONObject(i);
				models.add(new PassengerRecord(object));
			}
			return models;
		}
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("arrival_operation_schedule_id", toJSON(getArrivalOperationScheduleId().orNull()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
		jsonObject.put("departure_operation_schedule_id", toJSON(getDepartureOperationScheduleId()));
		jsonObject.put("get_off_time", toJSON(getGetOffTime().orNull()));
		jsonObject.put("get_on_time", toJSON(getGetOnTime()));
		jsonObject.put("head", toJSON(getHead()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("payment", toJSON(getPayment().orNull()));
		jsonObject.put("reservation_id", toJSON(getReservationId().orNull()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("timestamp", toJSON(getTimestamp().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("arrival_operation_schedule", toJSON(getArrivalOperationSchedule()));
		if (getArrivalOperationSchedule().isPresent()) {
			jsonObject.put("arrival_operation_schedule_id", toJSON(getArrivalOperationSchedule().get().getId()));
		}
		jsonObject.put("departure_operation_schedule", toJSON(getDepartureOperationSchedule()));
		if (getDepartureOperationSchedule().isPresent()) {
			jsonObject.put("departure_operation_schedule_id", toJSON(getDepartureOperationSchedule().get().getId()));
		}
		jsonObject.put("reservation", toJSON(getReservation()));
		if (getReservation().isPresent()) {
			jsonObject.put("reservation_id", toJSON(getReservation().get().getId()));
		}
		return jsonObject;
	}

	private Optional<Long> arrivalOperationScheduleId = Optional.<Long>absent();

	public Optional<Long> getArrivalOperationScheduleId() {
		return wrapNull(arrivalOperationScheduleId);
	}

	public void setArrivalOperationScheduleId(Optional<Long> arrivalOperationScheduleId) {
		this.arrivalOperationScheduleId = wrapNull(arrivalOperationScheduleId);
	}

	public void setArrivalOperationScheduleId(Long arrivalOperationScheduleId) {
		this.arrivalOperationScheduleId = Optional.fromNullable(arrivalOperationScheduleId);
	}

	public void clearArrivalOperationScheduleId() {
		this.arrivalOperationScheduleId = Optional.<Long>absent();
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

	private Long departureOperationScheduleId = 0L;

	public Long getDepartureOperationScheduleId() {
		return wrapNull(departureOperationScheduleId);
	}

	public void setDepartureOperationScheduleId(Long departureOperationScheduleId) {
		this.departureOperationScheduleId = wrapNull(departureOperationScheduleId);
	}

	private Optional<Date> getOffTime = Optional.<Date>absent();

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
		this.getOffTime = Optional.<Date>absent();
	}

	private Date getOnTime = new Date();

	public Date getGetOnTime() {
		return wrapNull(getOnTime);
	}

	public void setGetOnTime(Date getOnTime) {
		this.getOnTime = wrapNull(getOnTime);
	}

	private Long head = 0L;

	public Long getHead() {
		return wrapNull(head);
	}

	public void setHead(Long head) {
		this.head = wrapNull(head);
	}

	private Long id = 0L;

	public Long getId() {
		return wrapNull(id);
	}

	public void setId(Long id) {
		this.id = wrapNull(id);
	}

	private Optional<Boolean> payment = Optional.<Boolean>absent();

	public Optional<Boolean> getPayment() {
		return wrapNull(payment);
	}

	public void setPayment(Optional<Boolean> payment) {
		this.payment = wrapNull(payment);
	}

	public void setPayment(Boolean payment) {
		this.payment = Optional.fromNullable(payment);
	}

	public void clearPayment() {
		this.payment = Optional.<Boolean>absent();
	}

	private Optional<Long> reservationId = Optional.<Long>absent();

	public Optional<Long> getReservationId() {
		return wrapNull(reservationId);
	}

	public void setReservationId(Optional<Long> reservationId) {
		this.reservationId = wrapNull(reservationId);
	}

	public void setReservationId(Long reservationId) {
		this.reservationId = Optional.fromNullable(reservationId);
	}

	public void clearReservationId() {
		this.reservationId = Optional.<Long>absent();
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

	private Optional<Date> timestamp = Optional.<Date>absent();

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
		this.timestamp = Optional.<Date>absent();
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Optional<OperationSchedule> arrivalOperationSchedule = Optional.<OperationSchedule>absent();

	public Optional<OperationSchedule> getArrivalOperationSchedule() {
		return wrapNull(arrivalOperationSchedule);
	}

	public void setArrivalOperationSchedule(Optional<OperationSchedule> arrivalOperationSchedule) {
		this.arrivalOperationSchedule = wrapNull(arrivalOperationSchedule);
	}

	public void setArrivalOperationSchedule(OperationSchedule arrivalOperationSchedule) {
		this.arrivalOperationSchedule = Optional.<OperationSchedule>fromNullable(arrivalOperationSchedule);
	}

	public void clearArrivalOperationSchedule() {
		this.arrivalOperationSchedule = Optional.<OperationSchedule>absent();
	}

	private Optional<OperationSchedule> departureOperationSchedule = Optional.<OperationSchedule>absent();

	public Optional<OperationSchedule> getDepartureOperationSchedule() {
		return wrapNull(departureOperationSchedule);
	}

	public void setDepartureOperationSchedule(Optional<OperationSchedule> departureOperationSchedule) {
		this.departureOperationSchedule = wrapNull(departureOperationSchedule);
	}

	public void setDepartureOperationSchedule(OperationSchedule departureOperationSchedule) {
		this.departureOperationSchedule = Optional.<OperationSchedule>fromNullable(departureOperationSchedule);
	}

	public void clearDepartureOperationSchedule() {
		this.departureOperationSchedule = Optional.<OperationSchedule>absent();
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
