package com.kogasoftware.odt.webapi.model;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public class User extends Model {
    public static final String JSON_NAME = "user";
    public static final String CONTROLLER_NAME = "users";

    public User() {
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

    public User(JSONObject jsonObject) throws JSONException, ParseException {
        setAddress(parseString(jsonObject, "address"));
        setAge(parseInteger(jsonObject, "age"));
        setBirthday(parseDate(jsonObject, "birthday"));
        setCreatedAt(parseDate(jsonObject, "created_at"));
        setCurrentSignInAt(parseDate(jsonObject, "current_sign_in_at"));
        setCurrentSignInIp(parseString(jsonObject, "current_sign_in_ip"));
        setDeletedAt(parseDate(jsonObject, "deleted_at"));
        setEmail(parseString(jsonObject, "email"));
        setEmail2(parseString(jsonObject, "email2"));
        setEncryptedPassword(parseString(jsonObject, "encrypted_password"));
        setFamilyName(parseString(jsonObject, "family_name"));
        setFamilyNameRuby(parseString(jsonObject, "family_name_ruby"));
        setFelicaId(parseString(jsonObject, "felica_id"));
        setHandicapped(parseBoolean(jsonObject, "handicapped"));
        setId(parseInteger(jsonObject, "id"));
        setLastName(parseString(jsonObject, "last_name"));
        setLastNameRuby(parseString(jsonObject, "last_name_ruby"));
        setLastSignInAt(parseDate(jsonObject, "last_sign_in_at"));
        setLastSignInIp(parseString(jsonObject, "last_sign_in_ip"));
        setLogin(parseString(jsonObject, "login"));
        setNeededCare(parseBoolean(jsonObject, "needed_care"));
        setRecommendNotification(parseBoolean(jsonObject, "recommend_notification"));
        setRecommendOk(parseBoolean(jsonObject, "recommend_ok"));
        setRememberCreatedAt(parseDate(jsonObject, "remember_created_at"));
        setReserveNotification(parseBoolean(jsonObject, "reserve_notification"));
        setServiceProviderId(parseInteger(jsonObject, "service_provider_id"));
        setSex(parseInteger(jsonObject, "sex"));
        setSignInCount(parseInteger(jsonObject, "sign_in_count"));
        setTelephoneNumber(parseString(jsonObject, "telephone_number"));
        setTelephoneNumber2(parseString(jsonObject, "telephone_number2"));
        setUpdateNotification(parseBoolean(jsonObject, "update_notification"));
        setUpdatedAt(parseDate(jsonObject, "updated_at"));
        setWheelchair(parseBoolean(jsonObject, "wheelchair"));
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("address", toJSON(getAddress()));
        jsonObject.put("age", toJSON(getAge()));
        jsonObject.put("birthday", toJSON(getBirthday()));
        jsonObject.put("created_at", toJSON(getCreatedAt()));
        jsonObject.put("current_sign_in_at", toJSON(getCurrentSignInAt().orNull()));
        jsonObject.put("current_sign_in_ip", toJSON(getCurrentSignInIp().orNull()));
        jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
        jsonObject.put("email", toJSON(getEmail().orNull()));
        jsonObject.put("email2", toJSON(getEmail2().orNull()));
        jsonObject.put("encrypted_password", toJSON(getEncryptedPassword()));
        jsonObject.put("family_name", toJSON(getFamilyName()));
        jsonObject.put("family_name_ruby", toJSON(getFamilyNameRuby()));
        jsonObject.put("felica_id", toJSON(getFelicaId().orNull()));
        jsonObject.put("handicapped", toJSON(getHandicapped().orNull()));
        jsonObject.put("id", toJSON(getId()));
        jsonObject.put("last_name", toJSON(getLastName()));
        jsonObject.put("last_name_ruby", toJSON(getLastNameRuby()));
        jsonObject.put("last_sign_in_at", toJSON(getLastSignInAt().orNull()));
        jsonObject.put("last_sign_in_ip", toJSON(getLastSignInIp().orNull()));
        jsonObject.put("login", toJSON(getLogin()));
        jsonObject.put("needed_care", toJSON(getNeededCare().orNull()));
        jsonObject.put("recommend_notification", toJSON(getRecommendNotification().orNull()));
        jsonObject.put("recommend_ok", toJSON(getRecommendOk().orNull()));
        jsonObject.put("remember_created_at", toJSON(getRememberCreatedAt().orNull()));
        jsonObject.put("reserve_notification", toJSON(getReserveNotification().orNull()));
        jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
        jsonObject.put("sex", toJSON(getSex()));
        jsonObject.put("sign_in_count", toJSON(getSignInCount().orNull()));
        jsonObject.put("telephone_number", toJSON(getTelephoneNumber()));
        jsonObject.put("telephone_number2", toJSON(getTelephoneNumber2().orNull()));
        jsonObject.put("update_notification", toJSON(getUpdateNotification().orNull()));
        jsonObject.put("updated_at", toJSON(getUpdatedAt()));
        jsonObject.put("wheelchair", toJSON(getWheelchair().orNull()));
        return jsonObject;
    }

    private String address = "";

    public String getAddress() {
        return wrapNull(address);
    }

    public void setAddress(String address) {
        this.address = wrapNull(address);
    }

    private Integer age = 0;

    public Integer getAge() {
        return wrapNull(age);
    }

    public void setAge(Integer age) {
        this.age = wrapNull(age);
    }

    private Date birthday = new Date();

    public Date getBirthday() {
        return wrapNull(birthday);
    }

    public void setBirthday(Date birthday) {
        this.birthday = wrapNull(birthday);
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

    private Optional<String> email2 = Optional.<String>absent();

    public Optional<String> getEmail2() {
        return wrapNull(email2);
    }

    public void setEmail2(Optional<String> email2) {
        this.email2 = wrapNull(email2);
    }

    public void setEmail2(String email2) {
        this.email2 = Optional.fromNullable(email2);
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

    private String familyNameRuby = "";

    public String getFamilyNameRuby() {
        return wrapNull(familyNameRuby);
    }

    public void setFamilyNameRuby(String familyNameRuby) {
        this.familyNameRuby = wrapNull(familyNameRuby);
    }

    private Optional<String> felicaId = Optional.<String>absent();

    public Optional<String> getFelicaId() {
        return wrapNull(felicaId);
    }

    public void setFelicaId(Optional<String> felicaId) {
        this.felicaId = wrapNull(felicaId);
    }

    public void setFelicaId(String felicaId) {
        this.felicaId = Optional.fromNullable(felicaId);
    }

    private Optional<Boolean> handicapped = Optional.<Boolean>absent();

    public Optional<Boolean> getHandicapped() {
        return wrapNull(handicapped);
    }

    public void setHandicapped(Optional<Boolean> handicapped) {
        this.handicapped = wrapNull(handicapped);
    }

    public void setHandicapped(Boolean handicapped) {
        this.handicapped = Optional.fromNullable(handicapped);
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

    private String lastNameRuby = "";

    public String getLastNameRuby() {
        return wrapNull(lastNameRuby);
    }

    public void setLastNameRuby(String lastNameRuby) {
        this.lastNameRuby = wrapNull(lastNameRuby);
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

    private Optional<Boolean> neededCare = Optional.<Boolean>absent();

    public Optional<Boolean> getNeededCare() {
        return wrapNull(neededCare);
    }

    public void setNeededCare(Optional<Boolean> neededCare) {
        this.neededCare = wrapNull(neededCare);
    }

    public void setNeededCare(Boolean neededCare) {
        this.neededCare = Optional.fromNullable(neededCare);
    }

    private Optional<Boolean> recommendNotification = Optional.<Boolean>absent();

    public Optional<Boolean> getRecommendNotification() {
        return wrapNull(recommendNotification);
    }

    public void setRecommendNotification(Optional<Boolean> recommendNotification) {
        this.recommendNotification = wrapNull(recommendNotification);
    }

    public void setRecommendNotification(Boolean recommendNotification) {
        this.recommendNotification = Optional.fromNullable(recommendNotification);
    }

    private Optional<Boolean> recommendOk = Optional.<Boolean>absent();

    public Optional<Boolean> getRecommendOk() {
        return wrapNull(recommendOk);
    }

    public void setRecommendOk(Optional<Boolean> recommendOk) {
        this.recommendOk = wrapNull(recommendOk);
    }

    public void setRecommendOk(Boolean recommendOk) {
        this.recommendOk = Optional.fromNullable(recommendOk);
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

    private Optional<Boolean> reserveNotification = Optional.<Boolean>absent();

    public Optional<Boolean> getReserveNotification() {
        return wrapNull(reserveNotification);
    }

    public void setReserveNotification(Optional<Boolean> reserveNotification) {
        this.reserveNotification = wrapNull(reserveNotification);
    }

    public void setReserveNotification(Boolean reserveNotification) {
        this.reserveNotification = Optional.fromNullable(reserveNotification);
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

    private Integer sex = 0;

    public Integer getSex() {
        return wrapNull(sex);
    }

    public void setSex(Integer sex) {
        this.sex = wrapNull(sex);
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

    private String telephoneNumber = "";

    public String getTelephoneNumber() {
        return wrapNull(telephoneNumber);
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = wrapNull(telephoneNumber);
    }

    private Optional<String> telephoneNumber2 = Optional.<String>absent();

    public Optional<String> getTelephoneNumber2() {
        return wrapNull(telephoneNumber2);
    }

    public void setTelephoneNumber2(Optional<String> telephoneNumber2) {
        this.telephoneNumber2 = wrapNull(telephoneNumber2);
    }

    public void setTelephoneNumber2(String telephoneNumber2) {
        this.telephoneNumber2 = Optional.fromNullable(telephoneNumber2);
    }

    private Optional<Boolean> updateNotification = Optional.<Boolean>absent();

    public Optional<Boolean> getUpdateNotification() {
        return wrapNull(updateNotification);
    }

    public void setUpdateNotification(Optional<Boolean> updateNotification) {
        this.updateNotification = wrapNull(updateNotification);
    }

    public void setUpdateNotification(Boolean updateNotification) {
        this.updateNotification = Optional.fromNullable(updateNotification);
    }

    private Date updatedAt = new Date();

    public Date getUpdatedAt() {
        return wrapNull(updatedAt);
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = wrapNull(updatedAt);
    }

    private Optional<Boolean> wheelchair = Optional.<Boolean>absent();

    public Optional<Boolean> getWheelchair() {
        return wrapNull(wheelchair);
    }

    public void setWheelchair(Optional<Boolean> wheelchair) {
        this.wheelchair = wrapNull(wheelchair);
    }

    public void setWheelchair(Boolean wheelchair) {
        this.wheelchair = Optional.fromNullable(wheelchair);
    }
}

