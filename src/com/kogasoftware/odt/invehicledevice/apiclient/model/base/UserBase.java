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
 * ユーザ
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class UserBase extends Model {
	private static final long serialVersionUID = 5259803993174422990L;

	// Columns
	@JsonDeserialize(using=RailsOptionalDateDeserializer.class) @JsonSerialize(using=RailsOptionalDateSerializer.class)
	@JsonProperty private Optional<Date> birthday = Optional.absent();
	@JsonProperty private Optional<String> firstName = Optional.absent();
	@JsonProperty private Optional<Boolean> handicapped = Optional.absent();
	@JsonProperty private Integer id = 0;
	@JsonProperty private Optional<String> lastName = Optional.absent();
	@JsonProperty private String memo = "";
	@JsonProperty private Optional<Boolean> neededCare = Optional.absent();
	@JsonProperty private Integer sex = 0;
	@JsonProperty private String telephoneNumber = "";
	@JsonProperty private Integer typeOfUser = 0;
	@JsonProperty private Optional<Boolean> wheelchair = Optional.absent();

	// Associations

	public static final String UNDERSCORE = "user";
	public static final ResponseConverter<User> RESPONSE_CONVERTER = getResponseConverter(User.class);
	public static final ResponseConverter<List<User>> LIST_RESPONSE_CONVERTER = getListResponseConverter(User.class);

	public static User parse(String jsonString) throws IOException {
		return parse(jsonString, User.class);
	}

	public static List<User> parseList(String jsonString) throws IOException {
		return parseList(jsonString, User.class);
	}

	@JsonIgnore
	public Optional<Date> getBirthday() {
		return wrapNull(birthday);
	}

	@JsonIgnore
	public void setBirthday(Optional<Date> birthday) {
		this.birthday = wrapNull(birthday);
	}

	@JsonIgnore
	public void setBirthday(Date birthday) {
		setBirthday(Optional.fromNullable(birthday));
	}

	public void clearBirthday() {
		setBirthday(Optional.<Date>absent());
	}

	@JsonIgnore
	public Optional<String> getFirstName() {
		return wrapNull(firstName);
	}

	@JsonIgnore
	public void setFirstName(Optional<String> firstName) {
		this.firstName = wrapNull(firstName);
	}

	@JsonIgnore
	public void setFirstName(String firstName) {
		setFirstName(Optional.fromNullable(firstName));
	}

	public void clearFirstName() {
		setFirstName(Optional.<String>absent());
	}

	@JsonIgnore
	public Optional<Boolean> getHandicapped() {
		return wrapNull(handicapped);
	}

	@JsonIgnore
	public void setHandicapped(Optional<Boolean> handicapped) {
		this.handicapped = wrapNull(handicapped);
	}

	@JsonIgnore
	public void setHandicapped(Boolean handicapped) {
		setHandicapped(Optional.fromNullable(handicapped));
	}

	public void clearHandicapped() {
		setHandicapped(Optional.<Boolean>absent());
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
	public Optional<String> getLastName() {
		return wrapNull(lastName);
	}

	@JsonIgnore
	public void setLastName(Optional<String> lastName) {
		this.lastName = wrapNull(lastName);
	}

	@JsonIgnore
	public void setLastName(String lastName) {
		setLastName(Optional.fromNullable(lastName));
	}

	public void clearLastName() {
		setLastName(Optional.<String>absent());
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
	public Optional<Boolean> getNeededCare() {
		return wrapNull(neededCare);
	}

	@JsonIgnore
	public void setNeededCare(Optional<Boolean> neededCare) {
		this.neededCare = wrapNull(neededCare);
	}

	@JsonIgnore
	public void setNeededCare(Boolean neededCare) {
		setNeededCare(Optional.fromNullable(neededCare));
	}

	public void clearNeededCare() {
		setNeededCare(Optional.<Boolean>absent());
	}

	@JsonIgnore
	public Integer getSex() {
		return wrapNull(sex);
	}

	@JsonIgnore
	public void setSex(Integer sex) {
		this.sex = wrapNull(sex);
	}

	@JsonIgnore
	public String getTelephoneNumber() {
		return wrapNull(telephoneNumber);
	}

	@JsonIgnore
	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = wrapNull(telephoneNumber);
	}

	@JsonIgnore
	public Integer getTypeOfUser() {
		return wrapNull(typeOfUser);
	}

	@JsonIgnore
	public void setTypeOfUser(Integer typeOfUser) {
		this.typeOfUser = wrapNull(typeOfUser);
	}

	@JsonIgnore
	public Optional<Boolean> getWheelchair() {
		return wrapNull(wheelchair);
	}

	@JsonIgnore
	public void setWheelchair(Optional<Boolean> wheelchair) {
		this.wheelchair = wrapNull(wheelchair);
	}

	@JsonIgnore
	public void setWheelchair(Boolean wheelchair) {
		setWheelchair(Optional.fromNullable(wheelchair));
	}

	public void clearWheelchair() {
		setWheelchair(Optional.<Boolean>absent());
	}

	@Override
	public User clone() {
		return clone(true);
	}

	@Override
	public User clone(Boolean withAssociation) {
		return super.clone(User.class, withAssociation);
	}
}
