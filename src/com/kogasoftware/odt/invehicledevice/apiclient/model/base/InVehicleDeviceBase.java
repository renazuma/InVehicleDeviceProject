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
 * 車載器
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class InVehicleDeviceBase extends Model {
	private static final long serialVersionUID = 3407449911888804912L;

	// Columns
	@JsonProperty private Optional<String> authenticationToken = Optional.absent();
	@JsonProperty private Integer id = 0;
	@JsonProperty private String login = "";
	@JsonProperty private Optional<String> password = Optional.absent();

	// Associations

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

	@Override
	public InVehicleDevice clone() {
		return clone(true);
	}

	@Override
	public InVehicleDevice clone(Boolean withAssociation) {
		return super.clone(InVehicleDevice.class, withAssociation);
	}
}
