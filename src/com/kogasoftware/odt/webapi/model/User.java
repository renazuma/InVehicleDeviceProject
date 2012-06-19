package com.kogasoftware.odt.webapi.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public class User extends Model {
	private static final long serialVersionUID = 5790599050944439576L;

	public User() {
	}

	public User(JSONObject jsonObject) throws JSONException {
		try {
			fillMembers(this, jsonObject);
		} catch (ParseException e) {
			throw new JSONException(e.toString() + "\n" + ExceptionUtils.getStackTrace(e));
		}
	}

	public static void fillMembers(User model, JSONObject jsonObject) throws JSONException, ParseException {
		model.setAddress(parseString(jsonObject, "address"));
		model.setAge(parseInteger(jsonObject, "age"));
		model.setBirthday(parseDate(jsonObject, "birthday"));
		model.setEmail(parseOptionalString(jsonObject, "email"));
		model.setEmail2(parseOptionalString(jsonObject, "email2"));
		model.setFelicaId(parseOptionalString(jsonObject, "felica_id"));
		model.setFirstName(parseString(jsonObject, "first_name"));
		model.setFirstNameRuby(parseString(jsonObject, "first_name_ruby"));
		model.setHandicapped(parseOptionalBoolean(jsonObject, "handicapped"));
		model.setId(parseInteger(jsonObject, "id"));
		model.setLastName(parseString(jsonObject, "last_name"));
		model.setLastNameRuby(parseString(jsonObject, "last_name_ruby"));
		model.setLogin(parseString(jsonObject, "login"));
		model.setMemo(parseOptionalString(jsonObject, "memo"));
		model.setNeededCare(parseOptionalBoolean(jsonObject, "needed_care"));
		model.setRecommendNotification(parseOptionalBoolean(jsonObject, "recommend_notification"));
		model.setRecommendOk(parseOptionalBoolean(jsonObject, "recommend_ok"));
		model.setReserveNotification(parseOptionalBoolean(jsonObject, "reserve_notification"));
		model.setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		model.setSex(parseInteger(jsonObject, "sex"));
		model.setTelephoneNumber(parseString(jsonObject, "telephone_number"));
		model.setTelephoneNumber2(parseOptionalString(jsonObject, "telephone_number2"));
		model.setUpdateNotification(parseOptionalBoolean(jsonObject, "update_notification"));
		model.setWheelchair(parseOptionalBoolean(jsonObject, "wheelchair"));
		model.setZip(parseOptionalString(jsonObject, "zip"));
		model.setAuditComment(parseOptionalString(jsonObject, "audit_comment"));
		model.setFullname(parseOptionalString(jsonObject, "fullname"));
		model.setFullnameRuby(parseOptionalString(jsonObject, "fullname_ruby"));
		model.setPassword(parseOptionalString(jsonObject, "password"));
		model.setPasswordConfirmation(parseOptionalString(jsonObject, "password_confirmation"));
		model.setRememberMe(parseOptionalString(jsonObject, "remember_me"));
		model.setDemands(Demand.parseList(jsonObject, "demands"));
		model.setReservationCandidates(ReservationCandidate.parseList(jsonObject, "reservation_candidates"));
		model.setReservations(Reservation.parseList(jsonObject, "reservations"));
		model.setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
	}

	public static Optional<User> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<User> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.of(new User(jsonObject));
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
	protected JSONObject toJSONObject(Boolean recursive, Integer depth) throws JSONException {
		depth++;
		if (depth > MAX_RECURSE_DEPTH) {
			return new JSONObject();
		}
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
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("last_name", toJSON(getLastName()));
		jsonObject.put("last_name_ruby", toJSON(getLastNameRuby()));
		jsonObject.put("login", toJSON(getLogin()));
		jsonObject.put("memo", toJSON(getMemo().orNull()));
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
		jsonObject.put("zip", toJSON(getZip().orNull()));
		jsonObject.put("audit_comment", toJSON(getAuditComment().orNull()));
		jsonObject.put("fullname", toJSON(getFullname().orNull()));
		jsonObject.put("fullname_ruby", toJSON(getFullnameRuby().orNull()));
		jsonObject.put("password", toJSON(getPassword().orNull()));
		jsonObject.put("password_confirmation", toJSON(getPasswordConfirmation().orNull()));
		jsonObject.put("remember_me", toJSON(getRememberMe().orNull()));
		if (getDemands().size() > 0 && recursive) {
			jsonObject.put("demands", toJSON(getDemands(), true, depth));
		}
		if (getReservationCandidates().size() > 0 && recursive) {
			jsonObject.put("reservation_candidates", toJSON(getReservationCandidates(), true, depth));
		}
		if (getReservations().size() > 0 && recursive) {
			jsonObject.put("reservations", toJSON(getReservations(), true, depth));
		}
		if (getServiceProvider().isPresent()) {
			if (recursive) {
				jsonObject.put("service_provider", getServiceProvider().get().toJSONObject(true, depth));
			} else {
				jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
			}
		}
		return jsonObject;
	}

	private void writeObject(ObjectOutputStream objectOutputStream)
			throws IOException {
		try {
			objectOutputStream.writeObject(toJSONObject(true).toString());
		} catch (JSONException e) {
			throw new IOException(e);
		}
	}

	private void readObject(ObjectInputStream objectInputStream)
		throws IOException, ClassNotFoundException {
		Object object = objectInputStream.readObject();
		if (!(object instanceof String)) {
			return;
		}
		String jsonString = (String) object;
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			fillMembers(this, jsonObject);
		} catch (JSONException e) {
			throw new IOException(e);
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	@Override
	public User cloneByJSON() throws JSONException {
		return new User(toJSONObject(true));
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

	private Optional<String> email = Optional.absent();

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
		this.email = Optional.absent();
	}

	private Optional<String> email2 = Optional.absent();

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
		this.email2 = Optional.absent();
	}

	private Optional<String> felicaId = Optional.absent();

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
		this.felicaId = Optional.absent();
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

	private Optional<Boolean> handicapped = Optional.absent();

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
		this.handicapped = Optional.absent();
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

	private Optional<String> memo = Optional.absent();

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
		this.memo = Optional.absent();
	}

	private Optional<Boolean> neededCare = Optional.absent();

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
		this.neededCare = Optional.absent();
	}

	private Optional<Boolean> recommendNotification = Optional.absent();

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
		this.recommendNotification = Optional.absent();
	}

	private Optional<Boolean> recommendOk = Optional.absent();

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
		this.recommendOk = Optional.absent();
	}

	private Optional<Boolean> reserveNotification = Optional.absent();

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
		this.reserveNotification = Optional.absent();
	}

	private Optional<Integer> serviceProviderId = Optional.absent();

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
		this.serviceProviderId = Optional.absent();
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

	private Optional<String> telephoneNumber2 = Optional.absent();

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
		this.telephoneNumber2 = Optional.absent();
	}

	private Optional<Boolean> updateNotification = Optional.absent();

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
		this.updateNotification = Optional.absent();
	}

	private Optional<Boolean> wheelchair = Optional.absent();

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
		this.wheelchair = Optional.absent();
	}

	private Optional<String> zip = Optional.absent();

	public Optional<String> getZip() {
		return wrapNull(zip);
	}

	public void setZip(Optional<String> zip) {
		this.zip = wrapNull(zip);
	}

	public void setZip(String zip) {
		this.zip = Optional.fromNullable(zip);
	}

	public void clearZip() {
		this.zip = Optional.absent();
	}

	private Optional<String> auditComment = Optional.absent();

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
		this.auditComment = Optional.absent();
	}

	private Optional<String> fullname = Optional.absent();

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
		this.fullname = Optional.absent();
	}

	private Optional<String> fullnameRuby = Optional.absent();

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
		this.fullnameRuby = Optional.absent();
	}

	private Optional<String> password = Optional.absent();

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
		this.password = Optional.absent();
	}

	private Optional<String> passwordConfirmation = Optional.absent();

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
		this.passwordConfirmation = Optional.absent();
	}

	private Optional<String> rememberMe = Optional.absent();

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
		this.rememberMe = Optional.absent();
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

	private Optional<ServiceProvider> serviceProvider = Optional.absent();

	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = Optional.fromNullable(serviceProvider);
	}

	public void clearServiceProvider() {
		this.serviceProvider = Optional.absent();
	}
}
