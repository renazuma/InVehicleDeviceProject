package com.kogasoftware.odt.invehicledevice.apiclient.model.base;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;
import com.kogasoftware.odt.apiclient.DefaultApiClient;
import com.kogasoftware.odt.apiclient.ApiClient.ResponseConverter;
import com.kogasoftware.odt.invehicledevice.apiclient.model.*;

@SuppressWarnings("unused")
public abstract class PassengerRecordBase extends Model {
	private static final long serialVersionUID = 7301843552054789332L;
	public static final ResponseConverter<PassengerRecord> RESPONSE_CONVERTER = new ResponseConverter<PassengerRecord>() {
		@Override
		public PassengerRecord convert(byte[] rawResponse) throws JSONException {
			return parse(DefaultApiClient.parseJSONObject(rawResponse));
		}
	};
	public static final ResponseConverter<List<PassengerRecord>> LIST_RESPONSE_CONVERTER = new ResponseConverter<List<PassengerRecord>>() {
		@Override
		public List<PassengerRecord> convert(byte[] rawResponse) throws JSONException {
			return parseList(DefaultApiClient.parseJSONArray(rawResponse));
		}
	};
	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}
	@Override
	public void fill(JSONObject jsonObject) throws JSONException {
		setAge(parseOptionalInteger(jsonObject, "age"));
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
		setUserId(parseOptionalInteger(jsonObject, "user_id"));
		setArrivalOperationSchedule(OperationSchedule.parse(jsonObject, "arrival_operation_schedule"));
		setDepartureOperationSchedule(OperationSchedule.parse(jsonObject, "departure_operation_schedule"));
		setReservation(Reservation.parse(jsonObject, "reservation"));
		setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
		setUser(User.parse(jsonObject, "user"));

		setUpdatedAt(parseDate(jsonObject, "updated_at"));
	}

	public static Optional<PassengerRecord> parse(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return Optional.of(parse(jsonObject.getJSONObject(key)));
	}

	public static PassengerRecord parse(JSONObject jsonObject) throws JSONException {
		PassengerRecord model = new PassengerRecord();
		model.fill(jsonObject);
		return model;
	}

	public static LinkedList<PassengerRecord> parseList(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return new LinkedList<PassengerRecord>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<PassengerRecord> parseList(JSONArray jsonArray) throws JSONException {
		LinkedList<PassengerRecord> models = new LinkedList<PassengerRecord>();
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
		jsonObject.put("age", toJSON(getAge()));
		jsonObject.put("arrival_operation_schedule_id", toJSON(getArrivalOperationScheduleId()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt()));
		jsonObject.put("departure_operation_schedule_id", toJSON(getDepartureOperationScheduleId()));
		jsonObject.put("get_off_time", toJSON(getGetOffTime()));
		jsonObject.put("get_off_time_offline", toJSON(getGetOffTimeOffline()));
		jsonObject.put("get_on_time", toJSON(getGetOnTime()));
		jsonObject.put("get_on_time_offline", toJSON(getGetOnTimeOffline()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("passenger_count", toJSON(getPassengerCount()));
		jsonObject.put("payment", toJSON(getPayment()));
		jsonObject.put("reservation_id", toJSON(getReservationId()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId()));
		jsonObject.put("status", toJSON(getStatus()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("user_id", toJSON(getUserId()));
		if (getArrivalOperationSchedule().isPresent()) {
			if (recursive) {
				jsonObject.put("arrival_operation_schedule", getArrivalOperationSchedule().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("arrival_operation_schedule_id", toJSON(getArrivalOperationSchedule().get().getId()));
			}
		}
		if (getDepartureOperationSchedule().isPresent()) {
			if (recursive) {
				jsonObject.put("departure_operation_schedule", getDepartureOperationSchedule().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("departure_operation_schedule_id", toJSON(getDepartureOperationSchedule().get().getId()));
			}
		}
		if (getReservation().isPresent()) {
			if (recursive) {
				jsonObject.put("reservation", getReservation().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("reservation_id", toJSON(getReservation().get().getId()));
			}
		}
		if (getServiceProvider().isPresent()) {
			if (recursive) {
				jsonObject.put("service_provider", getServiceProvider().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
			}
		}
		if (getUser().isPresent()) {
			if (recursive) {
				jsonObject.put("user", getUser().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("user_id", toJSON(getUser().get().getId()));
			}
		}
		return jsonObject;
	}

	@Override
	public PassengerRecord cloneByJSON() throws JSONException {
		return parse(toJSONObject(true));
	}

	private Optional<Integer> age = Optional.absent();

	public Optional<Integer> getAge() {
		return wrapNull(age);
	}

	public void setAge(Optional<Integer> age) {
		refreshUpdatedAt();
		this.age = wrapNull(age);
	}

	public void setAge(Integer age) {
		setAge(Optional.fromNullable(age));
	}

	public void clearAge() {
		setAge(Optional.<Integer>absent());
	}

	private Optional<Integer> arrivalOperationScheduleId = Optional.absent();

	public Optional<Integer> getArrivalOperationScheduleId() {
		return wrapNull(arrivalOperationScheduleId);
	}

	public void setArrivalOperationScheduleId(Optional<Integer> arrivalOperationScheduleId) {
		refreshUpdatedAt();
		this.arrivalOperationScheduleId = wrapNull(arrivalOperationScheduleId);
	}

	public void setArrivalOperationScheduleId(Integer arrivalOperationScheduleId) {
		setArrivalOperationScheduleId(Optional.fromNullable(arrivalOperationScheduleId));
	}

	public void clearArrivalOperationScheduleId() {
		setArrivalOperationScheduleId(Optional.<Integer>absent());
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		refreshUpdatedAt();
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> deletedAt = Optional.absent();

	public Optional<Date> getDeletedAt() {
		return wrapNull(deletedAt);
	}

	public void setDeletedAt(Optional<Date> deletedAt) {
		refreshUpdatedAt();
		this.deletedAt = wrapNull(deletedAt);
	}

	public void setDeletedAt(Date deletedAt) {
		setDeletedAt(Optional.fromNullable(deletedAt));
	}

	public void clearDeletedAt() {
		setDeletedAt(Optional.<Date>absent());
	}

	private Optional<Integer> departureOperationScheduleId = Optional.absent();

	public Optional<Integer> getDepartureOperationScheduleId() {
		return wrapNull(departureOperationScheduleId);
	}

	public void setDepartureOperationScheduleId(Optional<Integer> departureOperationScheduleId) {
		refreshUpdatedAt();
		this.departureOperationScheduleId = wrapNull(departureOperationScheduleId);
	}

	public void setDepartureOperationScheduleId(Integer departureOperationScheduleId) {
		setDepartureOperationScheduleId(Optional.fromNullable(departureOperationScheduleId));
	}

	public void clearDepartureOperationScheduleId() {
		setDepartureOperationScheduleId(Optional.<Integer>absent());
	}

	private Optional<Date> getOffTime = Optional.absent();

	public Optional<Date> getGetOffTime() {
		return wrapNull(getOffTime);
	}

	public void setGetOffTime(Optional<Date> getOffTime) {
		refreshUpdatedAt();
		this.getOffTime = wrapNull(getOffTime);
	}

	public void setGetOffTime(Date getOffTime) {
		setGetOffTime(Optional.fromNullable(getOffTime));
	}

	public void clearGetOffTime() {
		setGetOffTime(Optional.<Date>absent());
	}

	private Optional<Boolean> getOffTimeOffline = Optional.absent();

	public Optional<Boolean> getGetOffTimeOffline() {
		return wrapNull(getOffTimeOffline);
	}

	public void setGetOffTimeOffline(Optional<Boolean> getOffTimeOffline) {
		refreshUpdatedAt();
		this.getOffTimeOffline = wrapNull(getOffTimeOffline);
	}

	public void setGetOffTimeOffline(Boolean getOffTimeOffline) {
		setGetOffTimeOffline(Optional.fromNullable(getOffTimeOffline));
	}

	public void clearGetOffTimeOffline() {
		setGetOffTimeOffline(Optional.<Boolean>absent());
	}

	private Optional<Date> getOnTime = Optional.absent();

	public Optional<Date> getGetOnTime() {
		return wrapNull(getOnTime);
	}

	public void setGetOnTime(Optional<Date> getOnTime) {
		refreshUpdatedAt();
		this.getOnTime = wrapNull(getOnTime);
	}

	public void setGetOnTime(Date getOnTime) {
		setGetOnTime(Optional.fromNullable(getOnTime));
	}

	public void clearGetOnTime() {
		setGetOnTime(Optional.<Date>absent());
	}

	private Optional<Boolean> getOnTimeOffline = Optional.absent();

	public Optional<Boolean> getGetOnTimeOffline() {
		return wrapNull(getOnTimeOffline);
	}

	public void setGetOnTimeOffline(Optional<Boolean> getOnTimeOffline) {
		refreshUpdatedAt();
		this.getOnTimeOffline = wrapNull(getOnTimeOffline);
	}

	public void setGetOnTimeOffline(Boolean getOnTimeOffline) {
		setGetOnTimeOffline(Optional.fromNullable(getOnTimeOffline));
	}

	public void clearGetOnTimeOffline() {
		setGetOnTimeOffline(Optional.<Boolean>absent());
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		refreshUpdatedAt();
		this.id = wrapNull(id);
	}

	private Integer passengerCount = 0;

	public Integer getPassengerCount() {
		return wrapNull(passengerCount);
	}

	public void setPassengerCount(Integer passengerCount) {
		refreshUpdatedAt();
		this.passengerCount = wrapNull(passengerCount);
	}

	private Optional<Integer> payment = Optional.absent();

	public Optional<Integer> getPayment() {
		return wrapNull(payment);
	}

	public void setPayment(Optional<Integer> payment) {
		refreshUpdatedAt();
		this.payment = wrapNull(payment);
	}

	public void setPayment(Integer payment) {
		setPayment(Optional.fromNullable(payment));
	}

	public void clearPayment() {
		setPayment(Optional.<Integer>absent());
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

	private Optional<Integer> serviceProviderId = Optional.absent();

	public Optional<Integer> getServiceProviderId() {
		return wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Optional<Integer> serviceProviderId) {
		refreshUpdatedAt();
		this.serviceProviderId = wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Integer serviceProviderId) {
		setServiceProviderId(Optional.fromNullable(serviceProviderId));
	}

	public void clearServiceProviderId() {
		setServiceProviderId(Optional.<Integer>absent());
	}

	private Integer status = 0;

	public Integer getStatus() {
		return wrapNull(status);
	}

	public void setStatus(Integer status) {
		refreshUpdatedAt();
		this.status = wrapNull(status);
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Optional<Integer> userId = Optional.absent();

	public Optional<Integer> getUserId() {
		return wrapNull(userId);
	}

	public void setUserId(Optional<Integer> userId) {
		refreshUpdatedAt();
		this.userId = wrapNull(userId);
	}

	public void setUserId(Integer userId) {
		setUserId(Optional.fromNullable(userId));
	}

	public void clearUserId() {
		setUserId(Optional.<Integer>absent());
	}

	private Optional<OperationSchedule> arrivalOperationSchedule = Optional.<OperationSchedule>absent();

	public Optional<OperationSchedule> getArrivalOperationSchedule() {
		return wrapNull(arrivalOperationSchedule);
	}

	public void setArrivalOperationSchedule(Optional<OperationSchedule> arrivalOperationSchedule) {
		this.arrivalOperationSchedule = wrapNull(arrivalOperationSchedule);
	}

	public void setArrivalOperationSchedule(OperationSchedule arrivalOperationSchedule) {
		setArrivalOperationSchedule(Optional.fromNullable(arrivalOperationSchedule));
	}

	public void clearArrivalOperationSchedule() {
		setArrivalOperationSchedule(Optional.<OperationSchedule>absent());
	}

	private Optional<OperationSchedule> departureOperationSchedule = Optional.<OperationSchedule>absent();

	public Optional<OperationSchedule> getDepartureOperationSchedule() {
		return wrapNull(departureOperationSchedule);
	}

	public void setDepartureOperationSchedule(Optional<OperationSchedule> departureOperationSchedule) {
		this.departureOperationSchedule = wrapNull(departureOperationSchedule);
	}

	public void setDepartureOperationSchedule(OperationSchedule departureOperationSchedule) {
		setDepartureOperationSchedule(Optional.fromNullable(departureOperationSchedule));
	}

	public void clearDepartureOperationSchedule() {
		setDepartureOperationSchedule(Optional.<OperationSchedule>absent());
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

	private Optional<ServiceProvider> serviceProvider = Optional.<ServiceProvider>absent();

	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		setServiceProvider(Optional.fromNullable(serviceProvider));
	}

	public void clearServiceProvider() {
		setServiceProvider(Optional.<ServiceProvider>absent());
	}

	private Optional<User> user = Optional.<User>absent();

	public Optional<User> getUser() {
		return wrapNull(user);
	}

	public void setUser(Optional<User> user) {
		this.user = wrapNull(user);
	}

	public void setUser(User user) {
		setUser(Optional.fromNullable(user));
	}

	public void clearUser() {
		setUser(Optional.<User>absent());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(age)
			.append(arrivalOperationScheduleId)
			.append(createdAt)
			.append(deletedAt)
			.append(departureOperationScheduleId)
			.append(getOffTime)
			.append(getOffTimeOffline)
			.append(getOnTime)
			.append(getOnTimeOffline)
			.append(id)
			.append(passengerCount)
			.append(payment)
			.append(reservationId)
			.append(serviceProviderId)
			.append(status)
			.append(updatedAt)
			.append(userId)
			.append(arrivalOperationSchedule)
			.append(departureOperationSchedule)
			.append(reservation)
			.append(serviceProvider)
			.append(user)
			.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof PassengerRecordBase)) {
			return false;
		}
		PassengerRecordBase other = (PassengerRecordBase) obj;
		return new EqualsBuilder()
			.append(age, other.age)
			.append(arrivalOperationScheduleId, other.arrivalOperationScheduleId)
			.append(createdAt, other.createdAt)
			.append(deletedAt, other.deletedAt)
			.append(departureOperationScheduleId, other.departureOperationScheduleId)
			.append(getOffTime, other.getOffTime)
			.append(getOffTimeOffline, other.getOffTimeOffline)
			.append(getOnTime, other.getOnTime)
			.append(getOnTimeOffline, other.getOnTimeOffline)
			.append(id, other.id)
			.append(passengerCount, other.passengerCount)
			.append(payment, other.payment)
			.append(reservationId, other.reservationId)
			.append(serviceProviderId, other.serviceProviderId)
			.append(status, other.status)
			.append(updatedAt, other.updatedAt)
			.append(userId, other.userId)
			.append(arrivalOperationSchedule, other.arrivalOperationSchedule)
			.append(departureOperationSchedule, other.departureOperationSchedule)
			.append(reservation, other.reservation)
			.append(serviceProvider, other.serviceProvider)
			.append(user, other.user)
			.isEquals();
	}
}
