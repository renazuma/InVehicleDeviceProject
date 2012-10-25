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
 * 運転手
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class DriverBase extends Model {
	private static final long serialVersionUID = 221008176444225965L;

	// Columns
	@JsonProperty private Date createdAt = new Date();
	@JsonProperty private Optional<Date> deletedAt = Optional.absent();
	@JsonProperty private String firstName = "";
	@JsonProperty private Integer id = 0;
	@JsonProperty private String lastName = "";
	@JsonProperty private Optional<Integer> serviceProviderId = Optional.absent();
	@JsonProperty private String telephoneNumber = "";
	@JsonProperty private Date updatedAt = new Date();

	// Associations
	@JsonProperty @JsonView(AssociationView.class) private Optional<ServiceProvider> serviceProvider = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private List<ServiceUnit> serviceUnits = Lists.newLinkedList();

	public static final String UNDERSCORE = "driver";
	public static final ResponseConverter<Driver> RESPONSE_CONVERTER = getResponseConverter(Driver.class);
	public static final ResponseConverter<List<Driver>> LIST_RESPONSE_CONVERTER = getListResponseConverter(Driver.class);

	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}

	public static Driver parse(String jsonString) throws IOException {
		return parse(jsonString, Driver.class);
	}

	public static List<Driver> parseList(String jsonString) throws IOException {
		return parseList(jsonString, Driver.class);
	}

	@JsonIgnore
	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	@JsonIgnore
	public void setCreatedAt(Date createdAt) {
		refreshUpdatedAt();
		this.createdAt = wrapNull(createdAt);
	}

	@JsonIgnore
	public Optional<Date> getDeletedAt() {
		return wrapNull(deletedAt);
	}

	@JsonIgnore
	public void setDeletedAt(Optional<Date> deletedAt) {
		refreshUpdatedAt();
		this.deletedAt = wrapNull(deletedAt);
	}

	@JsonIgnore
	public void setDeletedAt(Date deletedAt) {
		setDeletedAt(Optional.fromNullable(deletedAt));
	}

	public void clearDeletedAt() {
		setDeletedAt(Optional.<Date>absent());
	}

	@JsonIgnore
	public String getFirstName() {
		return wrapNull(firstName);
	}

	@JsonIgnore
	public void setFirstName(String firstName) {
		refreshUpdatedAt();
		this.firstName = wrapNull(firstName);
	}

	@Override
	@JsonIgnore
	public Integer getId() {
		return wrapNull(id);
	}

	@JsonIgnore
	public void setId(Integer id) {
		refreshUpdatedAt();
		this.id = wrapNull(id);
	}

	@JsonIgnore
	public String getLastName() {
		return wrapNull(lastName);
	}

	@JsonIgnore
	public void setLastName(String lastName) {
		refreshUpdatedAt();
		this.lastName = wrapNull(lastName);
	}

	@JsonIgnore
	public Optional<Integer> getServiceProviderId() {
		return wrapNull(serviceProviderId);
	}

	@JsonIgnore
	public void setServiceProviderId(Optional<Integer> serviceProviderId) {
		refreshUpdatedAt();
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
	public String getTelephoneNumber() {
		return wrapNull(telephoneNumber);
	}

	@JsonIgnore
	public void setTelephoneNumber(String telephoneNumber) {
		refreshUpdatedAt();
		this.telephoneNumber = wrapNull(telephoneNumber);
	}

	@JsonIgnore
	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	@JsonIgnore
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	@JsonIgnore
	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	@JsonIgnore
	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		refreshUpdatedAt();
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

	@Override
	public Driver clone() {
		return super.clone(Driver.class);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(createdAt)
			.append(deletedAt)
			.append(firstName)
			.append(id)
			.append(lastName)
			.append(serviceProviderId)
			.append(telephoneNumber)
			.append(updatedAt)
			.append(serviceProvider)
			.append(serviceUnits)
			.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof DriverBase)) {
			return false;
		}
		DriverBase other = (DriverBase) obj;
		return new EqualsBuilder()
			.append(createdAt, other.createdAt)
			.append(deletedAt, other.deletedAt)
			.append(firstName, other.firstName)
			.append(id, other.id)
			.append(lastName, other.lastName)
			.append(serviceProviderId, other.serviceProviderId)
			.append(telephoneNumber, other.telephoneNumber)
			.append(updatedAt, other.updatedAt)
			.append(serviceProvider, other.serviceProvider)
			.append(serviceUnits, other.serviceUnits)
			.isEquals();
	}
}
