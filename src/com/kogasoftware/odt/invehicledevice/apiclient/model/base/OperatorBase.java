package com.kogasoftware.odt.webapi.model.base;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;
import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPI.ResponseConverter;
import com.kogasoftware.odt.webapi.model.*;

@SuppressWarnings("unused")
public abstract class OperatorBase extends Model {
	private static final long serialVersionUID = 292885598331744478L;
	public static final ResponseConverter<Operator> RESPONSE_CONVERTER = new ResponseConverter<Operator>() {
		@Override
		public Operator convert(byte[] rawResponse) throws JSONException {
			return parse(WebAPI.parseJSONObject(rawResponse));
		}
	};
	public static final ResponseConverter<List<Operator>> LIST_RESPONSE_CONVERTER = new ResponseConverter<List<Operator>>() {
		@Override
		public List<Operator> convert(byte[] rawResponse) throws JSONException {
			return parseList(WebAPI.parseJSONArray(rawResponse));
		}
	};
	@Override
	public void fill(JSONObject jsonObject) throws JSONException {
		setAuthenticationToken(parseOptionalString(jsonObject, "authentication_token"));
		setEmail(parseOptionalString(jsonObject, "email"));
		setFirstName(parseString(jsonObject, "first_name"));
		setId(parseInteger(jsonObject, "id"));
		setLastName(parseString(jsonObject, "last_name"));
		setLogin(parseString(jsonObject, "login"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setAuditComment(parseOptionalString(jsonObject, "audit_comment"));
		setPassword(parseOptionalString(jsonObject, "password"));
		setPasswordConfirmation(parseOptionalString(jsonObject, "password_confirmation"));
		setRememberMe(parseOptionalString(jsonObject, "remember_me"));
		setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
	}

	public static Optional<Operator> parse(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return Optional.of(parse(jsonObject.getJSONObject(key)));
	}

	public static Operator parse(JSONObject jsonObject) throws JSONException {
		Operator model = new Operator();
		model.fill(jsonObject);
		return model;
	}

	public static LinkedList<Operator> parseList(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return new LinkedList<Operator>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<Operator> parseList(JSONArray jsonArray) throws JSONException {
		LinkedList<Operator> models = new LinkedList<Operator>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(parse(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	@Override
	protected JSONObject toJSONObject(Boolean recursive, Integer depth) throws JSONException {
		if (depth > MAX_RECURSE_DEPTH) {
			return new JSONObject();
		}
		Integer nextDepth = depth + 1;
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("authentication_token", toJSON(getAuthenticationToken()));
		jsonObject.put("email", toJSON(getEmail()));
		jsonObject.put("first_name", toJSON(getFirstName()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("last_name", toJSON(getLastName()));
		jsonObject.put("login", toJSON(getLogin()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId()));
		jsonObject.put("audit_comment", toJSON(getAuditComment()));
		jsonObject.put("password", toJSON(getPassword()));
		jsonObject.put("password_confirmation", toJSON(getPasswordConfirmation()));
		jsonObject.put("remember_me", toJSON(getRememberMe()));
		if (getServiceProvider().isPresent()) {
			if (recursive) {
				jsonObject.put("service_provider", getServiceProvider().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
			}
		}
		return jsonObject;
	}

	@Override
	public Operator cloneByJSON() throws JSONException {
		return parse(toJSONObject(true));
	}

	private Optional<String> authenticationToken = Optional.absent();

	public Optional<String> getAuthenticationToken() {
		return wrapNull(authenticationToken);
	}

	public void setAuthenticationToken(Optional<String> authenticationToken) {
		this.authenticationToken = wrapNull(authenticationToken);
	}

	public void setAuthenticationToken(String authenticationToken) {
		setAuthenticationToken(Optional.fromNullable(authenticationToken));
	}

	public void clearAuthenticationToken() {
		setAuthenticationToken(Optional.<String>absent());
	}

	private Optional<String> email = Optional.absent();

	public Optional<String> getEmail() {
		return wrapNull(email);
	}

	public void setEmail(Optional<String> email) {
		this.email = wrapNull(email);
	}

	public void setEmail(String email) {
		setEmail(Optional.fromNullable(email));
	}

	public void clearEmail() {
		setEmail(Optional.<String>absent());
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

	private String login = "";

	public String getLogin() {
		return wrapNull(login);
	}

	public void setLogin(String login) {
		this.login = wrapNull(login);
	}

	private Optional<Integer> serviceProviderId = Optional.absent();

	public Optional<Integer> getServiceProviderId() {
		return wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Optional<Integer> serviceProviderId) {
		this.serviceProviderId = wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Integer serviceProviderId) {
		setServiceProviderId(Optional.fromNullable(serviceProviderId));
	}

	public void clearServiceProviderId() {
		setServiceProviderId(Optional.<Integer>absent());
	}

	private Optional<String> auditComment = Optional.absent();

	public Optional<String> getAuditComment() {
		return wrapNull(auditComment);
	}

	public void setAuditComment(Optional<String> auditComment) {
		this.auditComment = wrapNull(auditComment);
	}

	public void setAuditComment(String auditComment) {
		setAuditComment(Optional.fromNullable(auditComment));
	}

	public void clearAuditComment() {
		setAuditComment(Optional.<String>absent());
	}

	private Optional<String> password = Optional.absent();

	public Optional<String> getPassword() {
		return wrapNull(password);
	}

	public void setPassword(Optional<String> password) {
		this.password = wrapNull(password);
	}

	public void setPassword(String password) {
		setPassword(Optional.fromNullable(password));
	}

	public void clearPassword() {
		setPassword(Optional.<String>absent());
	}

	private Optional<String> passwordConfirmation = Optional.absent();

	public Optional<String> getPasswordConfirmation() {
		return wrapNull(passwordConfirmation);
	}

	public void setPasswordConfirmation(Optional<String> passwordConfirmation) {
		this.passwordConfirmation = wrapNull(passwordConfirmation);
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		setPasswordConfirmation(Optional.fromNullable(passwordConfirmation));
	}

	public void clearPasswordConfirmation() {
		setPasswordConfirmation(Optional.<String>absent());
	}

	private Optional<String> rememberMe = Optional.absent();

	public Optional<String> getRememberMe() {
		return wrapNull(rememberMe);
	}

	public void setRememberMe(Optional<String> rememberMe) {
		this.rememberMe = wrapNull(rememberMe);
	}

	public void setRememberMe(String rememberMe) {
		setRememberMe(Optional.fromNullable(rememberMe));
	}

	public void clearRememberMe() {
		setRememberMe(Optional.<String>absent());
	}

	private Optional<ServiceProvider> serviceProvider = Optional.<ServiceProvider>absent();

	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		setServiceProvider(Optional.fromNullable(serviceProvider));
	}

	public void clearServiceProvider() {
		setServiceProvider(Optional.<ServiceProvider>absent());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(authenticationToken)
			.append(email)
			.append(firstName)
			.append(id)
			.append(lastName)
			.append(login)
			.append(serviceProviderId)
			.append(auditComment)
			.append(password)
			.append(passwordConfirmation)
			.append(rememberMe)
			.append(serviceProvider)
			.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof OperatorBase)) {
			return false;
		}
		OperatorBase other = (OperatorBase) obj;
		return new EqualsBuilder()
			.append(authenticationToken, other.authenticationToken)
			.append(email, other.email)
			.append(firstName, other.firstName)
			.append(id, other.id)
			.append(lastName, other.lastName)
			.append(login, other.login)
			.append(serviceProviderId, other.serviceProviderId)
			.append(auditComment, other.auditComment)
			.append(password, other.password)
			.append(passwordConfirmation, other.passwordConfirmation)
			.append(rememberMe, other.rememberMe)
			.append(serviceProvider, other.serviceProvider)
			.isEquals();
	}
}
