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
 * 自治体
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class ServiceProviderBase extends Model {
	private static final long serialVersionUID = 4750839812220644136L;

	// Columns
	@JsonProperty private Integer id = 0;
	@JsonProperty private Optional<String> logAccessKeyIdAws = Optional.absent();
	@JsonProperty private Optional<String> logSecretAccessKeyAws = Optional.absent();

	// Associations

	public static final String UNDERSCORE = "service_provider";
	public static final ResponseConverter<ServiceProvider> RESPONSE_CONVERTER = getResponseConverter(ServiceProvider.class);
	public static final ResponseConverter<List<ServiceProvider>> LIST_RESPONSE_CONVERTER = getListResponseConverter(ServiceProvider.class);

	public static ServiceProvider parse(String jsonString) throws IOException {
		return parse(jsonString, ServiceProvider.class);
	}

	public static List<ServiceProvider> parseList(String jsonString) throws IOException {
		return parseList(jsonString, ServiceProvider.class);
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
	public Optional<String> getLogAccessKeyIdAws() {
		return wrapNull(logAccessKeyIdAws);
	}

	@JsonIgnore
	public void setLogAccessKeyIdAws(Optional<String> logAccessKeyIdAws) {
		this.logAccessKeyIdAws = wrapNull(logAccessKeyIdAws);
	}

	@JsonIgnore
	public void setLogAccessKeyIdAws(String logAccessKeyIdAws) {
		setLogAccessKeyIdAws(Optional.fromNullable(logAccessKeyIdAws));
	}

	public void clearLogAccessKeyIdAws() {
		setLogAccessKeyIdAws(Optional.<String>absent());
	}

	@JsonIgnore
	public Optional<String> getLogSecretAccessKeyAws() {
		return wrapNull(logSecretAccessKeyAws);
	}

	@JsonIgnore
	public void setLogSecretAccessKeyAws(Optional<String> logSecretAccessKeyAws) {
		this.logSecretAccessKeyAws = wrapNull(logSecretAccessKeyAws);
	}

	@JsonIgnore
	public void setLogSecretAccessKeyAws(String logSecretAccessKeyAws) {
		setLogSecretAccessKeyAws(Optional.fromNullable(logSecretAccessKeyAws));
	}

	public void clearLogSecretAccessKeyAws() {
		setLogSecretAccessKeyAws(Optional.<String>absent());
	}

	@Override
	public ServiceProvider clone() {
		return clone(true);
	}

	@Override
	public ServiceProvider clone(Boolean withAssociation) {
		return super.clone(ServiceProvider.class, withAssociation);
	}
}
