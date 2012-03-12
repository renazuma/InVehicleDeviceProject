package com.kogasoftware.odt.webapi.model;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public class Reservation extends Model {
    public static final String JSON_NAME = "reservation";
    public static final String CONTROLLER_NAME = "reservations";

    public Reservation() {
    }

    public static class URL {
       public static final String ROOT = "/" + CONTROLLER_NAME + ".json";
       public static final String CANCELED = "/" + CONTROLLER_NAME + "/canceled.json";
       public static final String CREATE = "/" + CONTROLLER_NAME + "/create.json";
       public static final String DESTROY = "/" + CONTROLLER_NAME + "/destroy.json";
       public static final String EDIT = "/" + CONTROLLER_NAME + "/edit.json";
       public static final String INDEX = "/" + CONTROLLER_NAME + "/index.json";
       public static final String NEW = "/" + CONTROLLER_NAME + "/new.json";
       public static final String SEARCH = "/" + CONTROLLER_NAME + "/search.json";
       public static final String SHOW = "/" + CONTROLLER_NAME + "/show.json";
       public static final String UPDATE = "/" + CONTROLLER_NAME + "/update.json";
    }

    public Reservation(JSONObject jsonObject) throws JSONException, ParseException {
        setArrivalLock(parseBoolean(jsonObject, "arrival_lock"));
        setArrivalPlatformId(parseInteger(jsonObject, "arrival_platform_id"));
        setArrivalScheduleId(parseInteger(jsonObject, "arrival_schedule_id"));
        setArrivalTime(parseDate(jsonObject, "arrival_time"));
        setCreatedAt(parseDate(jsonObject, "created_at"));
        setDeletedAt(parseDate(jsonObject, "deleted_at"));
        setDemandId(parseInteger(jsonObject, "demand_id"));
        setDepartureLock(parseBoolean(jsonObject, "departure_lock"));
        setDeparturePlatformId(parseInteger(jsonObject, "departure_platform_id"));
        setDepartureScheduleId(parseInteger(jsonObject, "departure_schedule_id"));
        setDepartureTime(parseDate(jsonObject, "departure_time"));
        setHead(parseInteger(jsonObject, "head"));
        setId(parseInteger(jsonObject, "id"));
        setMemo(parseString(jsonObject, "memo"));
        setOperatorId(parseInteger(jsonObject, "operator_id"));
        setPayment(parseBoolean(jsonObject, "payment"));
        setServiceProviderId(parseInteger(jsonObject, "service_provider_id"));
        setStatus(parseInteger(jsonObject, "status"));
        setUnitAssignmentId(parseInteger(jsonObject, "unit_assignment_id"));
        setUpdatedAt(parseDate(jsonObject, "updated_at"));
        setUserId(parseInteger(jsonObject, "user_id"));
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

    private Optional<Integer> arrivalPlatformId = Optional.<Integer>absent();

    public Optional<Integer> getArrivalPlatformId() {
        return wrapNull(arrivalPlatformId);
    }

    public void setArrivalPlatformId(Optional<Integer> arrivalPlatformId) {
        this.arrivalPlatformId = wrapNull(arrivalPlatformId);
    }

    public void setArrivalPlatformId(Integer arrivalPlatformId) {
        this.arrivalPlatformId = Optional.fromNullable(arrivalPlatformId);
    }

    private Optional<Integer> arrivalScheduleId = Optional.<Integer>absent();

    public Optional<Integer> getArrivalScheduleId() {
        return wrapNull(arrivalScheduleId);
    }

    public void setArrivalScheduleId(Optional<Integer> arrivalScheduleId) {
        this.arrivalScheduleId = wrapNull(arrivalScheduleId);
    }

    public void setArrivalScheduleId(Integer arrivalScheduleId) {
        this.arrivalScheduleId = Optional.fromNullable(arrivalScheduleId);
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

    private Integer demandId = 0;

    public Integer getDemandId() {
        return wrapNull(demandId);
    }

    public void setDemandId(Integer demandId) {
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

    private Optional<Integer> departurePlatformId = Optional.<Integer>absent();

    public Optional<Integer> getDeparturePlatformId() {
        return wrapNull(departurePlatformId);
    }

    public void setDeparturePlatformId(Optional<Integer> departurePlatformId) {
        this.departurePlatformId = wrapNull(departurePlatformId);
    }

    public void setDeparturePlatformId(Integer departurePlatformId) {
        this.departurePlatformId = Optional.fromNullable(departurePlatformId);
    }

    private Optional<Integer> departureScheduleId = Optional.<Integer>absent();

    public Optional<Integer> getDepartureScheduleId() {
        return wrapNull(departureScheduleId);
    }

    public void setDepartureScheduleId(Optional<Integer> departureScheduleId) {
        this.departureScheduleId = wrapNull(departureScheduleId);
    }

    public void setDepartureScheduleId(Integer departureScheduleId) {
        this.departureScheduleId = Optional.fromNullable(departureScheduleId);
    }

    private Date departureTime = new Date();

    public Date getDepartureTime() {
        return wrapNull(departureTime);
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = wrapNull(departureTime);
    }

    private Integer head = 0;

    public Integer getHead() {
        return wrapNull(head);
    }

    public void setHead(Integer head) {
        this.head = wrapNull(head);
    }

    private Integer id = 0;

    public Integer getId() {
        return wrapNull(id);
    }

    public void setId(Integer id) {
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

    private Optional<Integer> operatorId = Optional.<Integer>absent();

    public Optional<Integer> getOperatorId() {
        return wrapNull(operatorId);
    }

    public void setOperatorId(Optional<Integer> operatorId) {
        this.operatorId = wrapNull(operatorId);
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = Optional.fromNullable(operatorId);
    }

    private Boolean payment = false;

    public Boolean getPayment() {
        return wrapNull(payment);
    }

    public void setPayment(Boolean payment) {
        this.payment = wrapNull(payment);
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

    private Integer status = 0;

    public Integer getStatus() {
        return wrapNull(status);
    }

    public void setStatus(Integer status) {
        this.status = wrapNull(status);
    }

    private Integer unitAssignmentId = 0;

    public Integer getUnitAssignmentId() {
        return wrapNull(unitAssignmentId);
    }

    public void setUnitAssignmentId(Integer unitAssignmentId) {
        this.unitAssignmentId = wrapNull(unitAssignmentId);
    }

    private Date updatedAt = new Date();

    public Date getUpdatedAt() {
        return wrapNull(updatedAt);
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = wrapNull(updatedAt);
    }

    private Integer userId = 0;

    public Integer getUserId() {
        return wrapNull(userId);
    }

    public void setUserId(Integer userId) {
        this.userId = wrapNull(userId);
    }
}

