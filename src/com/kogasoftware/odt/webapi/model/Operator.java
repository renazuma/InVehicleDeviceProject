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

public class Operator extends Model {
	private static final long serialVersionUID = 4270259938479785548L;

	public Operator() {
	}

	public Operator(JSONObject jsonObject) throws JSONException, ParseException {
		setAuthenticationToken(parseOptionalString(jsonObject, "authentication_token"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setCurrentSignInAt(parseOptionalDate(jsonObject, "current_sign_in_at"));
		setCurrentSignInIp(parseOptionalString(jsonObject, "current_sign_in_ip"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setEmail(parseOptionalString(jsonObject, "email"));
		setEncryptedPassword(parseString(jsonObject, "encrypted_password"));
		setFirstName(parseString(jsonObject, "first_name"));
		setId(parseInteger(jsonObject, "id"));
		setLastName(parseString(jsonObject, "last_name"));
		setLastSignInAt(parseOptionalDate(jsonObject, "last_sign_in_at"));
		setLastSignInIp(parseOptionalString(jsonObject, "last_sign_in_ip"));
		setLogin(parseString(jsonObject, "login"));
		setRememberCreatedAt(parseOptionalDate(jsonObject, "remember_created_at"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setSignInCount(parseOptionalInteger(jsonObject, "sign_in_count"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setAuditComment(parseOptionalString(jsonObject, "audit_comment"));
		setPassword(parseOptionalString(jsonObject, "password"));
		setPasswordConfirmation(parseOptionalString(jsonObject, "password_confirmation"));
		setRememberMe(parseOptionalString(jsonObject, "remember_me"));
		setServiceProvider(parseOptionalString(jsonObject, "service_provider"));
		setReservations(Reservation.parseList(jsonObject, "reservations"));
	}

	public static Optional<Operator> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<Operator>absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<Operator> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.<Operator>of(new Operator(jsonObject));
	}

	public static LinkedList<Operator> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<Operator>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<Operator> parseList(JSONArray jsonArray) throws JSONException, ParseException {
		LinkedList<Operator> models = new LinkedList<Operator>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new Operator(jsonArray.getJSONObject(i)));
		}
		return models;
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
		jsonObject.put("first_name", toJSON(getFirstName()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("last_name", toJSON(getLastName()));
		jsonObject.put("last_sign_in_at", toJSON(getLastSignInAt().orNull()));
		jsonObject.put("last_sign_in_ip", toJSON(getLastSignInIp().orNull()));
		jsonObject.put("login", toJSON(getLogin()));
		jsonObject.put("remember_created_at", toJSON(getRememberCreatedAt().orNull()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("sign_in_count", toJSON(getSignInCount().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("audit_comment", toJSON(getAuditComment().orNull()));
		jsonObject.put("password", toJSON(getPassword().orNull()));
		jsonObject.put("password_confirmation", toJSON(getPasswordConfirmation().orNull()));
		jsonObject.put("remember_me", toJSON(getRememberMe().orNull()));
		jsonObject.put("service_provider", toJSON(getServiceProvider().orNull()));
		jsonObject.put("reservations", toJSON(getReservations()));
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

	public void clearAuthenticationToken() {
		this.authenticationToken = Optional.<String>absent();
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

	private String encryptedPassword = "";

	public String getEncryptedPassword() {
		return wrapNull(encryptedPassword);
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = wrapNull(encryptedPassword);
	}

	private String firstName = "";

	public String getFirstName() {
		return wrapNull(firstName);
	}

	public void setFirstName(String firstName) {
		this.firstName = wrapNull(firstName);
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

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
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

	private Optional<String> serviceProvider = Optional.<String>absent();

	public Optional<String> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	public void setServiceProvider(Optional<String> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
	}

	public void setServiceProvider(String serviceProvider) {
		this.serviceProvider = Optional.fromNullable(serviceProvider);
	}

	public void clearServiceProvider() {
		this.serviceProvider = Optional.<String>absent();
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
}
