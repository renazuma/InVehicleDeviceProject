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
    private static final long serialVersionUID = 3564935524893358684L;
    public static final String JSON_NAME = "operation_schedule";
    public static final String CONTROLLER_NAME = "operation_schedules";

    public static class URL {
        public static final String ROOT = "/" + CONTROLLER_NAME;
        public static final String INDEX = "/" + CONTROLLER_NAME + "/index";
        public static final String TOP = "/" + CONTROLLER_NAME + "/top";
    }

    public OperationSchedule() {
    }

    public OperationSchedule(JSONObject jsonObject) throws JSONException, ParseException {
        setArrivalEstimate(parseDate(jsonObject, "arrival_estimate"));
        setArrivedAt(parseDate(jsonObject, "arrived_at"));
        setCreatedAt(parseDate(jsonObject, "created_at"));
        setDeletedAt(parseDate(jsonObject, "deleted_at"));
        setDepartedAt(parseDate(jsonObject, "departed_at"));
        setDepartureEstimate(parseDate(jsonObject, "departure_estimate"));
        setId(parseInteger(jsonObject, "id"));
        setPlatformId(parseInteger(jsonObject, "platform_id"));
        setServiceProviderId(parseInteger(jsonObject, "service_provider_id"));
        setUnitAssignmentId(parseInteger(jsonObject, "unit_assignment_id"));
        setUpdatedAt(parseDate(jsonObject, "updated_at"));
    }

    public static class ResponseConverter implements
            WebAPI.ResponseConverter<OperationSchedule> {
        @Override
        public OperationSchedule convert(byte[] rawResponse) throws JSONException, ParseException {
            return new OperationSchedule(new JSONObject(new String(rawResponse)));
        }
    }

    public static class ListResponseConverter implements
            WebAPI.ResponseConverter<List<OperationSchedule>> {
        @Override
        public List<OperationSchedule> convert(byte[] rawResponse) throws JSONException,
                ParseException {
            JSONArray array = new JSONArray(new String(rawResponse));
            List<OperationSchedule> models = new LinkedList<OperationSchedule>();
            for (Integer i = 0; i < array.length(); ++i) {
                if (array.isNull(i)) {
                    continue;
                }
                JSONObject object = array.getJSONObject(i);
                models.add(new OperationSchedule(object));
            }
            return models;
        }
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
        return jsonObject;
    }

    private Date arrivalEstimate = new Date();

    public Date getArrivalEstimate() {
        return wrapNull(arrivalEstimate);
    }

    public void setArrivalEstimate(Date arrivalEstimate) {
        errorIfNull(arrivalEstimate);
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
        errorIfNull(createdAt);
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
        errorIfNull(departureEstimate);
        this.departureEstimate = wrapNull(departureEstimate);
    }

    private Integer id = 0;

    public Integer getId() {
        return wrapNull(id);
    }

    public void setId(Integer id) {
        errorIfNull(id);
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
        errorIfNull(updatedAt);
        this.updatedAt = wrapNull(updatedAt);
    }
}

