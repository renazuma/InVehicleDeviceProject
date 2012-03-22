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

public class Demand extends Model {
	private static final long serialVersionUID = 8256467706956261249L;
	public static final String JSON_NAME = "demand";
	public static final String CONTROLLER_NAME = "demands";

	public static class URL {
		public static final String ROOT = "/" + CONTROLLER_NAME;
		public static final String CREATE = "/" + CONTROLLER_NAME + "/create";
		public static final String DESTROY = "/" + CONTROLLER_NAME + "/destroy";
		public static final String EDIT = "/" + CONTROLLER_NAME + "/edit";
		public static final String INDEX = "/" + CONTROLLER_NAME + "/index";
		public static final String NEW = "/" + CONTROLLER_NAME + "/new";
		public static final String SHOW = "/" + CONTROLLER_NAME + "/show";
		public static final String UPDATE = "/" + CONTROLLER_NAME + "/update";
	}

	public Demand() {
	}

	public Demand(JSONObject jsonObject) throws JSONException, ParseException {
		setArrivalPlatformId(parseOptionalLong(jsonObject, "arrival_platform_id"));
		setArrivalTime(parseOptionalDate(jsonObject, "arrival_time"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setDeparturePlatformId(parseOptionalLong(jsonObject, "departure_platform_id"));
		setDepartureTime(parseOptionalDate(jsonObject, "departure_time"));
		setHead(parseLong(jsonObject, "head"));
		setId(parseLong(jsonObject, "id"));
		setMemo(parseOptionalString(jsonObject, "memo"));
		setServiceProviderId(parseOptionalLong(jsonObject, "service_provider_id"));
		setStoppageTime(parseOptionalLong(jsonObject, "stoppage_time"));
		setUnitAssignmentId(parseOptionalLong(jsonObject, "unit_assignment_id"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setUserId(parseLong(jsonObject, "user_id"));
		setArrivalPlatform(Platform.parse(jsonObject, "arrival_platform"));
		if (getArrivalPlatform().isPresent()) {
			setArrivalPlatformId(getArrivalPlatform().get().getId());
		}
		setDeparturePlatform(Platform.parse(jsonObject, "departure_platform"));
		if (getDeparturePlatform().isPresent()) {
			setDeparturePlatformId(getDeparturePlatform().get().getId());
		}
		setReservation(Reservation.parse(jsonObject, "reservation"));
		setUser(User.parse(jsonObject, "user"));
		if (getUser().isPresent()) {
			setUserId(getUser().get().getId());
		}
	}

	public static Optional<Demand> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<Demand>absent();
		}
		return Optional.<Demand>of(new Demand(jsonObject.getJSONObject(key)));
	}

	public static List<Demand> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<Demand>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		List<Demand> models = new LinkedList<Demand>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new Demand(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	public static class ResponseConverter implements
			WebAPI.ResponseConverter<Demand> {
		@Override
		public Demand convert(byte[] rawResponse) throws JSONException, ParseException {
			return new Demand(new JSONObject(new String(rawResponse)));
		}
	}

	public static class ListResponseConverter implements
			WebAPI.ResponseConverter<List<Demand>> {
		@Override
		public List<Demand> convert(byte[] rawResponse) throws JSONException,
				ParseException {
			JSONArray array = new JSONArray(new String(rawResponse));
			List<Demand> models = new LinkedList<Demand>();
			for (Integer i = 0; i < array.length(); ++i) {
				if (array.isNull(i)) {
					continue;
				}
				JSONObject object = array.getJSONObject(i);
				models.add(new Demand(object));
			}
			return models;
		}
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("arrival_platform_id", toJSON(getArrivalPlatformId().orNull()));
		jsonObject.put("arrival_time", toJSON(getArrivalTime().orNull()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
		jsonObject.put("departure_platform_id", toJSON(getDeparturePlatformId().orNull()));
		jsonObject.put("departure_time", toJSON(getDepartureTime().orNull()));
		jsonObject.put("head", toJSON(getHead()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("memo", toJSON(getMemo().orNull()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("stoppage_time", toJSON(getStoppageTime().orNull()));
		jsonObject.put("unit_assignment_id", toJSON(getUnitAssignmentId().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("user_id", toJSON(getUserId()));
		jsonObject.put("arrival_platform", toJSON(getArrivalPlatform()));
		if (getArrivalPlatform().isPresent()) {
			jsonObject.put("arrival_platform_id", toJSON(getArrivalPlatform().get().getId()));
		}
		jsonObject.put("departure_platform", toJSON(getDeparturePlatform()));
		if (getDeparturePlatform().isPresent()) {
			jsonObject.put("departure_platform_id", toJSON(getDeparturePlatform().get().getId()));
		}
		jsonObject.put("reservation", toJSON(getReservation()));
		jsonObject.put("user", toJSON(getUser()));
		if (getUser().isPresent()) {
			jsonObject.put("user_id", toJSON(getUser().get().getId()));
		}
		return jsonObject;
	}

	private Optional<Long> arrivalPlatformId = Optional.<Long>absent();

	public Optional<Long> getArrivalPlatformId() {
		return wrapNull(arrivalPlatformId);
	}

	public void setArrivalPlatformId(Optional<Long> arrivalPlatformId) {
		this.arrivalPlatformId = wrapNull(arrivalPlatformId);
	}

	public void setArrivalPlatformId(Long arrivalPlatformId) {
		this.arrivalPlatformId = Optional.fromNullable(arrivalPlatformId);
	}

	public void clearArrivalPlatformId() {
		this.arrivalPlatformId = Optional.<Long>absent();
	}

	private Optional<Date> arrivalTime = Optional.<Date>absent();

	public Optional<Date> getArrivalTime() {
		return wrapNull(arrivalTime);
	}

	public void setArrivalTime(Optional<Date> arrivalTime) {
		this.arrivalTime = wrapNull(arrivalTime);
	}

	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = Optional.fromNullable(arrivalTime);
	}

	public void clearArrivalTime() {
		this.arrivalTime = Optional.<Date>absent();
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

	private Optional<Long> departurePlatformId = Optional.<Long>absent();

	public Optional<Long> getDeparturePlatformId() {
		return wrapNull(departurePlatformId);
	}

	public void setDeparturePlatformId(Optional<Long> departurePlatformId) {
		this.departurePlatformId = wrapNull(departurePlatformId);
	}

	public void setDeparturePlatformId(Long departurePlatformId) {
		this.departurePlatformId = Optional.fromNullable(departurePlatformId);
	}

	public void clearDeparturePlatformId() {
		this.departurePlatformId = Optional.<Long>absent();
	}

	private Optional<Date> departureTime = Optional.<Date>absent();

	public Optional<Date> getDepartureTime() {
		return wrapNull(departureTime);
	}

	public void setDepartureTime(Optional<Date> departureTime) {
		this.departureTime = wrapNull(departureTime);
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = Optional.fromNullable(departureTime);
	}

	public void clearDepartureTime() {
		this.departureTime = Optional.<Date>absent();
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

	private Optional<String> memo = Optional.<String>absent();

	public Optional<String> getMemo() {
		return wrapNull(memo);
	}

	public void setMemo(Optional<String> memo) {
		this.memo = wrapNull(memo);
	}

	public void setMemo(String memo) {
		this.memo = Optional.fromNullable(memo);
	}

	public void clearMemo() {
		this.memo = Optional.<String>absent();
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

	private Optional<Long> stoppageTime = Optional.<Long>absent();

	public Optional<Long> getStoppageTime() {
		return wrapNull(stoppageTime);
	}

	public void setStoppageTime(Optional<Long> stoppageTime) {
		this.stoppageTime = wrapNull(stoppageTime);
	}

	public void setStoppageTime(Long stoppageTime) {
		this.stoppageTime = Optional.fromNullable(stoppageTime);
	}

	public void clearStoppageTime() {
		this.stoppageTime = Optional.<Long>absent();
	}

	private Optional<Long> unitAssignmentId = Optional.<Long>absent();

	public Optional<Long> getUnitAssignmentId() {
		return wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Optional<Long> unitAssignmentId) {
		this.unitAssignmentId = wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Long unitAssignmentId) {
		this.unitAssignmentId = Optional.fromNullable(unitAssignmentId);
	}

	public void clearUnitAssignmentId() {
		this.unitAssignmentId = Optional.<Long>absent();
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Long userId = 0L;

	public Long getUserId() {
		return wrapNull(userId);
	}

	public void setUserId(Long userId) {
		this.userId = wrapNull(userId);
	}

	private Optional<Platform> arrivalPlatform = Optional.<Platform>absent();

	public Optional<Platform> getArrivalPlatform() {
		return wrapNull(arrivalPlatform);
	}

	public void setArrivalPlatform(Optional<Platform> arrivalPlatform) {
		this.arrivalPlatform = wrapNull(arrivalPlatform);
	}

	public void setArrivalPlatform(Platform arrivalPlatform) {
		this.arrivalPlatform = Optional.<Platform>fromNullable(arrivalPlatform);
	}

	public void clearArrivalPlatform() {
		this.arrivalPlatform = Optional.<Platform>absent();
	}

	private Optional<Platform> departurePlatform = Optional.<Platform>absent();

	public Optional<Platform> getDeparturePlatform() {
		return wrapNull(departurePlatform);
	}

	public void setDeparturePlatform(Optional<Platform> departurePlatform) {
		this.departurePlatform = wrapNull(departurePlatform);
	}

	public void setDeparturePlatform(Platform departurePlatform) {
		this.departurePlatform = Optional.<Platform>fromNullable(departurePlatform);
	}

	public void clearDeparturePlatform() {
		this.departurePlatform = Optional.<Platform>absent();
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

	private Optional<User> user = Optional.<User>absent();

	public Optional<User> getUser() {
		return wrapNull(user);
	}

	public void setUser(Optional<User> user) {
		this.user = wrapNull(user);
	}

	public void setUser(User user) {
		this.user = Optional.<User>fromNullable(user);
	}

	public void clearUser() {
		this.user = Optional.<User>absent();
	}
}
