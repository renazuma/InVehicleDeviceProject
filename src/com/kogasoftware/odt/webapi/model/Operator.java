package com.kogasoftware.odt.webapi.model;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public class Operator extends Model {
    public static final String JSON_NAME = "operator";
    public static final String CONTROLLER_NAME = "operators";

    public Operator() {
    }

    public static class URL {
       public static final String ROOT = "/" + CONTROLLER_NAME + ".json";
    }

    public Operator(JSONObject jsonObject) throws JSONException, ParseException {
        setAuthenticationToken(parseString(jsonObject, "authentication_token"));
        setCreatedAt(parseDate(jsonObject, "created_at"));
        setCurrentSignInAt(parseDate(jsonObject, "current_sign_in_at"));
        setCurrentSignInIp(parseString(jsonObject, "current_sign_in_ip"));
        setDeletedAt(parseDate(jsonObject, "deleted_at"));
        setEmail(parseString(jsonObject, "email"));
        setEncryptedPassword(parseString(jsonObject, "encrypted_password"));
        setFamilyName(parseString(jsonObject, "family_name"));
        setId(parseInteger(jsonObject, "id"));
        setLastName(parseString(jsonObject, "last_name"));
        setLastSignInAt(parseDate(jsonObject, "last_sign_in_at"));
        setLastSignInIp(parseString(jsonObject, "last_sign_in_ip"));
        setLogin(parseString(jsonObject, "login"));
        setRememberCreatedAt(parseDate(jsonObject, "remember_created_at"));
        setServiceProviderId(parseInteger(jsonObject, "service_provider_id"));
        setSignInCount(parseInteger(jsonObject, "sign_in_count"));
        setUpdatedAt(parseDate(jsonObject, "updated_at"));
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("authentication_token", toJSON(getAuthenticationToken().orNull()));
        jsonObject.put("created_at", toJSON(getCreatedAt()));
        jsonObject.put("current_sign_in_at", toJSON(getCurrentSignInAt().orNull()));
        jsonObject.put("current_sign_in_ip", toJSON(getCurrentSignInIp().orNull()));
        jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
        jsonObject.put("email", toJSON(getEmail().orNull()));
        jsonObject.put("encrypted_password", toJSON(getEncryptedPassword()));
        jsonObject.put("family_name", toJSON(getFamilyName()));
        jsonObject.put("id", toJSON(getId()));
        jsonObject.put("last_name", toJSON(getLastName()));
        jsonObject.put("last_sign_in_at", toJSON(getLastSignInAt().orNull()));
        jsonObject.put("last_sign_in_ip", toJSON(getLastSignInIp().orNull()));
        jsonObject.put("login", toJSON(getLogin()));
        jsonObject.put("remember_created_at", toJSON(getRememberCreatedAt().orNull()));
        jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
        jsonObject.put("sign_in_count", toJSON(getSignInCount().orNull()));
        jsonObject.put("updated_at", toJSON(getUpdatedAt()));
        return jsonObject;
    }

    private Optional<String> authenticationToken = Optional.<String>absent();

    public Optional<String> getAuthenticationToken() {
        return wrapNull(authenticationToken);
    }

    public void setAuthenticationToken(Optional<String> authenticationToken) {
        this.authenticationToken = wrapNull(authenticationToken);
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = Optional.fromNullable(authenticationToken);
    }

    private Date createdAt = new Date();

    public Date getCreatedAt() {
        return wrapNull(createdAt);
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = wrapNull(createdAt);
    }

    private Optional<Date> currentSignInAt = Optional.<Date>absent();

    public Optional<Date> getCurrentSignInAt() {
        return wrapNull(currentSignInAt);
    }

    public void setCurrentSignInAt(Optional<Date> currentSignInAt) {
        this.currentSignInAt = wrapNull(currentSignInAt);
    }

    public void setCurrentSignInAt(Date currentSignInAt) {
        this.currentSignInAt = Optional.fromNullable(currentSignInAt);
    }

    private Optional<String> currentSignInIp = Optional.<String>absent();

    public Optional<String> getCurrentSignInIp() {
        return wrapNull(currentSignInIp);
    }

    public void setCurrentSignInIp(Optional<String> currentSignInIp) {
        this.currentSignInIp = wrapNull(currentSignInIp);
    }

    public void setCurrentSignInIp(String currentSignInIp) {
        this.currentSignInIp = Optional.fromNullable(currentSignInIp);
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

    private Optional<String> email = Optional.<String>absent();

    public Optional<String> getEmail() {
        return wrapNull(email);
    }

    public void setEmail(Optional<String> email) {
        this.email = wrapNull(email);
    }

    public void setEmail(String email) {
        this.email = Optional.fromNullable(email);
    }

    private String encryptedPassword = "";

    public String getEncryptedPassword() {
        return wrapNull(encryptedPassword);
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = wrapNull(encryptedPassword);
    }

    private String familyName = "";

    public String getFamilyName() {
        return wrapNull(familyName);
    }

    public void setFamilyName(String familyName) {
        this.familyName = wrapNull(familyName);
    }

    private Integer id = 0;

    public Integer getId() {
        return wrapNull(id);
    }

    public void setId(Integer id) {
        this.id = wrapNull(id);
    }

    private String lastName = "";

    public String getLastName() {
        return wrapNull(lastName);
    }

    public void setLastName(String lastName) {
        this.lastName = wrapNull(lastName);
    }

    private Optional<Date> lastSignInAt = Optional.<Date>absent();

    public Optional<Date> getLastSignInAt() {
        return wrapNull(lastSignInAt);
    }

    public void setLastSignInAt(Optional<Date> lastSignInAt) {
        this.lastSignInAt = wrapNull(lastSignInAt);
    }

    public void setLastSignInAt(Date lastSignInAt) {
        this.lastSignInAt = Optional.fromNullable(lastSignInAt);
    }

    private Optional<String> lastSignInIp = Optional.<String>absent();

    public Optional<String> getLastSignInIp() {
        return wrapNull(lastSignInIp);
    }

    public void setLastSignInIp(Optional<String> lastSignInIp) {
        this.lastSignInIp = wrapNull(lastSignInIp);
    }

    public void setLastSignInIp(String lastSignInIp) {
        this.lastSignInIp = Optional.fromNullable(lastSignInIp);
    }

    private String login = "";

    public String getLogin() {
        return wrapNull(login);
    }

    public void setLogin(String login) {
        this.login = wrapNull(login);
    }

    private Optional<Date> rememberCreatedAt = Optional.<Date>absent();

    public Optional<Date> getRememberCreatedAt() {
        return wrapNull(rememberCreatedAt);
    }

    public void setRememberCreatedAt(Optional<Date> rememberCreatedAt) {
        this.rememberCreatedAt = wrapNull(rememberCreatedAt);
    }

    public void setRememberCreatedAt(Date rememberCreatedAt) {
        this.rememberCreatedAt = Optional.fromNullable(rememberCreatedAt);
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

    private Optional<Integer> signInCount = Optional.<Integer>absent();

    public Optional<Integer> getSignInCount() {
        return wrapNull(signInCount);
    }

    public void setSignInCount(Optional<Integer> signInCount) {
        this.signInCount = wrapNull(signInCount);
    }

    public void setSignInCount(Integer signInCount) {
        this.signInCount = Optional.fromNullable(signInCount);
    }

    private Date updatedAt = new Date();

    public Date getUpdatedAt() {
        return wrapNull(updatedAt);
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = wrapNull(updatedAt);
    }
}

