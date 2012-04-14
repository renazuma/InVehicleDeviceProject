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

public class OperationSchedule extends Model {
	private static final long serialVersionUID = 772960645335983575L;

	public OperationSchedule() {
	}

	public OperationSchedule(JSONObject jsonObject) throws JSONException, ParseException {
		setArrivalEstimate(parseDate(jsonObject, "arrival_estimate"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setDepartureEstimate(parseDate(jsonObject, "departure_estimate"));
		setId(parseInteger(jsonObject, "id"));
		setPlatformId(parseOptionalInteger(jsonObject, "platform_id"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setUnitAssignmentId(parseOptionalInteger(jsonObject, "unit_assignment_id"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setOperationRecord(OperationRecord.parse(jsonObject, "operation_record"));
		setPlatform(Platform.parse(jsonObject, "platform"));
		if (getPlatform().isPresent()) {
			setPlatformId(getPlatform().get().getId());
		}
		setReservationsAsArrival(Reservation.parseList(jsonObject, "reservations_as_arrival"));
		setReservationsAsDeparture(Reservation.parseList(jsonObject, "reservations_as_departure"));
		setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
		if (getServiceProvider().isPresent()) {
			setServiceProviderId(getServiceProvider().get().getId());
		}
	}

	public static Optional<OperationSchedule> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<OperationSchedule>absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<OperationSchedule> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.<OperationSchedule>of(new OperationSchedule(jsonObject));
	}

	public static LinkedList<OperationSchedule> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<OperationSchedule>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<OperationSchedule> parseList(JSONArray jsonArray) throws JSONException, ParseException {
		LinkedList<OperationSchedule> models = new LinkedList<OperationSchedule>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new OperationSchedule(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("arrival_estimate", toJSON(getArrivalEstimate()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
		jsonObject.put("departure_estimate", toJSON(getDepartureEstimate()));
		jsonObject.put("platform_id", toJSON(getPlatformId().orNull()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("unit_assignment_id", toJSON(getUnitAssignmentId().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));


		if (getPlatform().isPresent()) {
			jsonObject.put("platform_id", toJSON(getPlatform().get().getId()));
		}
		if (getReservationsAsArrival().size() > 0) {
	   		jsonObject.put("reservations_as_arrival", toJSON(getReservationsAsArrival()));
		}

		if (getReservationsAsDeparture().size() > 0) {
	   		jsonObject.put("reservations_as_departure", toJSON(getReservationsAsDeparture()));
		}


		if (getServiceProvider().isPresent()) {
			jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
		}
		return jsonObject;
	}

	private Date arrivalEstimate = new Date();

	public Date getArrivalEstimate() {
		return wrapNull(arrivalEstimate);
	}

	public void setArrivalEstimate(Date arrivalEstimate) {
		this.arrivalEstimate = wrapNull(arrivalEstimate);
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

	private Date departureEstimate = new Date();

	public Date getDepartureEstimate() {
		return wrapNull(departureEstimate);
	}

	public void setDepartureEstimate(Date departureEstimate) {
		this.departureEstimate = wrapNull(departureEstimate);
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	private Optional<Integer> platformId = Optional.<Integer>absent();

	public Optional<Integer> getPlatformId() {
		return wrapNull(platformId);
	}

	public void setPlatformId(Optional<Integer> platformId) {
		this.platformId = wrapNull(platformId);
	}

	public void setPlatformId(Integer platformId) {
		this.platformId = Optional.fromNullable(platformId);
	}

	public void clearPlatformId() {
		this.platformId = Optional.<Integer>absent();
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

	private Optional<Integer> unitAssignmentId = Optional.<Integer>absent();

	public Optional<Integer> getUnitAssignmentId() {
		return wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Optional<Integer> unitAssignmentId) {
		this.unitAssignmentId = wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Integer unitAssignmentId) {
		this.unitAssignmentId = Optional.fromNullable(unitAssignmentId);
	}

	public void clearUnitAssignmentId() {
		this.unitAssignmentId = Optional.<Integer>absent();
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Optional<OperationRecord> operationRecord = Optional.<OperationRecord>absent();

	public Optional<OperationRecord> getOperationRecord() {
		return wrapNull(operationRecord);
	}

	public void setOperationRecord(Optional<OperationRecord> operationRecord) {
		this.operationRecord = wrapNull(operationRecord);
	}

	public void setOperationRecord(OperationRecord operationRecord) {
		this.operationRecord = Optional.<OperationRecord>fromNullable(operationRecord);
	}

	public void clearOperationRecord() {
		this.operationRecord = Optional.<OperationRecord>absent();
	}

	private Optional<Platform> platform = Optional.<Platform>absent();

	public Optional<Platform> getPlatform() {
		return wrapNull(platform);
	}

	public void setPlatform(Optional<Platform> platform) {
		this.platform = wrapNull(platform);
	}

	public void setPlatform(Platform platform) {
		this.platform = Optional.<Platform>fromNullable(platform);
	}

	public void clearPlatform() {
		this.platform = Optional.<Platform>absent();
	}

	private LinkedList<Reservation> reservationsAsArrival = new LinkedList<Reservation>();

	public List<Reservation> getReservationsAsArrival() {
		return new LinkedList<Reservation>(wrapNull(reservationsAsArrival));
	}

	public void setReservationsAsArrival(List<Reservation> reservationsAsArrival) {
		this.reservationsAsArrival = new LinkedList<Reservation>(wrapNull(reservationsAsArrival));
	}

	public void clearReservationsAsArrival() {
		this.reservationsAsArrival = new LinkedList<Reservation>();
	}

	private LinkedList<Reservation> reservationsAsDeparture = new LinkedList<Reservation>();

	public List<Reservation> getReservationsAsDeparture() {
		return new LinkedList<Reservation>(wrapNull(reservationsAsDeparture));
	}

	public void setReservationsAsDeparture(List<Reservation> reservationsAsDeparture) {
		this.reservationsAsDeparture = new LinkedList<Reservation>(wrapNull(reservationsAsDeparture));
	}

	public void clearReservationsAsDeparture() {
		this.reservationsAsDeparture = new LinkedList<Reservation>();
	}

	private Optional<ServiceProvider> serviceProvider = Optional.<ServiceProvider>absent();

	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = Optional.<ServiceProvider>fromNullable(serviceProvider);
	}

	public void clearServiceProvider() {
		this.serviceProvider = Optional.<ServiceProvider>absent();
	}
}
