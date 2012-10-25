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
 * 号車ログ
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class ServiceUnitStatusLogBase extends Model {
	private static final long serialVersionUID = 5488621136488087102L;

	// Columns
	@JsonProperty private Date createdAt = new Date();
	@JsonProperty private Integer id = 0;
	@JsonProperty private BigDecimal latitude = BigDecimal.ZERO;
	@JsonProperty private BigDecimal longitude = BigDecimal.ZERO;
	@JsonProperty private Optional<Boolean> offline = Optional.absent();
	@JsonProperty private Optional<Date> offlineTime = Optional.absent();
	@JsonProperty private Optional<Integer> orientation = Optional.absent();
	@JsonProperty private Optional<Integer> serviceUnitId = Optional.absent();
	@JsonProperty private Optional<Integer> temperature = Optional.absent();
	@JsonProperty private Date updatedAt = new Date();

	// Associations
	@JsonProperty @JsonView(AssociationView.class) private Optional<ServiceUnit> serviceUnit = Optional.absent();

	public static final String UNDERSCORE = "service_unit_status_log";
	public static final ResponseConverter<ServiceUnitStatusLog> RESPONSE_CONVERTER = getResponseConverter(ServiceUnitStatusLog.class);
	public static final ResponseConverter<List<ServiceUnitStatusLog>> LIST_RESPONSE_CONVERTER = getListResponseConverter(ServiceUnitStatusLog.class);

	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}

	public static ServiceUnitStatusLog parse(String jsonString) throws IOException {
		return parse(jsonString, ServiceUnitStatusLog.class);
	}

	public static List<ServiceUnitStatusLog> parseList(String jsonString) throws IOException {
		return parseList(jsonString, ServiceUnitStatusLog.class);
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
	public BigDecimal getLatitude() {
		return wrapNull(latitude);
	}

	@JsonIgnore
	public void setLatitude(BigDecimal latitude) {
		refreshUpdatedAt();
		this.latitude = wrapNull(latitude);
	}

	@JsonIgnore
	public BigDecimal getLongitude() {
		return wrapNull(longitude);
	}

	@JsonIgnore
	public void setLongitude(BigDecimal longitude) {
		refreshUpdatedAt();
		this.longitude = wrapNull(longitude);
	}

	@JsonIgnore
	public Optional<Boolean> getOffline() {
		return wrapNull(offline);
	}

	@JsonIgnore
	public void setOffline(Optional<Boolean> offline) {
		refreshUpdatedAt();
		this.offline = wrapNull(offline);
	}

	@JsonIgnore
	public void setOffline(Boolean offline) {
		setOffline(Optional.fromNullable(offline));
	}

	public void clearOffline() {
		setOffline(Optional.<Boolean>absent());
	}

	@JsonIgnore
	public Optional<Date> getOfflineTime() {
		return wrapNull(offlineTime);
	}

	@JsonIgnore
	public void setOfflineTime(Optional<Date> offlineTime) {
		refreshUpdatedAt();
		this.offlineTime = wrapNull(offlineTime);
	}

	@JsonIgnore
	public void setOfflineTime(Date offlineTime) {
		setOfflineTime(Optional.fromNullable(offlineTime));
	}

	public void clearOfflineTime() {
		setOfflineTime(Optional.<Date>absent());
	}

	@JsonIgnore
	public Optional<Integer> getOrientation() {
		return wrapNull(orientation);
	}

	@JsonIgnore
	public void setOrientation(Optional<Integer> orientation) {
		refreshUpdatedAt();
		this.orientation = wrapNull(orientation);
	}

	@JsonIgnore
	public void setOrientation(Integer orientation) {
		setOrientation(Optional.fromNullable(orientation));
	}

	public void clearOrientation() {
		setOrientation(Optional.<Integer>absent());
	}

	@JsonIgnore
	public Optional<Integer> getServiceUnitId() {
		return wrapNull(serviceUnitId);
	}

	@JsonIgnore
	public void setServiceUnitId(Optional<Integer> serviceUnitId) {
		refreshUpdatedAt();
		this.serviceUnitId = wrapNull(serviceUnitId);
		for (ServiceUnit presentServiceUnit : getServiceUnit().asSet()) {
			for (Integer presentServiceUnitId : getServiceUnitId().asSet()) {
				presentServiceUnit.setId(presentServiceUnitId);
			}
		}
	}

	@JsonIgnore
	public void setServiceUnitId(Integer serviceUnitId) {
		setServiceUnitId(Optional.fromNullable(serviceUnitId));
	}

	public void clearServiceUnitId() {
		setServiceUnitId(Optional.<Integer>absent());
	}

	@JsonIgnore
	public Optional<Integer> getTemperature() {
		return wrapNull(temperature);
	}

	@JsonIgnore
	public void setTemperature(Optional<Integer> temperature) {
		refreshUpdatedAt();
		this.temperature = wrapNull(temperature);
	}

	@JsonIgnore
	public void setTemperature(Integer temperature) {
		setTemperature(Optional.fromNullable(temperature));
	}

	public void clearTemperature() {
		setTemperature(Optional.<Integer>absent());
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
	public Optional<ServiceUnit> getServiceUnit() {
		return wrapNull(serviceUnit);
	}

	@JsonIgnore
	public void setServiceUnit(Optional<ServiceUnit> serviceUnit) {
		refreshUpdatedAt();
		this.serviceUnit = wrapNull(serviceUnit);
		for (ServiceUnit presentServiceUnit : getServiceUnit().asSet()) {
			setServiceUnitId(presentServiceUnit.getId());
		}
	}

	@JsonIgnore
	public void setServiceUnit(ServiceUnit serviceUnit) {
		setServiceUnit(Optional.fromNullable(serviceUnit));
	}

	public void clearServiceUnit() {
		setServiceUnit(Optional.<ServiceUnit>absent());
	}

	@Override
	public ServiceUnitStatusLog clone() {
		return super.clone(ServiceUnitStatusLog.class);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(createdAt)
			.append(id)
			.append(latitude)
			.append(longitude)
			.append(offline)
			.append(offlineTime)
			.append(orientation)
			.append(serviceUnitId)
			.append(temperature)
			.append(updatedAt)
			.append(serviceUnit)
			.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof ServiceUnitStatusLogBase)) {
			return false;
		}
		ServiceUnitStatusLogBase other = (ServiceUnitStatusLogBase) obj;
		return new EqualsBuilder()
			.append(createdAt, other.createdAt)
			.append(id, other.id)
			.append(latitude, other.latitude)
			.append(longitude, other.longitude)
			.append(offline, other.offline)
			.append(offlineTime, other.offlineTime)
			.append(orientation, other.orientation)
			.append(serviceUnitId, other.serviceUnitId)
			.append(temperature, other.temperature)
			.append(updatedAt, other.updatedAt)
			.append(serviceUnit, other.serviceUnit)
			.isEquals();
	}
}
