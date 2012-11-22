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
	private static final long serialVersionUID = 3508981572135013205L;

	// Columns
	@JsonProperty private String address = "";
	@JsonDeserialize(using=RailsDateDeserializer.class) @JsonSerialize(using=RailsDateSerializer.class)
	@JsonProperty private Date birthday = DEFAULT_DATE;
	@JsonProperty private Optional<String> email = Optional.absent();
	@JsonProperty private Optional<String> email2 = Optional.absent();
	@JsonProperty private Optional<String> felicaId = Optional.absent();
	@JsonProperty private String firstName = "";
	@JsonProperty private String firstNameRuby = "";
	@JsonProperty private Optional<Boolean> handicapped = Optional.absent();
	@JsonProperty private Integer id = 0;
	@JsonProperty private String lastName = "";
	@JsonProperty private String lastNameRuby = "";
	@JsonProperty private String login = "";
	@JsonProperty private String memo = "";
	@JsonProperty private Optional<Boolean> neededCare = Optional.absent();
	@JsonProperty private Boolean passwordActive = false;
	@JsonProperty private Optional<Boolean> recommendNotification = Optional.absent();
	@JsonProperty private Optional<Boolean> recommendOk = Optional.absent();
	@JsonProperty private Optional<Boolean> reserveNotification = Optional.absent();
	@JsonProperty private Integer sex = 0;
	@JsonProperty private String telephoneNumber = "";
	@JsonProperty private Optional<String> telephoneNumber2 = Optional.absent();
	@JsonProperty private Integer typeOfUser = 0;
	@JsonProperty private Optional<Boolean> updateNotification = Optional.absent();
	@JsonProperty private Optional<Boolean> wheelchair = Optional.absent();
	@JsonProperty private Optional<String> zip = Optional.absent();
	@JsonProperty private Optional<String> auditComment = Optional.absent();
	@JsonProperty private Optional<String> fullname = Optional.absent();
	@JsonProperty private Optional<String> fullnameRuby = Optional.absent();
	@JsonProperty private Optional<String> password = Optional.absent();
	@JsonProperty private Optional<String> passwordConfirmation = Optional.absent();
	@JsonProperty private Optional<String> rememberMe = Optional.absent();

	// Associations
	@JsonProperty @JsonView(AssociationView.class) private List<Demand> demands = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<PassengerRecord> passengerRecords = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<Platform> platforms = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<ReservationCandidate> reservationCandidates = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<ReservationUser> reservationUsers = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<Reservation> reservations = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<Reservation> reservationsAsFellow = Lists.newLinkedList();

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
	public String getAddress() {
		return wrapNull(address);
	}

	@JsonIgnore
	public void setAddress(String address) {
		this.address = wrapNull(address);
	}

	@JsonIgnore
	public Date getBirthday() {
		return wrapNull(birthday);
	}

	@JsonIgnore
	public void setBirthday(Date birthday) {
		this.birthday = wrapNull(birthday);
	}

	@JsonIgnore
	public Optional<String> getEmail() {
		return wrapNull(email);
	}

	@JsonIgnore
	public void setEmail(Optional<String> email) {
		this.email = wrapNull(email);
	}

	@JsonIgnore
	public void setEmail(String email) {
		setEmail(Optional.fromNullable(email));
	}

	public void clearEmail() {
		setEmail(Optional.<String>absent());
	}

	@JsonIgnore
	public Optional<String> getEmail2() {
		return wrapNull(email2);
	}

	@JsonIgnore
	public void setEmail2(Optional<String> email2) {
		this.email2 = wrapNull(email2);
	}

	@JsonIgnore
	public void setEmail2(String email2) {
		setEmail2(Optional.fromNullable(email2));
	}

	public void clearEmail2() {
		setEmail2(Optional.<String>absent());
	}

	@JsonIgnore
	public Optional<String> getFelicaId() {
		return wrapNull(felicaId);
	}

	@JsonIgnore
	public void setFelicaId(Optional<String> felicaId) {
		this.felicaId = wrapNull(felicaId);
	}

	@JsonIgnore
	public void setFelicaId(String felicaId) {
		setFelicaId(Optional.fromNullable(felicaId));
	}

	public void clearFelicaId() {
		setFelicaId(Optional.<String>absent());
	}

	@JsonIgnore
	public String getFirstName() {
		return wrapNull(firstName);
	}

	@JsonIgnore
	public void setFirstName(String firstName) {
		this.firstName = wrapNull(firstName);
	}

	@JsonIgnore
	public String getFirstNameRuby() {
		return wrapNull(firstNameRuby);
	}

	@JsonIgnore
	public void setFirstNameRuby(String firstNameRuby) {
		this.firstNameRuby = wrapNull(firstNameRuby);
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
	public String getLastName() {
		return wrapNull(lastName);
	}

	@JsonIgnore
	public void setLastName(String lastName) {
		this.lastName = wrapNull(lastName);
	}

	@JsonIgnore
	public String getLastNameRuby() {
		return wrapNull(lastNameRuby);
	}

	@JsonIgnore
	public void setLastNameRuby(String lastNameRuby) {
		this.lastNameRuby = wrapNull(lastNameRuby);
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
	public Boolean getPasswordActive() {
		return wrapNull(passwordActive);
	}

	@JsonIgnore
	public void setPasswordActive(Boolean passwordActive) {
		this.passwordActive = wrapNull(passwordActive);
	}

	@JsonIgnore
	public Optional<Boolean> getRecommendNotification() {
		return wrapNull(recommendNotification);
	}

	@JsonIgnore
	public void setRecommendNotification(Optional<Boolean> recommendNotification) {
		this.recommendNotification = wrapNull(recommendNotification);
	}

	@JsonIgnore
	public void setRecommendNotification(Boolean recommendNotification) {
		setRecommendNotification(Optional.fromNullable(recommendNotification));
	}

	public void clearRecommendNotification() {
		setRecommendNotification(Optional.<Boolean>absent());
	}

	@JsonIgnore
	public Optional<Boolean> getRecommendOk() {
		return wrapNull(recommendOk);
	}

	@JsonIgnore
	public void setRecommendOk(Optional<Boolean> recommendOk) {
		this.recommendOk = wrapNull(recommendOk);
	}

	@JsonIgnore
	public void setRecommendOk(Boolean recommendOk) {
		setRecommendOk(Optional.fromNullable(recommendOk));
	}

	public void clearRecommendOk() {
		setRecommendOk(Optional.<Boolean>absent());
	}

	@JsonIgnore
	public Optional<Boolean> getReserveNotification() {
		return wrapNull(reserveNotification);
	}

	@JsonIgnore
	public void setReserveNotification(Optional<Boolean> reserveNotification) {
		this.reserveNotification = wrapNull(reserveNotification);
	}

	@JsonIgnore
	public void setReserveNotification(Boolean reserveNotification) {
		setReserveNotification(Optional.fromNullable(reserveNotification));
	}

	public void clearReserveNotification() {
		setReserveNotification(Optional.<Boolean>absent());
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
	public Optional<String> getTelephoneNumber2() {
		return wrapNull(telephoneNumber2);
	}

	@JsonIgnore
	public void setTelephoneNumber2(Optional<String> telephoneNumber2) {
		this.telephoneNumber2 = wrapNull(telephoneNumber2);
	}

	@JsonIgnore
	public void setTelephoneNumber2(String telephoneNumber2) {
		setTelephoneNumber2(Optional.fromNullable(telephoneNumber2));
	}

	public void clearTelephoneNumber2() {
		setTelephoneNumber2(Optional.<String>absent());
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
	public Optional<Boolean> getUpdateNotification() {
		return wrapNull(updateNotification);
	}

	@JsonIgnore
	public void setUpdateNotification(Optional<Boolean> updateNotification) {
		this.updateNotification = wrapNull(updateNotification);
	}

	@JsonIgnore
	public void setUpdateNotification(Boolean updateNotification) {
		setUpdateNotification(Optional.fromNullable(updateNotification));
	}

	public void clearUpdateNotification() {
		setUpdateNotification(Optional.<Boolean>absent());
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

	@JsonIgnore
	public Optional<String> getZip() {
		return wrapNull(zip);
	}

	@JsonIgnore
	public void setZip(Optional<String> zip) {
		this.zip = wrapNull(zip);
	}

	@JsonIgnore
	public void setZip(String zip) {
		setZip(Optional.fromNullable(zip));
	}

	public void clearZip() {
		setZip(Optional.<String>absent());
	}

	@JsonIgnore
	public Optional<String> getAuditComment() {
		return wrapNull(auditComment);
	}

	@JsonIgnore
	public void setAuditComment(Optional<String> auditComment) {
		this.auditComment = wrapNull(auditComment);
	}

	@JsonIgnore
	public void setAuditComment(String auditComment) {
		setAuditComment(Optional.fromNullable(auditComment));
	}

	public void clearAuditComment() {
		setAuditComment(Optional.<String>absent());
	}

	@JsonIgnore
	public Optional<String> getFullname() {
		return wrapNull(fullname);
	}

	@JsonIgnore
	public void setFullname(Optional<String> fullname) {
		this.fullname = wrapNull(fullname);
	}

	@JsonIgnore
	public void setFullname(String fullname) {
		setFullname(Optional.fromNullable(fullname));
	}

	public void clearFullname() {
		setFullname(Optional.<String>absent());
	}

	@JsonIgnore
	public Optional<String> getFullnameRuby() {
		return wrapNull(fullnameRuby);
	}

	@JsonIgnore
	public void setFullnameRuby(Optional<String> fullnameRuby) {
		this.fullnameRuby = wrapNull(fullnameRuby);
	}

	@JsonIgnore
	public void setFullnameRuby(String fullnameRuby) {
		setFullnameRuby(Optional.fromNullable(fullnameRuby));
	}

	public void clearFullnameRuby() {
		setFullnameRuby(Optional.<String>absent());
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

	@JsonIgnore
	public Optional<String> getPasswordConfirmation() {
		return wrapNull(passwordConfirmation);
	}

	@JsonIgnore
	public void setPasswordConfirmation(Optional<String> passwordConfirmation) {
		this.passwordConfirmation = wrapNull(passwordConfirmation);
	}

	@JsonIgnore
	public void setPasswordConfirmation(String passwordConfirmation) {
		setPasswordConfirmation(Optional.fromNullable(passwordConfirmation));
	}

	public void clearPasswordConfirmation() {
		setPasswordConfirmation(Optional.<String>absent());
	}

	@JsonIgnore
	public Optional<String> getRememberMe() {
		return wrapNull(rememberMe);
	}

	@JsonIgnore
	public void setRememberMe(Optional<String> rememberMe) {
		this.rememberMe = wrapNull(rememberMe);
	}

	@JsonIgnore
	public void setRememberMe(String rememberMe) {
		setRememberMe(Optional.fromNullable(rememberMe));
	}

	public void clearRememberMe() {
		setRememberMe(Optional.<String>absent());
	}

	@JsonIgnore
	public List<Demand> getDemands() {
		return wrapNull(demands);
	}

	@JsonIgnore
	public void setDemands(Iterable<Demand> demands) {
		this.demands = wrapNull(demands);
	}

	public void clearDemands() {
		setDemands(new LinkedList<Demand>());
	}

	@JsonIgnore
	public List<PassengerRecord> getPassengerRecords() {
		return wrapNull(passengerRecords);
	}

	@JsonIgnore
	public void setPassengerRecords(Iterable<PassengerRecord> passengerRecords) {
		this.passengerRecords = wrapNull(passengerRecords);
	}

	public void clearPassengerRecords() {
		setPassengerRecords(new LinkedList<PassengerRecord>());
	}

	@JsonIgnore
	public List<Platform> getPlatforms() {
		return wrapNull(platforms);
	}

	@JsonIgnore
	public void setPlatforms(Iterable<Platform> platforms) {
		this.platforms = wrapNull(platforms);
	}

	public void clearPlatforms() {
		setPlatforms(new LinkedList<Platform>());
	}

	@JsonIgnore
	public List<ReservationCandidate> getReservationCandidates() {
		return wrapNull(reservationCandidates);
	}

	@JsonIgnore
	public void setReservationCandidates(Iterable<ReservationCandidate> reservationCandidates) {
		this.reservationCandidates = wrapNull(reservationCandidates);
	}

	public void clearReservationCandidates() {
		setReservationCandidates(new LinkedList<ReservationCandidate>());
	}

	@JsonIgnore
	public List<ReservationUser> getReservationUsers() {
		return wrapNull(reservationUsers);
	}

	@JsonIgnore
	public void setReservationUsers(Iterable<ReservationUser> reservationUsers) {
		this.reservationUsers = wrapNull(reservationUsers);
	}

	public void clearReservationUsers() {
		setReservationUsers(new LinkedList<ReservationUser>());
	}

	@JsonIgnore
	public List<Reservation> getReservations() {
		return wrapNull(reservations);
	}

	@JsonIgnore
	public void setReservations(Iterable<Reservation> reservations) {
		this.reservations = wrapNull(reservations);
	}

	public void clearReservations() {
		setReservations(new LinkedList<Reservation>());
	}

	@JsonIgnore
	public List<Reservation> getReservationsAsFellow() {
		return wrapNull(reservationsAsFellow);
	}

	@JsonIgnore
	public void setReservationsAsFellow(Iterable<Reservation> reservationsAsFellow) {
		this.reservationsAsFellow = wrapNull(reservationsAsFellow);
	}

	public void clearReservationsAsFellow() {
		setReservationsAsFellow(new LinkedList<Reservation>());
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
