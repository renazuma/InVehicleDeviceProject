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

public class Reservation extends Model {
	private static final long serialVersionUID = 4024586764839910047L;
	public static final String JSON_NAME = "reservation";
	public static final String CONTROLLER_NAME = "reservations";

	public static class URL {
		public static final String ROOT = "/" + CONTROLLER_NAME;
		public static final String CANCELED = "/" + CONTROLLER_NAME + "/canceled";
		public static final String CREATE = "/" + CONTROLLER_NAME + "/create";
		public static final String DESTROY = "/" + CONTROLLER_NAME + "/destroy";
		public static final String EDIT = "/" + CONTROLLER_NAME + "/edit";
		public static final String HISTORY_FOR_CLUSTER = "/" + CONTROLLER_NAME + "/history_for_cluster";
		public static final String INDEX = "/" + CONTROLLER_NAME + "/index";
		public static final String NEW = "/" + CONTROLLER_NAME + "/new";
		public static final String SEARCH = "/" + CONTROLLER_NAME + "/search";
		public static final String SHOW = "/" + CONTROLLER_NAME + "/show";
		public static final String UPDATE = "/" + CONTROLLER_NAME + "/update";
	}

	public Reservation() {
	}

	public Reservation(JSONObject jsonObject) throws JSONException, ParseException {
		setArrivalLock(parseOptionalBoolean(jsonObject, "arrival_lock"));
		setArrivalPlatformId(parseOptionalLong(jsonObject, "arrival_platform_id"));
		setArrivalScheduleId(parseOptionalLong(jsonObject, "arrival_schedule_id"));
		setArrivalTime(parseDate(jsonObject, "arrival_time"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setDemandId(parseLong(jsonObject, "demand_id"));
		setDepartureLock(parseOptionalBoolean(jsonObject, "departure_lock"));
		setDeparturePlatformId(parseOptionalLong(jsonObject, "departure_platform_id"));
		setDepartureScheduleId(parseOptionalLong(jsonObject, "departure_schedule_id"));
		setDepartureTime(parseDate(jsonObject, "departure_time"));
		setHead(parseLong(jsonObject, "head"));
		setId(parseLong(jsonObject, "id"));
		setMemo(parseOptionalString(jsonObject, "memo"));
		setOperatorId(parseOptionalLong(jsonObject, "operator_id"));
		setPayment(parseBoolean(jsonObject, "payment"));
		setServiceProviderId(parseOptionalLong(jsonObject, "service_provider_id"));
		setStatus(parseLong(jsonObject, "status"));
		setUnitAssignmentId(parseLong(jsonObject, "unit_assignment_id"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setUserId(parseLong(jsonObject, "user_id"));
		setArrivalPlatform(new Platform(jsonObject.getJSONObject("arrival_platform")));
		if (getArrivalPlatform().isPresent()) {
			setArrivalPlatformId(getArrivalPlatform().get().getId());
		}
		setArrivalSchedule(new OperationSchedule(jsonObject.getJSONObject("arrival_schedule")));
		if (getArrivalSchedule().isPresent()) {
			setArrivalScheduleId(getArrivalSchedule().get().getId());
		}
		setDemand(new Demand(jsonObject.getJSONObject("demand")));
		if (getDemand().isPresent()) {
			setDemandId(getDemand().get().getId());
		}
		setDeparturePlatform(new Platform(jsonObject.getJSONObject("departure_platform")));
		if (getDeparturePlatform().isPresent()) {
			setDeparturePlatformId(getDeparturePlatform().get().getId());
		}
		setDepartureSchedule(new OperationSchedule(jsonObject.getJSONObject("departure_schedule")));
		if (getDepartureSchedule().isPresent()) {
			setDepartureScheduleId(getDepartureSchedule().get().getId());
		}
		setOperator(new Operator(jsonObject.getJSONObject("operator")));
		if (getOperator().isPresent()) {
			setOperatorId(getOperator().get().getId());
		}
		setUser(new User(jsonObject.getJSONObject("user")));
		if (getUser().isPresent()) {
			setUserId(getUser().get().getId());
		}
	}

	public static Optional<Reservation> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<Reservation>absent();
		}
		return Optional.<Reservation>of(new Reservation(jsonObject.getJSONObject(key)));
	}

	public static List<Reservation> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<Reservation>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		List<Reservation> models = new LinkedList<Reservation>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new Reservation(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	public static class ResponseConverter implements
			WebAPI.ResponseConverter<Reservation> {
		@Override
		public Reservation convert(byte[] rawResponse) throws JSONException, ParseException {
			return new Reservation(new JSONObject(new String(rawResponse)));
		}
	}

	public static class ListResponseConverter implements
			WebAPI.ResponseConverter<List<Reservation>> {
		@Override
		public List<Reservation> convert(byte[] rawResponse) throws JSONException,
				ParseException {
			JSONArray array = new JSONArray(new String(rawResponse));
			List<Reservation> models = new LinkedList<Reservation>();
			for (Integer i = 0; i < array.length(); ++i) {
				if (array.isNull(i)) {
					continue;
				}
				JSONObject object = array.getJSONObject(i);
				models.add(new Reservation(object));
			}
			return models;
		}
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("arrival_lock", toJSON(getArrivalLock().orNull()));
		jsonObject.put("arrival_platform_id", toJSON(getArrivalPlatformId().orNull()));
		jsonObject.put("arrival_schedule_id", toJSON(getArrivalScheduleId().orNull()));
		jsonObject.put("arrival_time", toJSON(getArrivalTime()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
		jsonObject.put("demand_id", toJSON(getDemandId()));
		jsonObject.put("departure_lock", toJSON(getDepartureLock().orNull()));
		jsonObject.put("departure_platform_id", toJSON(getDeparturePlatformId().orNull()));
		jsonObject.put("departure_schedule_id", toJSON(getDepartureScheduleId().orNull()));
		jsonObject.put("departure_time", toJSON(getDepartureTime()));
		jsonObject.put("head", toJSON(getHead()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("memo", toJSON(getMemo().orNull()));
		jsonObject.put("operator_id", toJSON(getOperatorId().orNull()));
		jsonObject.put("payment", toJSON(getPayment()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("status", toJSON(getStatus()));
		jsonObject.put("unit_assignment_id", toJSON(getUnitAssignmentId()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("user_id", toJSON(getUserId()));
		jsonObject.put("arrival_platform", toJSON(getArrivalPlatform()));
		if (getArrivalPlatform().isPresent()) {
			jsonObject.put("arrival_platform_id", toJSON(getArrivalPlatform().get().getId()));
		}
		jsonObject.put("arrival_schedule", toJSON(getArrivalSchedule()));
		if (getArrivalSchedule().isPresent()) {
			jsonObject.put("arrival_schedule_id", toJSON(getArrivalSchedule().get().getId()));
		}
		jsonObject.put("demand", toJSON(getDemand()));
		if (getDemand().isPresent()) {
			jsonObject.put("demand_id", toJSON(getDemand().get().getId()));
		}
		jsonObject.put("departure_platform", toJSON(getDeparturePlatform()));
		if (getDeparturePlatform().isPresent()) {
			jsonObject.put("departure_platform_id", toJSON(getDeparturePlatform().get().getId()));
		}
		jsonObject.put("departure_schedule", toJSON(getDepartureSchedule()));
		if (getDepartureSchedule().isPresent()) {
			jsonObject.put("departure_schedule_id", toJSON(getDepartureSchedule().get().getId()));
		}
		jsonObject.put("operator", toJSON(getOperator()));
		if (getOperator().isPresent()) {
			jsonObject.put("operator_id", toJSON(getOperator().get().getId()));
		}
		jsonObject.put("user", toJSON(getUser()));
		if (getUser().isPresent()) {
			jsonObject.put("user_id", toJSON(getUser().get().getId()));
		}
		return jsonObject;
	}

	private Optional<Boolean> arrivalLock = Optional.<Boolean>absent();

	public Optional<Boolean> getArrivalLock() {
		return wrapNull(arrivalLock);
	}

	public void setArrivalLock(Optional<Boolean> arrivalLock) {
		this.arrivalLock = wrapNull(arrivalLock);
	}

	public void setArrivalLock(Boolean arrivalLock) {
		this.arrivalLock = Optional.fromNullable(arrivalLock);
	}

	public void clearArrivalLock() {
		this.arrivalLock = Optional.<Boolean>absent();
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

	private Optional<Long> arrivalScheduleId = Optional.<Long>absent();

	public Optional<Long> getArrivalScheduleId() {
		return wrapNull(arrivalScheduleId);
	}

	public void setArrivalScheduleId(Optional<Long> arrivalScheduleId) {
		this.arrivalScheduleId = wrapNull(arrivalScheduleId);
	}

	public void setArrivalScheduleId(Long arrivalScheduleId) {
		this.arrivalScheduleId = Optional.fromNullable(arrivalScheduleId);
	}

	public void clearArrivalScheduleId() {
		this.arrivalScheduleId = Optional.<Long>absent();
	}

	private Date arrivalTime = new Date();

	public Date getArrivalTime() {
		return wrapNull(arrivalTime);
	}

	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = wrapNull(arrivalTime);
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

	private Long demandId = 0L;

	public Long getDemandId() {
		return wrapNull(demandId);
	}

	public void setDemandId(Long demandId) {
		this.demandId = wrapNull(demandId);
	}

	private Optional<Boolean> departureLock = Optional.<Boolean>absent();

	public Optional<Boolean> getDepartureLock() {
		return wrapNull(departureLock);
	}

	public void setDepartureLock(Optional<Boolean> departureLock) {
		this.departureLock = wrapNull(departureLock);
	}

	public void setDepartureLock(Boolean departureLock) {
		this.departureLock = Optional.fromNullable(departureLock);
	}

	public void clearDepartureLock() {
		this.departureLock = Optional.<Boolean>absent();
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

	private Optional<Long> departureScheduleId = Optional.<Long>absent();

	public Optional<Long> getDepartureScheduleId() {
		return wrapNull(departureScheduleId);
	}

	public void setDepartureScheduleId(Optional<Long> departureScheduleId) {
		this.departureScheduleId = wrapNull(departureScheduleId);
	}

	public void setDepartureScheduleId(Long departureScheduleId) {
		this.departureScheduleId = Optional.fromNullable(departureScheduleId);
	}

	public void clearDepartureScheduleId() {
		this.departureScheduleId = Optional.<Long>absent();
	}

	private Date departureTime = new Date();

	public Date getDepartureTime() {
		return wrapNull(departureTime);
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = wrapNull(departureTime);
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

	private Optional<Long> operatorId = Optional.<Long>absent();

	public Optional<Long> getOperatorId() {
		return wrapNull(operatorId);
	}

	public void setOperatorId(Optional<Long> operatorId) {
		this.operatorId = wrapNull(operatorId);
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = Optional.fromNullable(operatorId);
	}

	public void clearOperatorId() {
		this.operatorId = Optional.<Long>absent();
	}

	private Boolean payment = false;

	public Boolean getPayment() {
		return wrapNull(payment);
	}

	public void setPayment(Boolean payment) {
		this.payment = wrapNull(payment);
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

	private Long status = 0L;

	public Long getStatus() {
		return wrapNull(status);
	}

	public void setStatus(Long status) {
		this.status = wrapNull(status);
	}

	private Long unitAssignmentId = 0L;

	public Long getUnitAssignmentId() {
		return wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Long unitAssignmentId) {
		this.unitAssignmentId = wrapNull(unitAssignmentId);
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

	private Optional<OperationSchedule> arrivalSchedule = Optional.<OperationSchedule>absent();

	public Optional<OperationSchedule> getArrivalSchedule() {
		return wrapNull(arrivalSchedule);
	}

	public void setArrivalSchedule(Optional<OperationSchedule> arrivalSchedule) {
		this.arrivalSchedule = wrapNull(arrivalSchedule);
	}

	public void setArrivalSchedule(OperationSchedule arrivalSchedule) {
		this.arrivalSchedule = Optional.<OperationSchedule>fromNullable(arrivalSchedule);
	}

	public void clearArrivalSchedule() {
		this.arrivalSchedule = Optional.<OperationSchedule>absent();
	}

	private Optional<Demand> demand = Optional.<Demand>absent();

	public Optional<Demand> getDemand() {
		return wrapNull(demand);
	}

	public void setDemand(Optional<Demand> demand) {
		this.demand = wrapNull(demand);
	}

	public void setDemand(Demand demand) {
		this.demand = Optional.<Demand>fromNullable(demand);
	}

	public void clearDemand() {
		this.demand = Optional.<Demand>absent();
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

	private Optional<OperationSchedule> departureSchedule = Optional.<OperationSchedule>absent();

	public Optional<OperationSchedule> getDepartureSchedule() {
		return wrapNull(departureSchedule);
	}

	public void setDepartureSchedule(Optional<OperationSchedule> departureSchedule) {
		this.departureSchedule = wrapNull(departureSchedule);
	}

	public void setDepartureSchedule(OperationSchedule departureSchedule) {
		this.departureSchedule = Optional.<OperationSchedule>fromNullable(departureSchedule);
	}

	public void clearDepartureSchedule() {
		this.departureSchedule = Optional.<OperationSchedule>absent();
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
