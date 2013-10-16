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
 * 予約とユーザの関連
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class ReservationUserBase extends Model {
	private static final long serialVersionUID = 22558928279598258L;

	// Columns
	@JsonProperty private Date createdAt = new Date();
	@JsonProperty private Integer id = 0;
	@JsonProperty private Integer likeReservationId = 0;
	@JsonProperty private String likeReservationType = "";
	@JsonProperty private Date updatedAt = new Date();
	@JsonProperty private Integer userId = 0;

	// Associations
	@JsonProperty @JsonView(AssociationView.class) private Optional<Reservation> reservation = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<User> user = Optional.absent();

	public static final String UNDERSCORE = "reservation_user";
	public static final ResponseConverter<ReservationUser> RESPONSE_CONVERTER = getResponseConverter(ReservationUser.class);
	public static final ResponseConverter<List<ReservationUser>> LIST_RESPONSE_CONVERTER = getListResponseConverter(ReservationUser.class);

	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}

	public static ReservationUser parse(String jsonString) throws IOException {
		return parse(jsonString, ReservationUser.class);
	}

	public static List<ReservationUser> parseList(String jsonString) throws IOException {
		return parseList(jsonString, ReservationUser.class);
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
	public Integer getLikeReservationId() {
		return wrapNull(likeReservationId);
	}

	@JsonIgnore
	public void setLikeReservationId(Integer likeReservationId) {
		refreshUpdatedAt();
		this.likeReservationId = wrapNull(likeReservationId);
		for (Reservation presentReservation : getReservation().asSet()) {
			presentReservation.setId(getLikeReservationId());
		}
	}

	@JsonIgnore
	public String getLikeReservationType() {
		return wrapNull(likeReservationType);
	}

	@JsonIgnore
	public void setLikeReservationType(String likeReservationType) {
		refreshUpdatedAt();
		this.likeReservationType = wrapNull(likeReservationType);
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
	public Integer getUserId() {
		return wrapNull(userId);
	}

	@JsonIgnore
	public void setUserId(Integer userId) {
		refreshUpdatedAt();
		this.userId = wrapNull(userId);
		for (User presentUser : getUser().asSet()) {
			presentUser.setId(getUserId());
		}
	}

	@JsonIgnore
	public Optional<Reservation> getReservation() {
		return wrapNull(reservation);
	}

	@JsonIgnore
	public void setReservation(Optional<Reservation> reservation) {
		refreshUpdatedAt();
		this.reservation = wrapNull(reservation);
		for (Reservation presentReservation : getReservation().asSet()) {
			setLikeReservationId(presentReservation.getId());
		}
	}

	@JsonIgnore
	public void setReservation(Reservation reservation) {
		setReservation(Optional.fromNullable(reservation));
	}

	public void clearReservation() {
		setReservation(Optional.<Reservation>absent());
	}

	@JsonIgnore
	public Optional<User> getUser() {
		return wrapNull(user);
	}

	@JsonIgnore
	public void setUser(Optional<User> user) {
		refreshUpdatedAt();
		this.user = wrapNull(user);
		for (User presentUser : getUser().asSet()) {
			setUserId(presentUser.getId());
		}
	}

	@JsonIgnore
	public void setUser(User user) {
		setUser(Optional.fromNullable(user));
	}

	public void clearUser() {
		setUser(Optional.<User>absent());
	}

	@Override
	public ReservationUser clone() {
		return clone(true);
	}

	@Override
	public ReservationUser clone(Boolean withAssociation) {
		return super.clone(ReservationUser.class, withAssociation);
	}
}
