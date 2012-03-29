package com.kogasoftware.odt.webapi.model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public class User extends Model {
	private static final long serialVersionUID = 7852090135320302034L;

	public User() {
	}

	public User(JSONObject jsonObject) throws JSONException, ParseException {
		setAddress(parseString(jsonObject, "address"));
		setAge(parseInteger(jsonObject, "age"));
		setBirthday(parseDate(jsonObject, "birthday"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setCurrentSignInAt(parseOptionalDate(jsonObject, "current_sign_in_at"));
		setCurrentSignInIp(parseOptionalString(jsonObject, "current_sign_in_ip"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setEmail(parseOptionalString(jsonObject, "email"));
		setEmail2(parseOptionalString(jsonObject, "email2"));
		setEncryptedPassword(parseString(jsonObject, "encrypted_password"));
		setFelicaId(parseOptionalString(jsonObject, "felica_id"));
		setFirstName(parseString(jsonObject, "first_name"));
		setFirstNameRuby(parseString(jsonObject, "first_name_ruby"));
		setHandicapped(parseOptionalBoolean(jsonObject, "handicapped"));
		setId(parseInteger(jsonObject, "id"));
		setLastName(parseString(jsonObject, "last_name"));
		setLastNameRuby(parseString(jsonObject, "last_name_ruby"));
		setLastSignInAt(parseOptionalDate(jsonObject, "last_sign_in_at"));
		setLastSignInIp(parseOptionalString(jsonObject, "last_sign_in_ip"));
		setLogin(parseString(jsonObject, "login"));
		setNeededCare(parseOptionalBoolean(jsonObject, "needed_care"));
		setRecommendNotification(parseOptionalBoolean(jsonObject, "recommend_notification"));
		setRecommendOk(parseOptionalBoolean(jsonObject, "recommend_ok"));
		setRememberCreatedAt(parseOptionalDate(jsonObject, "remember_created_at"));
		setReserveNotification(parseOptionalBoolean(jsonObject, "reserve_notification"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setSex(parseInteger(jsonObject, "sex"));
		setSignInCount(parseOptionalInteger(jsonObject, "sign_in_count"));
		setTelephoneNumber(parseString(jsonObject, "telephone_number"));
		setTelephoneNumber2(parseOptionalString(jsonObject, "telephone_number2"));
		setUpdateNotification(parseOptionalBoolean(jsonObject, "update_notification"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setWheelchair(parseOptionalBoolean(jsonObject, "wheelchair"));
		setDemands(Demand.parseList(jsonObject, "demands"));
		setReservationCandidates(ReservationCandidate.parseList(jsonObject, "reservation_candidates"));
		setReservations(Reservation.parseList(jsonObject, "reservations"));
	}

	public static Optional<User> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<User>absent();
		}
		return Optional.<User>of(new User(jsonObject.getJSONObject(key)));
	}

	public static LinkedList<User> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<User>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		LinkedList<User> models = new LinkedList<User>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new User(jsonArray.getJSONObject(i)));
		}
		return models;
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
		jsonObject.put("felica_id", toJSON(getFelicaId().orNull()));
		jsonObject.put("first_name", toJSON(getFirstName()));
		jsonObject.put("first_name_ruby", toJSON(getFirstNameRuby()));
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
		jsonObject.put("demands", toJSON(getDemands()));
		jsonObject.put("reservation_candidates", toJSON(getReservationCandidates()));
		jsonObject.put("reservations", toJSON(getReservations()));
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

	public void clearCurrentSignInAt() {
		this.currentSignInAt = Optional.<Date>absent();
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

	public void clearCurrentSignInIp() {
		this.currentSignInIp = Optional.<String>absent();
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

	public void clearEmail() {
		this.email = Optional.<String>absent();
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

	public void clearEmail2() {
		this.email2 = Optional.<String>absent();
	}

	private String encryptedPassword = "";

	public String getEncryptedPassword() {
		return wrapNull(encryptedPassword);
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = wrapNull(encryptedPassword);
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

	public void clearFelicaId() {
		this.felicaId = Optional.<String>absent();
	}

	private String firstName = "";

	public String getFirstName() {
		return wrapNull(firstName);
	}

	public void setFirstName(String firstName) {
		this.firstName = wrapNull(firstName);
	}

	private String firstNameRuby = "";

	public String getFirstNameRuby() {
		return wrapNull(firstNameRuby);
	}

	public void setFirstNameRuby(String firstNameRuby) {
		this.firstNameRuby = wrapNull(firstNameRuby);
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

	public void clearHandicapped() {
		this.handicapped = Optional.<Boolean>absent();
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

	public void clearLastSignInAt() {
		this.lastSignInAt = Optional.<Date>absent();
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

	public void clearLastSignInIp() {
		this.lastSignInIp = Optional.<String>absent();
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

	public void clearNeededCare() {
		this.neededCare = Optional.<Boolean>absent();
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

	public void clearRecommendNotification() {
		this.recommendNotification = Optional.<Boolean>absent();
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

	public void clearRecommendOk() {
		this.recommendOk = Optional.<Boolean>absent();
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

	public void clearRememberCreatedAt() {
		this.rememberCreatedAt = Optional.<Date>absent();
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

	public void clearReserveNotification() {
		this.reserveNotification = Optional.<Boolean>absent();
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

	public void clearSignInCount() {
		this.signInCount = Optional.<Integer>absent();
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

	public void clearTelephoneNumber2() {
		this.telephoneNumber2 = Optional.<String>absent();
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

	public void clearUpdateNotification() {
		this.updateNotification = Optional.<Boolean>absent();
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

	public void clearWheelchair() {
		this.wheelchair = Optional.<Boolean>absent();
	}

	private LinkedList<Demand> demands = new LinkedList<Demand>();

	public LinkedList<Demand> getDemands() {
		return new LinkedList<Demand>(wrapNull(demands));
	}

	public void setDemands(LinkedList<Demand> demands) {
		this.demands = new LinkedList<Demand>(wrapNull(demands));
	}

	public void clearDemands() {
		this.demands = new LinkedList<Demand>();
	}

	private LinkedList<ReservationCandidate> reservationCandidates = new LinkedList<ReservationCandidate>();

	public LinkedList<ReservationCandidate> getReservationCandidates() {
		return new LinkedList<ReservationCandidate>(wrapNull(reservationCandidates));
	}

	public void setReservationCandidates(LinkedList<ReservationCandidate> reservationCandidates) {
		this.reservationCandidates = new LinkedList<ReservationCandidate>(wrapNull(reservationCandidates));
	}

	public void clearReservationCandidates() {
		this.reservationCandidates = new LinkedList<ReservationCandidate>();
	}

	private LinkedList<Reservation> reservations = new LinkedList<Reservation>();

	public LinkedList<Reservation> getReservations() {
		return new LinkedList<Reservation>(wrapNull(reservations));
	}

	public void setReservations(LinkedList<Reservation> reservations) {
		this.reservations = new LinkedList<Reservation>(wrapNull(reservations));
	}

	public void clearReservations() {
		this.reservations = new LinkedList<Reservation>();
	}
}
