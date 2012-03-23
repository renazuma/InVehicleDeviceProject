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

public class OperationSchedule extends Model {
	private static final long serialVersionUID = 4511002219572134754L;
	public static final String JSON_NAME = "operation_schedule";
	public static final String CONTROLLER_NAME = "operation_schedules";

	public OperationSchedule() {
	}

	public OperationSchedule(JSONObject jsonObject) throws JSONException, ParseException {
		setArrivalEstimate(parseDate(jsonObject, "arrival_estimate"));
		setArrivedAt(parseOptionalDate(jsonObject, "arrived_at"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setDepartedAt(parseOptionalDate(jsonObject, "departed_at"));
		setDepartureEstimate(parseDate(jsonObject, "departure_estimate"));
		setId(parseInteger(jsonObject, "id"));
		setPlatformId(parseOptionalInteger(jsonObject, "platform_id"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setUnitAssignmentId(parseOptionalInteger(jsonObject, "unit_assignment_id"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setPlatform(Platform.parse(jsonObject, "platform"));
		if (getPlatform().isPresent()) {
			setPlatformId(getPlatform().get().getId());
		}
		setReservationsAsArrival(Reservation.parseList(jsonObject, "reservations_as_arrival"));
		setReservationsAsDeparture(Reservation.parseList(jsonObject, "reservations_as_departure"));
	}

	public static Optional<OperationSchedule> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<OperationSchedule>absent();
		}
		return Optional.<OperationSchedule>of(new OperationSchedule(jsonObject.getJSONObject(key)));
	}

	public static List<OperationSchedule> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<OperationSchedule>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		List<OperationSchedule> models = new LinkedList<OperationSchedule>();
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
		jsonObject.put("arrived_at", toJSON(getArrivedAt().orNull()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
		jsonObject.put("departed_at", toJSON(getDepartedAt().orNull()));
		jsonObject.put("departure_estimate", toJSON(getDepartureEstimate()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("platform_id", toJSON(getPlatformId().orNull()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("unit_assignment_id", toJSON(getUnitAssignmentId().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("platform", toJSON(getPlatform()));
		if (getPlatform().isPresent()) {
			jsonObject.put("platform_id", toJSON(getPlatform().get().getId()));
		}
		jsonObject.put("reservations_as_arrival", toJSON(getReservationsAsArrival()));
		jsonObject.put("reservations_as_departure", toJSON(getReservationsAsDeparture()));
		return jsonObject;
	}

	private Date arrivalEstimate = new Date();

	public Date getArrivalEstimate() {
		return wrapNull(arrivalEstimate);
	}

	public void setArrivalEstimate(Date arrivalEstimate) {
		this.arrivalEstimate = wrapNull(arrivalEstimate);
	}

	private Optional<Date> arrivedAt = Optional.<Date>absent();

	public Optional<Date> getArrivedAt() {
		return wrapNull(arrivedAt);
	}

	public void setArrivedAt(Optional<Date> arrivedAt) {
		this.arrivedAt = wrapNull(arrivedAt);
	}

	public void setArrivedAt(Date arrivedAt) {
		this.arrivedAt = Optional.fromNullable(arrivedAt);
	}

	public void clearArrivedAt() {
		this.arrivedAt = Optional.<Date>absent();
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

	private Optional<Date> departedAt = Optional.<Date>absent();

	public Optional<Date> getDepartedAt() {
		return wrapNull(departedAt);
	}

	public void setDepartedAt(Optional<Date> departedAt) {
		this.departedAt = wrapNull(departedAt);
	}

	public void setDepartedAt(Date departedAt) {
		this.departedAt = Optional.fromNullable(departedAt);
	}

	public void clearDepartedAt() {
		this.departedAt = Optional.<Date>absent();
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

	private List<Reservation> reservationsAsArrival = new LinkedList<Reservation>();

	public List<Reservation> getReservationsAsArrival() {
		return new LinkedList<Reservation>(wrapNull(reservationsAsArrival));
	}

	public void setReservationsAsArrival(List<Reservation> reservationsAsArrival) {
		this.reservationsAsArrival = new LinkedList<Reservation>(wrapNull(reservationsAsArrival));
	}

	public void clearReservationsAsArrival() {
		this.reservationsAsArrival = new LinkedList<Reservation>();
	}

	private List<Reservation> reservationsAsDeparture = new LinkedList<Reservation>();

	public List<Reservation> getReservationsAsDeparture() {
		return new LinkedList<Reservation>(wrapNull(reservationsAsDeparture));
	}

	public void setReservationsAsDeparture(List<Reservation> reservationsAsDeparture) {
		this.reservationsAsDeparture = new LinkedList<Reservation>(wrapNull(reservationsAsDeparture));
	}

	public void clearReservationsAsDeparture() {
		this.reservationsAsDeparture = new LinkedList<Reservation>();
	}
}
