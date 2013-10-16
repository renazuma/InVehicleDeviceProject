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
 * 号車ログ
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class ServiceUnitStatusLogBase extends Model {
	private static final long serialVersionUID = 5520827379476516989L;

	// Columns
	@JsonProperty private Integer id = 0;
	@JsonProperty private BigDecimal latitude = BigDecimal.ZERO;
	@JsonProperty private BigDecimal longitude = BigDecimal.ZERO;
	@JsonProperty private Optional<Boolean> offline = Optional.absent();
	@JsonProperty private Optional<Date> offlineTime = Optional.absent();
	@JsonProperty private Optional<Integer> orientation = Optional.absent();
	@JsonProperty private Optional<Integer> temperature = Optional.absent();

	// Associations

	public static final String UNDERSCORE = "service_unit_status_log";
	public static final ResponseConverter<ServiceUnitStatusLog> RESPONSE_CONVERTER = getResponseConverter(ServiceUnitStatusLog.class);
	public static final ResponseConverter<List<ServiceUnitStatusLog>> LIST_RESPONSE_CONVERTER = getListResponseConverter(ServiceUnitStatusLog.class);

	public static ServiceUnitStatusLog parse(String jsonString) throws IOException {
		return parse(jsonString, ServiceUnitStatusLog.class);
	}

	public static List<ServiceUnitStatusLog> parseList(String jsonString) throws IOException {
		return parseList(jsonString, ServiceUnitStatusLog.class);
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
	public BigDecimal getLatitude() {
		return wrapNull(latitude);
	}

	@JsonIgnore
	public void setLatitude(BigDecimal latitude) {
		this.latitude = wrapNull(latitude);
	}

	@JsonIgnore
	public BigDecimal getLongitude() {
		return wrapNull(longitude);
	}

	@JsonIgnore
	public void setLongitude(BigDecimal longitude) {
		this.longitude = wrapNull(longitude);
	}

	@JsonIgnore
	public Optional<Boolean> getOffline() {
		return wrapNull(offline);
	}

	@JsonIgnore
	public void setOffline(Optional<Boolean> offline) {
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
	public Optional<Integer> getTemperature() {
		return wrapNull(temperature);
	}

	@JsonIgnore
	public void setTemperature(Optional<Integer> temperature) {
		this.temperature = wrapNull(temperature);
	}

	@JsonIgnore
	public void setTemperature(Integer temperature) {
		setTemperature(Optional.fromNullable(temperature));
	}

	public void clearTemperature() {
		setTemperature(Optional.<Integer>absent());
	}

	@Override
	public ServiceUnitStatusLog clone() {
		return clone(true);
	}

	@Override
	public ServiceUnitStatusLog clone(Boolean withAssociation) {
		return super.clone(ServiceUnitStatusLog.class, withAssociation);
	}
}
