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

public class User extends Model {
	private static final long serialVersionUID = 3082163235276016117L;

	public User() {
	}

	public User(JSONObject jsonObject) throws JSONException, ParseException {
		setAddress(parseString(jsonObject, "address"));
		setAge(parseInteger(jsonObject, "age"));
		setBirthday(parseDate(jsonObject, "birthday"));
		setEmail(parseOptionalString(jsonObject, "email"));
		setEmail2(parseOptionalString(jsonObject, "email2"));
		setFelicaId(parseOptionalString(jsonObject, "felica_id"));
		setFirstName(parseString(jsonObject, "first_name"));
		setFirstNameRuby(parseString(jsonObject, "first_name_ruby"));
		setHandicapped(parseOptionalBoolean(jsonObject, "handicapped"));
		setId(parseInteger(jsonObject, "id"));
		setLastName(parseString(jsonObject, "last_name"));
		setLastNameRuby(parseString(jsonObject, "last_name_ruby"));
		setLogin(parseString(jsonObject, "login"));
		setNeededCare(parseOptionalBoolean(jsonObject, "needed_care"));
		setRecommendNotification(parseOptionalBoolean(jsonObject, "recommend_notification"));
		setRecommendOk(parseOptionalBoolean(jsonObject, "recommend_ok"));
		setReserveNotification(parseOptionalBoolean(jsonObject, "reserve_notification"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setSex(parseInteger(jsonObject, "sex"));
		setTelephoneNumber(parseString(jsonObject, "telephone_number"));
		setTelephoneNumber2(parseOptionalString(jsonObject, "telephone_number2"));
		setUpdateNotification(parseOptionalBoolean(jsonObject, "update_notification"));
		setWheelchair(parseOptionalBoolean(jsonObject, "wheelchair"));
		setAuditComment(parseOptionalString(jsonObject, "audit_comment"));
		setFullname(parseOptionalString(jsonObject, "fullname"));
		setFullnameRuby(parseOptionalString(jsonObject, "fullname_ruby"));
		setPassword(parseOptionalString(jsonObject, "password"));
		setPasswordConfirmation(parseOptionalString(jsonObject, "password_confirmation"));
		setRememberMe(parseOptionalString(jsonObject, "remember_me"));
		setDemands(Demand.parseList(jsonObject, "demands"));
		setReservationCandidates(ReservationCandidate.parseList(jsonObject, "reservation_candidates"));
		setReservations(Reservation.parseList(jsonObject, "reservations"));
		setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
		if (getServiceProvider().isPresent()) {
			setServiceProviderId(getServiceProvider().get().getId());
		}
	}

	public static Optional<User> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<User>absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<User> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.<User>of(new User(jsonObject));
	}

	public static LinkedList<User> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<User>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<User> parseList(JSONArray jsonArray) throws JSONException, ParseException {
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
		jsonObject.put("email", toJSON(getEmail().orNull()));
		jsonObject.put("email2", toJSON(getEmail2().orNull()));
		jsonObject.put("felica_id", toJSON(getFelicaId().orNull()));
		jsonObject.put("first_name", toJSON(getFirstName()));
		jsonObject.put("first_name_ruby", toJSON(getFirstNameRuby()));
		jsonObject.put("handicapped", toJSON(getHandicapped().orNull()));
		jsonObject.put("last_name", toJSON(getLastName()));
		jsonObject.put("last_name_ruby", toJSON(getLastNameRuby()));
		jsonObject.put("login", toJSON(getLogin()));
		jsonObject.put("needed_care", toJSON(getNeededCare().orNull()));
		jsonObject.put("recommend_notification", toJSON(getRecommendNotification().orNull()));
		jsonObject.put("recommend_ok", toJSON(getRecommendOk().orNull()));
		jsonObject.put("reserve_notification", toJSON(getReserveNotification().orNull()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("sex", toJSON(getSex()));
		jsonObject.put("telephone_number", toJSON(getTelephoneNumber()));
		jsonObject.put("telephone_number2", toJSON(getTelephoneNumber2().orNull()));
		jsonObject.put("update_notification", toJSON(getUpdateNotification().orNull()));
		jsonObject.put("wheelchair", toJSON(getWheelchair().orNull()));
		jsonObject.put("audit_comment", toJSON(getAuditComment().orNull()));
		jsonObject.put("fullname", toJSON(getFullname().orNull()));
		jsonObject.put("fullname_ruby", toJSON(getFullnameRuby().orNull()));
		jsonObject.put("password", toJSON(getPassword().orNull()));
		jsonObject.put("password_confirmation", toJSON(getPasswordConfirmation().orNull()));
		jsonObject.put("remember_me", toJSON(getRememberMe().orNull()));
		if (getDemands().size() > 0) {
	   		jsonObject.put("demands", toJSON(getDemands()));
		}

		if (getReservationCandidates().size() > 0) {
	   		jsonObject.put("reservation_candidates", toJSON(getReservationCandidates()));
		}

		if (getReservations().size() > 0) {
	   		jsonObject.put("reservations", toJSON(getReservations()));
		}


		if (getServiceProvider().isPresent()) {
			jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
		}
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

	private Optional<String> auditComment = Optional.<String>absent();

	public Optional<String> getAuditComment() {
		return wrapNull(auditComment);
	}

	public void setAuditComment(Optional<String> auditComment) {
		this.auditComment = wrapNull(auditComment);
	}

	public void setAuditComment(String auditComment) {
		this.auditComment = Optional.fromNullable(auditComment);
	}

	public void clearAuditComment() {
		this.auditComment = Optional.<String>absent();
	}

	private Optional<String> fullname = Optional.<String>absent();

	public Optional<String> getFullname() {
		return wrapNull(fullname);
	}

	public void setFullname(Optional<String> fullname) {
		this.fullname = wrapNull(fullname);
	}

	public void setFullname(String fullname) {
		this.fullname = Optional.fromNullable(fullname);
	}

	public void clearFullname() {
		this.fullname = Optional.<String>absent();
	}

	private Optional<String> fullnameRuby = Optional.<String>absent();

	public Optional<String> getFullnameRuby() {
		return wrapNull(fullnameRuby);
	}

	public void setFullnameRuby(Optional<String> fullnameRuby) {
		this.fullnameRuby = wrapNull(fullnameRuby);
	}

	public void setFullnameRuby(String fullnameRuby) {
		this.fullnameRuby = Optional.fromNullable(fullnameRuby);
	}

	public void clearFullnameRuby() {
		this.fullnameRuby = Optional.<String>absent();
	}

	private Optional<String> password = Optional.<String>absent();

	public Optional<String> getPassword() {
		return wrapNull(password);
	}

	public void setPassword(Optional<String> password) {
		this.password = wrapNull(password);
	}

	public void setPassword(String password) {
		this.password = Optional.fromNullable(password);
	}

	public void clearPassword() {
		this.password = Optional.<String>absent();
	}

	private Optional<String> passwordConfirmation = Optional.<String>absent();

	public Optional<String> getPasswordConfirmation() {
		return wrapNull(passwordConfirmation);
	}

	public void setPasswordConfirmation(Optional<String> passwordConfirmation) {
		this.passwordConfirmation = wrapNull(passwordConfirmation);
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = Optional.fromNullable(passwordConfirmation);
	}

	public void clearPasswordConfirmation() {
		this.passwordConfirmation = Optional.<String>absent();
	}

	private Optional<String> rememberMe = Optional.<String>absent();

	public Optional<String> getRememberMe() {
		return wrapNull(rememberMe);
	}

	public void setRememberMe(Optional<String> rememberMe) {
		this.rememberMe = wrapNull(rememberMe);
	}

	public void setRememberMe(String rememberMe) {
		this.rememberMe = Optional.fromNullable(rememberMe);
	}

	public void clearRememberMe() {
		this.rememberMe = Optional.<String>absent();
	}

	private LinkedList<Demand> demands = new LinkedList<Demand>();

	public List<Demand> getDemands() {
		return new LinkedList<Demand>(wrapNull(demands));
	}

	public void setDemands(List<Demand> demands) {
		this.demands = new LinkedList<Demand>(wrapNull(demands));
	}

	public void clearDemands() {
		this.demands = new LinkedList<Demand>();
	}

	private LinkedList<ReservationCandidate> reservationCandidates = new LinkedList<ReservationCandidate>();

	public List<ReservationCandidate> getReservationCandidates() {
		return new LinkedList<ReservationCandidate>(wrapNull(reservationCandidates));
	}

	public void setReservationCandidates(List<ReservationCandidate> reservationCandidates) {
		this.reservationCandidates = new LinkedList<ReservationCandidate>(wrapNull(reservationCandidates));
	}

	public void clearReservationCandidates() {
		this.reservationCandidates = new LinkedList<ReservationCandidate>();
	}

	private LinkedList<Reservation> reservations = new LinkedList<Reservation>();

	public List<Reservation> getReservations() {
		return new LinkedList<Reservation>(wrapNull(reservations));
	}

	public void setReservations(List<Reservation> reservations) {
		this.reservations = new LinkedList<Reservation>(wrapNull(reservations));
	}

	public void clearReservations() {
		this.reservations = new LinkedList<Reservation>();
	}

	private Optional<ServiceProvider> serviceProvider = Optional.<ServiceProvider>absent();

	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = Optional.<ServiceProvider>fromNullable(serviceProvider);
	}

	public void clearServiceProvider() {
		this.serviceProvider = Optional.<ServiceProvider>absent();
	}
}
