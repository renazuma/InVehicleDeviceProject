package com.kogasoftware.odt.invehicledevice.apiclient.model.base;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.apiclient.ApiClient.ResponseConverter;
import com.kogasoftware.odt.apiclient.ApiClients;
import com.kogasoftware.odt.invehicledevice.apiclient.model.*;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.jsondeserializer.*;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.jsonview.*;

/**
 * オペレーター
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class OperatorBase extends Model {
	private static final long serialVersionUID = 4409045715248500952L;

	// Columns
	@JsonProperty private Optional<String> authenticationToken = Optional.absent();
	@JsonProperty private Optional<String> email = Optional.absent();
	@JsonProperty private String firstName = "";
	@JsonProperty private Integer id = 0;
	@JsonProperty private String lastName = "";
	@JsonProperty private String login = "";
	@JsonProperty private Optional<Integer> serviceProviderId = Optional.absent();
	@JsonProperty private Optional<String> auditComment = Optional.absent();
	@JsonProperty private Optional<String> password = Optional.absent();
	@JsonProperty private Optional<String> passwordConfirmation = Optional.absent();
	@JsonProperty private Optional<String> rememberMe = Optional.absent();

	// Associations
	@JsonProperty @JsonView(AssociationView.class) private Optional<ServiceProvider> serviceProvider = Optional.absent();

	public static final String UNDERSCORE = "operator";
	public static final ResponseConverter<Operator> RESPONSE_CONVERTER = getResponseConverter(Operator.class);
	public static final ResponseConverter<List<Operator>> LIST_RESPONSE_CONVERTER = getListResponseConverter(Operator.class);

	public static Operator parse(String jsonString) throws IOException {
		return parse(jsonString, Operator.class);
	}

	public static List<Operator> parseList(String jsonString) throws IOException {
		return parseList(jsonString, Operator.class);
	}

	@JsonIgnore
	public Optional<String> getAuthenticationToken() {
		return wrapNull(authenticationToken);
	}

	@JsonIgnore
	public void setAuthenticationToken(Optional<String> authenticationToken) {
		this.authenticationToken = wrapNull(authenticationToken);
	}

	@JsonIgnore
	public void setAuthenticationToken(String authenticationToken) {
		setAuthenticationToken(Optional.fromNullable(authenticationToken));
	}

	public void clearAuthenticationToken() {
		setAuthenticationToken(Optional.<String>absent());
	}

	@JsonIgnore
	public Optional<String> getEmail() {
		return wrapNull(email);
	}

	@JsonIgnore
	public void setEmail(Optional<String> email) {
		this.email = wrapNull(email);
	}

	@JsonIgnore
	public void setEmail(String email) {
		setEmail(Optional.fromNullable(email));
	}

	public void clearEmail() {
		setEmail(Optional.<String>absent());
	}

	@JsonIgnore
	public String getFirstName() {
		return wrapNull(firstName);
	}

	@JsonIgnore
	public void setFirstName(String firstName) {
		this.firstName = wrapNull(firstName);
	}

	@Override
	@JsonIgnore
	public Integer getId() {
		return wrapNull(id);
	}

	@JsonIgnore
	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	@JsonIgnore
	public String getLastName() {
		return wrapNull(lastName);
	}

	@JsonIgnore
	public void setLastName(String lastName) {
		this.lastName = wrapNull(lastName);
	}

	@JsonIgnore
	public String getLogin() {
		return wrapNull(login);
	}

	@JsonIgnore
	public void setLogin(String login) {
		this.login = wrapNull(login);
	}

	@JsonIgnore
	public Optional<Integer> getServiceProviderId() {
		return wrapNull(serviceProviderId);
	}

	@JsonIgnore
	public void setServiceProviderId(Optional<Integer> serviceProviderId) {
		this.serviceProviderId = wrapNull(serviceProviderId);
		for (ServiceProvider presentServiceProvider : getServiceProvider().asSet()) {
			for (Integer presentServiceProviderId : getServiceProviderId().asSet()) {
				presentServiceProvider.setId(presentServiceProviderId);
			}
		}
	}

	@JsonIgnore
	public void setServiceProviderId(Integer serviceProviderId) {
		setServiceProviderId(Optional.fromNullable(serviceProviderId));
	}

	public void clearServiceProviderId() {
		setServiceProviderId(Optional.<Integer>absent());
	}

	@JsonIgnore
	public Optional<String> getAuditComment() {
		return wrapNull(auditComment);
	}

	@JsonIgnore
	public void setAuditComment(Optional<String> auditComment) {
		this.auditComment = wrapNull(auditComment);
	}

	@JsonIgnore
	public void setAuditComment(String auditComment) {
		setAuditComment(Optional.fromNullable(auditComment));
	}

	public void clearAuditComment() {
		setAuditComment(Optional.<String>absent());
	}

	@JsonIgnore
	public Optional<String> getPassword() {
		return wrapNull(password);
	}

	@JsonIgnore
	public void setPassword(Optional<String> password) {
		this.password = wrapNull(password);
	}

	@JsonIgnore
	public void setPassword(String password) {
		setPassword(Optional.fromNullable(password));
	}

	public void clearPassword() {
		setPassword(Optional.<String>absent());
	}

	@JsonIgnore
	public Optional<String> getPasswordConfirmation() {
		return wrapNull(passwordConfirmation);
	}

	@JsonIgnore
	public void setPasswordConfirmation(Optional<String> passwordConfirmation) {
		this.passwordConfirmation = wrapNull(passwordConfirmation);
	}

	@JsonIgnore
	public void setPasswordConfirmation(String passwordConfirmation) {
		setPasswordConfirmation(Optional.fromNullable(passwordConfirmation));
	}

	public void clearPasswordConfirmation() {
		setPasswordConfirmation(Optional.<String>absent());
	}

	@JsonIgnore
	public Optional<String> getRememberMe() {
		return wrapNull(rememberMe);
	}

	@JsonIgnore
	public void setRememberMe(Optional<String> rememberMe) {
		this.rememberMe = wrapNull(rememberMe);
	}

	@JsonIgnore
	public void setRememberMe(String rememberMe) {
		setRememberMe(Optional.fromNullable(rememberMe));
	}

	public void clearRememberMe() {
		setRememberMe(Optional.<String>absent());
	}

	@JsonIgnore
	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	@JsonIgnore
	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
		for (ServiceProvider presentServiceProvider : getServiceProvider().asSet()) {
			setServiceProviderId(presentServiceProvider.getId());
		}
	}

	@JsonIgnore
	public void setServiceProvider(ServiceProvider serviceProvider) {
		setServiceProvider(Optional.fromNullable(serviceProvider));
	}

	public void clearServiceProvider() {
		setServiceProvider(Optional.<ServiceProvider>absent());
	}

	@Override
	public Operator clone() {
		return clone(true);
	}

	@Override
	public Operator clone(Boolean withAssociation) {
		return super.clone(Operator.class, withAssociation);
	}
}
