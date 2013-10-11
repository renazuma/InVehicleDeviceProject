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
 * 車載器への通知
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class VehicleNotificationBase extends Model {
	private static final long serialVersionUID = 4068889257298120496L;

	// Columns
	@JsonProperty private String body = "";
	@JsonProperty private Optional<String> bodyRuby = Optional.absent();
	@JsonProperty private Integer id = 0;
	@JsonProperty private Integer notificationKind = 0;
	@JsonProperty private Optional<Boolean> offline = Optional.absent();
	@JsonProperty private Optional<Date> readAt = Optional.absent();
	@JsonProperty private Optional<Integer> response = Optional.absent();

	// Associations

	public static final String UNDERSCORE = "vehicle_notification";
	public static final ResponseConverter<VehicleNotification> RESPONSE_CONVERTER = getResponseConverter(VehicleNotification.class);
	public static final ResponseConverter<List<VehicleNotification>> LIST_RESPONSE_CONVERTER = getListResponseConverter(VehicleNotification.class);

	public static VehicleNotification parse(String jsonString) throws IOException {
		return parse(jsonString, VehicleNotification.class);
	}

	public static List<VehicleNotification> parseList(String jsonString) throws IOException {
		return parseList(jsonString, VehicleNotification.class);
	}

	@JsonIgnore
	public String getBody() {
		return wrapNull(body);
	}

	@JsonIgnore
	public void setBody(String body) {
		this.body = wrapNull(body);
	}

	@JsonIgnore
	public Optional<String> getBodyRuby() {
		return wrapNull(bodyRuby);
	}

	@JsonIgnore
	public void setBodyRuby(Optional<String> bodyRuby) {
		this.bodyRuby = wrapNull(bodyRuby);
	}

	@JsonIgnore
	public void setBodyRuby(String bodyRuby) {
		setBodyRuby(Optional.fromNullable(bodyRuby));
	}

	public void clearBodyRuby() {
		setBodyRuby(Optional.<String>absent());
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
	public Integer getNotificationKind() {
		return wrapNull(notificationKind);
	}

	@JsonIgnore
	public void setNotificationKind(Integer notificationKind) {
		this.notificationKind = wrapNull(notificationKind);
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
	public Optional<Date> getReadAt() {
		return wrapNull(readAt);
	}

	@JsonIgnore
	public void setReadAt(Optional<Date> readAt) {
		this.readAt = wrapNull(readAt);
	}

	@JsonIgnore
	public void setReadAt(Date readAt) {
		setReadAt(Optional.fromNullable(readAt));
	}

	public void clearReadAt() {
		setReadAt(Optional.<Date>absent());
	}

	@JsonIgnore
	public Optional<Integer> getResponse() {
		return wrapNull(response);
	}

	@JsonIgnore
	public void setResponse(Optional<Integer> response) {
		this.response = wrapNull(response);
	}

	@JsonIgnore
	public void setResponse(Integer response) {
		setResponse(Optional.fromNullable(response));
	}

	public void clearResponse() {
		setResponse(Optional.<Integer>absent());
	}

	@Override
	public VehicleNotification clone() {
		return clone(true);
	}

	@Override
	public VehicleNotification clone(Boolean withAssociation) {
		return super.clone(VehicleNotification.class, withAssociation);
	}
}
