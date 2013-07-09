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
 * 車両
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class VehicleBase extends Model {
	private static final long serialVersionUID = 2329981797512287273L;

	// Columns
	@JsonProperty private Integer capacity = 0;
	@JsonProperty private Date createdAt = new Date();
	@JsonProperty private Optional<Date> deletedAt = Optional.absent();
	@JsonProperty private Integer id = 0;
	@JsonProperty private Optional<String> image = Optional.absent();
	@JsonProperty private String modelName = "";
	@JsonProperty private String number = "";
	@JsonProperty private Optional<Integer> serviceProviderId = Optional.absent();
	@JsonProperty private Date updatedAt = new Date();

	// Associations
	@JsonProperty @JsonView(AssociationView.class) private Optional<ServiceProvider> serviceProvider = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private List<ServiceUnit> serviceUnits = Lists.newLinkedList();

	public static final String UNDERSCORE = "vehicle";
	public static final ResponseConverter<Vehicle> RESPONSE_CONVERTER = getResponseConverter(Vehicle.class);
	public static final ResponseConverter<List<Vehicle>> LIST_RESPONSE_CONVERTER = getListResponseConverter(Vehicle.class);

	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}

	public static Vehicle parse(String jsonString) throws IOException {
		return parse(jsonString, Vehicle.class);
	}

	public static List<Vehicle> parseList(String jsonString) throws IOException {
		return parseList(jsonString, Vehicle.class);
	}

	@JsonIgnore
	public Integer getCapacity() {
		return wrapNull(capacity);
	}

	@JsonIgnore
	public void setCapacity(Integer capacity) {
		refreshUpdatedAt();
		this.capacity = wrapNull(capacity);
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
	public Optional<String> getImage() {
		return wrapNull(image);
	}

	@JsonIgnore
	public void setImage(Optional<String> image) {
		refreshUpdatedAt();
		this.image = wrapNull(image);
	}

	@JsonIgnore
	public void setImage(String image) {
		setImage(Optional.fromNullable(image));
	}

	public void clearImage() {
		setImage(Optional.<String>absent());
	}

	@JsonIgnore
	public String getModelName() {
		return wrapNull(modelName);
	}

	@JsonIgnore
	public void setModelName(String modelName) {
		refreshUpdatedAt();
		this.modelName = wrapNull(modelName);
	}

	@JsonIgnore
	public String getNumber() {
		return wrapNull(number);
	}

	@JsonIgnore
	public void setNumber(String number) {
		refreshUpdatedAt();
		this.number = wrapNull(number);
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
	public Vehicle clone() {
		return clone(true);
	}

	@Override
	public Vehicle clone(Boolean withAssociation) {
		return super.clone(Vehicle.class, withAssociation);
	}
}
