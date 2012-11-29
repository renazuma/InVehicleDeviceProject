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
 * 運行記録
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class OperationRecordBase extends Model {
	private static final long serialVersionUID = 6595288813113302312L;

	// Columns
	@JsonProperty private Optional<Date> arrivedAt = Optional.absent();
	@JsonProperty private Optional<Boolean> arrivedAtOffline = Optional.absent();
	@JsonProperty private Optional<Date> departedAt = Optional.absent();
	@JsonProperty private Optional<Boolean> departedAtOffline = Optional.absent();
	@JsonProperty private Integer id = 0;
	@JsonProperty private Date updatedAt = new Date();

	// Associations

	public static final String UNDERSCORE = "operation_record";
	public static final ResponseConverter<OperationRecord> RESPONSE_CONVERTER = getResponseConverter(OperationRecord.class);
	public static final ResponseConverter<List<OperationRecord>> LIST_RESPONSE_CONVERTER = getListResponseConverter(OperationRecord.class);

	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}

	public static OperationRecord parse(String jsonString) throws IOException {
		return parse(jsonString, OperationRecord.class);
	}

	public static List<OperationRecord> parseList(String jsonString) throws IOException {
		return parseList(jsonString, OperationRecord.class);
	}

	@JsonIgnore
	public Optional<Date> getArrivedAt() {
		return wrapNull(arrivedAt);
	}

	@JsonIgnore
	public void setArrivedAt(Optional<Date> arrivedAt) {
		refreshUpdatedAt();
		this.arrivedAt = wrapNull(arrivedAt);
	}

	@JsonIgnore
	public void setArrivedAt(Date arrivedAt) {
		setArrivedAt(Optional.fromNullable(arrivedAt));
	}

	public void clearArrivedAt() {
		setArrivedAt(Optional.<Date>absent());
	}

	@JsonIgnore
	public Optional<Boolean> getArrivedAtOffline() {
		return wrapNull(arrivedAtOffline);
	}

	@JsonIgnore
	public void setArrivedAtOffline(Optional<Boolean> arrivedAtOffline) {
		refreshUpdatedAt();
		this.arrivedAtOffline = wrapNull(arrivedAtOffline);
	}

	@JsonIgnore
	public void setArrivedAtOffline(Boolean arrivedAtOffline) {
		setArrivedAtOffline(Optional.fromNullable(arrivedAtOffline));
	}

	public void clearArrivedAtOffline() {
		setArrivedAtOffline(Optional.<Boolean>absent());
	}

	@JsonIgnore
	public Optional<Date> getDepartedAt() {
		return wrapNull(departedAt);
	}

	@JsonIgnore
	public void setDepartedAt(Optional<Date> departedAt) {
		refreshUpdatedAt();
		this.departedAt = wrapNull(departedAt);
	}

	@JsonIgnore
	public void setDepartedAt(Date departedAt) {
		setDepartedAt(Optional.fromNullable(departedAt));
	}

	public void clearDepartedAt() {
		setDepartedAt(Optional.<Date>absent());
	}

	@JsonIgnore
	public Optional<Boolean> getDepartedAtOffline() {
		return wrapNull(departedAtOffline);
	}

	@JsonIgnore
	public void setDepartedAtOffline(Optional<Boolean> departedAtOffline) {
		refreshUpdatedAt();
		this.departedAtOffline = wrapNull(departedAtOffline);
	}

	@JsonIgnore
	public void setDepartedAtOffline(Boolean departedAtOffline) {
		setDepartedAtOffline(Optional.fromNullable(departedAtOffline));
	}

	public void clearDepartedAtOffline() {
		setDepartedAtOffline(Optional.<Boolean>absent());
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
	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	@JsonIgnore
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	@Override
	public OperationRecord clone() {
		return clone(true);
	}

	@Override
	public OperationRecord clone(Boolean withAssociation) {
		return super.clone(OperationRecord.class, withAssociation);
	}
}
