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
	private static final long serialVersionUID = 7908478778502020576L;

	public Operator() {
	}

	public Operator(JSONObject jsonObject) throws JSONException, ParseException {
		setAuthenticationToken(parseOptionalString(jsonObject, "authentication_token"));
		setEmail(parseOptionalString(jsonObject, "email"));
		setFirstName(parseString(jsonObject, "first_name"));
		setLastName(parseString(jsonObject, "last_name"));
		setLogin(parseString(jsonObject, "login"));
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
		jsonObject.put("email", toJSON(getEmail().orNull()));
		jsonObject.put("first_name", toJSON(getFirstName()));
		jsonObject.put("last_name", toJSON(getLastName()));
		jsonObject.put("login", toJSON(getLogin()));
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

	private String firstName = "";

	public String getFirstName() {
		return wrapNull(firstName);
	}

	public void setFirstName(String firstName) {
		this.firstName = wrapNull(firstName);
	}

	private String lastName = "";

	public String getLastName() {
		return wrapNull(lastName);
	}

	public void setLastName(String lastName) {
		this.lastName = wrapNull(lastName);
	}

	private String login = "";

	public String getLogin() {
		return wrapNull(login);
	}

	public void setLogin(String login) {
		this.login = wrapNull(login);
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
