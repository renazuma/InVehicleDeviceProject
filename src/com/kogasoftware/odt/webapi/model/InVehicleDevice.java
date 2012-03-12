package com.kogasoftware.odt.webapi.model;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public class InVehicleDevice extends Model {
    public static final String JSON_NAME = "in_vehicle_device";
    public static final String CONTROLLER_NAME = "in_vehicle_devices";

    public InVehicleDevice() {
    }

    public static class URL {
       public static final String ROOT = "/" + CONTROLLER_NAME + ".json";
       public static final String CREATE = "/" + CONTROLLER_NAME + "/create.json";
       public static final String DESTROY = "/" + CONTROLLER_NAME + "/destroy.json";
       public static final String EDIT = "/" + CONTROLLER_NAME + "/edit.json";
       public static final String INDEX = "/" + CONTROLLER_NAME + "/index.json";
       public static final String NEW = "/" + CONTROLLER_NAME + "/new.json";
       public static final String SHOW = "/" + CONTROLLER_NAME + "/show.json";
       public static final String UPDATE = "/" + CONTROLLER_NAME + "/update.json";
    }

    public InVehicleDevice(JSONObject jsonObject) throws JSONException, ParseException {
        setCreatedAt(parseDate(jsonObject, "created_at"));
        setDeletedAt(parseDate(jsonObject, "deleted_at"));
        setId(parseInteger(jsonObject, "id"));
        setModelName(parseString(jsonObject, "model_name"));
        setServiceProviderId(parseInteger(jsonObject, "service_provider_id"));
        setTypeNumber(parseString(jsonObject, "type_number"));
        setUpdatedAt(parseDate(jsonObject, "updated_at"));
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("created_at", toJSON(getCreatedAt()));
        jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
        jsonObject.put("id", toJSON(getId()));
        jsonObject.put("model_name", toJSON(getModelName()));
        jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
        jsonObject.put("type_number", toJSON(getTypeNumber()));
        jsonObject.put("updated_at", toJSON(getUpdatedAt()));
        return jsonObject;
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

    private Integer id = 0;

    public Integer getId() {
        return wrapNull(id);
    }

    public void setId(Integer id) {
        this.id = wrapNull(id);
    }

    private String modelName = "";

    public String getModelName() {
        return wrapNull(modelName);
    }

    public void setModelName(String modelName) {
        this.modelName = wrapNull(modelName);
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

    private String typeNumber = "";

    public String getTypeNumber() {
        return wrapNull(typeNumber);
    }

    public void setTypeNumber(String typeNumber) {
        this.typeNumber = wrapNull(typeNumber);
    }

    private Date updatedAt = new Date();

    public Date getUpdatedAt() {
        return wrapNull(updatedAt);
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = wrapNull(updatedAt);
    }
}

