package com.kogasoftware.odt.webapi.model;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public class VehicleNotification extends Model {
    public static final String JSON_NAME = "vehicle_notification";
    public static final String CONTROLLER_NAME = "vehicle_notifications";

    public VehicleNotification() {
    }

    public static class URL {
       public static final String ROOT = "/" + CONTROLLER_NAME + ".json";
       public static final String CREATE = "/" + CONTROLLER_NAME + "/create.json";
       public static final String GET_NEW_NOTIFICATIONS = "/" + CONTROLLER_NAME + "/get_new_notifications.json";
       public static final String SEARCH = "/" + CONTROLLER_NAME + "/search.json";
    }

    public VehicleNotification(JSONObject jsonObject) throws JSONException, ParseException {
        setBody(parseString(jsonObject, "body"));
        setCreatedAt(parseDate(jsonObject, "created_at"));
        setId(parseInteger(jsonObject, "id"));
        setInVehicleDeviceId(parseInteger(jsonObject, "in_vehicle_device_id"));
        setOperatorId(parseInteger(jsonObject, "operator_id"));
        setReadAt(parseDate(jsonObject, "read_at"));
        setUpdatedAt(parseDate(jsonObject, "updated_at"));
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("body", toJSON(getBody().orNull()));
        jsonObject.put("created_at", toJSON(getCreatedAt()));
        jsonObject.put("id", toJSON(getId()));
        jsonObject.put("in_vehicle_device_id", toJSON(getInVehicleDeviceId()));
        jsonObject.put("operator_id", toJSON(getOperatorId()));
        jsonObject.put("read_at", toJSON(getReadAt().orNull()));
        jsonObject.put("updated_at", toJSON(getUpdatedAt()));
        return jsonObject;
    }

    private Optional<String> body = Optional.<String>absent();

    public Optional<String> getBody() {
        return wrapNull(body);
    }

    public void setBody(Optional<String> body) {
        this.body = wrapNull(body);
    }

    public void setBody(String body) {
        this.body = Optional.fromNullable(body);
    }

    private Date createdAt = new Date();

    public Date getCreatedAt() {
        return wrapNull(createdAt);
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = wrapNull(createdAt);
    }

    private Integer id = 0;

    public Integer getId() {
        return wrapNull(id);
    }

    public void setId(Integer id) {
        this.id = wrapNull(id);
    }

    private Integer inVehicleDeviceId = 0;

    public Integer getInVehicleDeviceId() {
        return wrapNull(inVehicleDeviceId);
    }

    public void setInVehicleDeviceId(Integer inVehicleDeviceId) {
        this.inVehicleDeviceId = wrapNull(inVehicleDeviceId);
    }

    private Integer operatorId = 0;

    public Integer getOperatorId() {
        return wrapNull(operatorId);
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = wrapNull(operatorId);
    }

    private Optional<Date> readAt = Optional.<Date>absent();

    public Optional<Date> getReadAt() {
        return wrapNull(readAt);
    }

    public void setReadAt(Optional<Date> readAt) {
        this.readAt = wrapNull(readAt);
    }

    public void setReadAt(Date readAt) {
        this.readAt = Optional.fromNullable(readAt);
    }

    private Date updatedAt = new Date();

    public Date getUpdatedAt() {
        return wrapNull(updatedAt);
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = wrapNull(updatedAt);
    }
}

