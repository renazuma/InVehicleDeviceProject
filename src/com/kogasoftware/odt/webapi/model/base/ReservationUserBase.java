package com.kogasoftware.odt.webapi.model.base;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;
import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPI.ResponseConverter;
import com.kogasoftware.odt.webapi.model.*;

@SuppressWarnings("unused")
public abstract class ReservationUserBase extends Model {
	private static final long serialVersionUID = 4919514987534104473L;
	public static final ResponseConverter<ReservationUser> RESPONSE_CONVERTER = new ResponseConverter<ReservationUser>() {
		@Override
		public ReservationUser convert(byte[] rawResponse) throws JSONException {
			return parse(WebAPI.parseJSONObject(rawResponse));
		}
	};
	public static final ResponseConverter<List<ReservationUser>> LIST_RESPONSE_CONVERTER = new ResponseConverter<List<ReservationUser>>() {
		@Override
		public List<ReservationUser> convert(byte[] rawResponse) throws JSONException {
			return parseList(WebAPI.parseJSONArray(rawResponse));
		}
	};
	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}
	@Override
	public void fill(JSONObject jsonObject) throws JSONException {
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setId(parseInteger(jsonObject, "id"));
		setLikeReservationId(parseInteger(jsonObject, "like_reservation_id"));
		setLikeReservationType(parseString(jsonObject, "like_reservation_type"));
		setUserId(parseInteger(jsonObject, "user_id"));
		setReservation(Reservation.parse(jsonObject, "reservation"));
		setUser(User.parse(jsonObject, "user"));

		setUpdatedAt(parseDate(jsonObject, "updated_at"));
	}

	public static Optional<ReservationUser> parse(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return Optional.of(parse(jsonObject.getJSONObject(key)));
	}

	public static ReservationUser parse(JSONObject jsonObject) throws JSONException {
		ReservationUser model = new ReservationUser();
		model.fill(jsonObject);
		return model;
	}

	public static LinkedList<ReservationUser> parseList(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return new LinkedList<ReservationUser>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<ReservationUser> parseList(JSONArray jsonArray) throws JSONException {
		LinkedList<ReservationUser> models = new LinkedList<ReservationUser>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(parse(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	@Override
	protected JSONObject toJSONObject(Boolean recursive, Integer depth) throws JSONException {
		if (depth > MAX_RECURSE_DEPTH) {
			return new JSONObject();
		}
		Integer nextDepth = depth + 1;
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("like_reservation_id", toJSON(getLikeReservationId()));
		jsonObject.put("like_reservation_type", toJSON(getLikeReservationType()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("user_id", toJSON(getUserId()));
		if (getReservation().isPresent()) {
			if (recursive) {
				jsonObject.put("reservation", getReservation().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("like_reservation_id", toJSON(getReservation().get().getId()));
			}
		}
		if (getUser().isPresent()) {
			if (recursive) {
				jsonObject.put("user", getUser().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("user_id", toJSON(getUser().get().getId()));
			}
		}
		return jsonObject;
	}

	@Override
	public ReservationUser cloneByJSON() throws JSONException {
		return parse(toJSONObject(true));
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		refreshUpdatedAt();
		this.createdAt = wrapNull(createdAt);
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		refreshUpdatedAt();
		this.id = wrapNull(id);
	}

	private Integer likeReservationId = 0;

	public Integer getLikeReservationId() {
		return wrapNull(likeReservationId);
	}

	public void setLikeReservationId(Integer likeReservationId) {
		refreshUpdatedAt();
		this.likeReservationId = wrapNull(likeReservationId);
	}

	private String likeReservationType = "";

	public String getLikeReservationType() {
		return wrapNull(likeReservationType);
	}

	public void setLikeReservationType(String likeReservationType) {
		refreshUpdatedAt();
		this.likeReservationType = wrapNull(likeReservationType);
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Integer userId = 0;

	public Integer getUserId() {
		return wrapNull(userId);
	}

	public void setUserId(Integer userId) {
		refreshUpdatedAt();
		this.userId = wrapNull(userId);
	}

	private Optional<Reservation> reservation = Optional.<Reservation>absent();

	public Optional<Reservation> getReservation() {
		return wrapNull(reservation);
	}

	public void setReservation(Optional<Reservation> reservation) {
		this.reservation = wrapNull(reservation);
	}

	public void setReservation(Reservation reservation) {
		setReservation(Optional.fromNullable(reservation));
	}

	public void clearReservation() {
		setReservation(Optional.<Reservation>absent());
	}

	private Optional<User> user = Optional.<User>absent();

	public Optional<User> getUser() {
		return wrapNull(user);
	}

	public void setUser(Optional<User> user) {
		this.user = wrapNull(user);
	}

	public void setUser(User user) {
		setUser(Optional.fromNullable(user));
	}

	public void clearUser() {
		setUser(Optional.<User>absent());
	}
}
