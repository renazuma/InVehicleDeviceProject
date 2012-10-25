package com.kogasoftware.odt.invehicledevice.apiclient.model.base;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
 * 車載器
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class InVehicleDeviceBase extends Model {
	private static final long serialVersionUID = 6553318018433220819L;

	// Columns
	@JsonProperty private Optional<String> authenticationToken = Optional.absent();
	@JsonProperty private Integer id = 0;
	@JsonProperty private String login = "";
	@JsonProperty private String modelName = "";
	@JsonProperty private Optional<Integer> serviceProviderId = Optional.absent();
	@JsonProperty private String typeNumber = "";
	@JsonProperty private Optional<String> auditComment = Optional.absent();
	@JsonProperty private Optional<String> password = Optional.absent();
	@JsonProperty private Optional<String> passwordConfirmation = Optional.absent();
	@JsonProperty private Optional<String> rememberMe = Optional.absent();

	// Associations
	@JsonProperty @JsonView(AssociationView.class) private Optional<ServiceProvider> serviceProvider = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private List<ServiceUnit> serviceUnits = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<UnitAssignment> unitAssignments = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<VehicleNotification> vehicleNotifications = Lists.newLinkedList();

	public static final String UNDERSCORE = "in_vehicle_device";
	public static final ResponseConverter<InVehicleDevice> RESPONSE_CONVERTER = getResponseConverter(InVehicleDevice.class);
	public static final ResponseConverter<List<InVehicleDevice>> LIST_RESPONSE_CONVERTER = getListResponseConverter(InVehicleDevice.class);

	public static InVehicleDevice parse(String jsonString) throws IOException {
		return parse(jsonString, InVehicleDevice.class);
	}

	public static List<InVehicleDevice> parseList(String jsonString) throws IOException {
		return parseList(jsonString, InVehicleDevice.class);
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
	public String getLogin() {
		return wrapNull(login);
	}

	@JsonIgnore
	public void setLogin(String login) {
		this.login = wrapNull(login);
	}

	@JsonIgnore
	public String getModelName() {
		return wrapNull(modelName);
	}

	@JsonIgnore
	public void setModelName(String modelName) {
		this.modelName = wrapNull(modelName);
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
	public String getTypeNumber() {
		return wrapNull(typeNumber);
	}

	@JsonIgnore
	public void setTypeNumber(String typeNumber) {
		this.typeNumber = wrapNull(typeNumber);
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

	@JsonIgnore
	public List<ServiceUnit> getServiceUnits() {
		return wrapNull(serviceUnits);
	}

	@JsonIgnore
	public void setServiceUnits(Iterable<ServiceUnit> serviceUnits) {
		this.serviceUnits = wrapNull(serviceUnits);
	}

	public void clearServiceUnits() {
		setServiceUnits(new LinkedList<ServiceUnit>());
	}

	@JsonIgnore
	public List<UnitAssignment> getUnitAssignments() {
		return wrapNull(unitAssignments);
	}

	@JsonIgnore
	public void setUnitAssignments(Iterable<UnitAssignment> unitAssignments) {
		this.unitAssignments = wrapNull(unitAssignments);
	}

	public void clearUnitAssignments() {
		setUnitAssignments(new LinkedList<UnitAssignment>());
	}

	@JsonIgnore
	public List<VehicleNotification> getVehicleNotifications() {
		return wrapNull(vehicleNotifications);
	}

	@JsonIgnore
	public void setVehicleNotifications(Iterable<VehicleNotification> vehicleNotifications) {
		this.vehicleNotifications = wrapNull(vehicleNotifications);
	}

	public void clearVehicleNotifications() {
		setVehicleNotifications(new LinkedList<VehicleNotification>());
	}

	@Override
	public InVehicleDevice clone() {
		return super.clone(InVehicleDevice.class);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(authenticationToken)
			.append(id)
			.append(login)
			.append(modelName)
			.append(serviceProviderId)
			.append(typeNumber)
			.append(auditComment)
			.append(password)
			.append(passwordConfirmation)
			.append(rememberMe)
			.append(serviceProvider)
			.append(serviceUnits)
			.append(unitAssignments)
			.append(vehicleNotifications)
			.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof InVehicleDeviceBase)) {
			return false;
		}
		InVehicleDeviceBase other = (InVehicleDeviceBase) obj;
		return new EqualsBuilder()
			.append(authenticationToken, other.authenticationToken)
			.append(id, other.id)
			.append(login, other.login)
			.append(modelName, other.modelName)
			.append(serviceProviderId, other.serviceProviderId)
			.append(typeNumber, other.typeNumber)
			.append(auditComment, other.auditComment)
			.append(password, other.password)
			.append(passwordConfirmation, other.passwordConfirmation)
			.append(rememberMe, other.rememberMe)
			.append(serviceProvider, other.serviceProvider)
			.append(serviceUnits, other.serviceUnits)
			.append(unitAssignments, other.unitAssignments)
			.append(vehicleNotifications, other.vehicleNotifications)
			.isEquals();
	}
}
