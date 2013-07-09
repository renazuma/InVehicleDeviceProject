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
 * 乗降場
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class PlatformBase extends Model {
	private static final long serialVersionUID = 1851324914633166932L;

	// Columns
	@JsonProperty private Integer id = 0;
	@JsonProperty private BigDecimal latitude = BigDecimal.ZERO;
	@JsonProperty private BigDecimal longitude = BigDecimal.ZERO;
	@JsonProperty private String memo = "";
	@JsonProperty private String name = "";
	@JsonProperty private String nameRuby = "";

	// Associations

	public static final String UNDERSCORE = "platform";
	public static final ResponseConverter<Platform> RESPONSE_CONVERTER = getResponseConverter(Platform.class);
	public static final ResponseConverter<List<Platform>> LIST_RESPONSE_CONVERTER = getListResponseConverter(Platform.class);

	public static Platform parse(String jsonString) throws IOException {
		return parse(jsonString, Platform.class);
	}

	public static List<Platform> parseList(String jsonString) throws IOException {
		return parseList(jsonString, Platform.class);
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
	public String getMemo() {
		return wrapNull(memo);
	}

	@JsonIgnore
	public void setMemo(String memo) {
		this.memo = wrapNull(memo);
	}

	@JsonIgnore
	public String getName() {
		return wrapNull(name);
	}

	@JsonIgnore
	public void setName(String name) {
		this.name = wrapNull(name);
	}

	@JsonIgnore
	public String getNameRuby() {
		return wrapNull(nameRuby);
	}

	@JsonIgnore
	public void setNameRuby(String nameRuby) {
		this.nameRuby = wrapNull(nameRuby);
	}

	@Override
	public Platform clone() {
		return clone(true);
	}

	@Override
	public Platform clone(Boolean withAssociation) {
		return super.clone(Platform.class, withAssociation);
	}
}
