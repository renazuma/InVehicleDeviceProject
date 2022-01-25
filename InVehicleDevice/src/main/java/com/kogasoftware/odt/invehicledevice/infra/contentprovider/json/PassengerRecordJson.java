package com.kogasoftware.odt.invehicledevice.infra.contentprovider.json;

import android.content.ContentValues;

import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.util.ContentValuesUtils;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;

/**
 * 乗車実績のJSON
 */
public class PassengerRecordJson {
    public Long id;
    public Long reservationId;
    public DateTime getOnTime;
    public DateTime getOffTime;
    public Long userId;
    public Long expectedCharge;
    public Long paidCharge;

    public ContentValues toContentValues(List<ReservationJson> reservations)
            throws IOException {
        for (ReservationJson reservation : reservations) {
            if (reservationId.equals(reservation.id)) {
                return toContentValues(reservation);
            }
        }
        throw new IOException("toContentValues reservation.id=" + reservationId
                + " not found");
    }

    public ContentValues toContentValues(ReservationJson reservation) {
        ContentValues values = new ContentValues();
        values.put(PassengerRecord.Columns._ID, id);
        values.put(PassengerRecord.Columns.RESERVATION_ID, reservationId);
        values.put(PassengerRecord.Columns.USER_ID, userId);
        ContentValuesUtils.putDateTime(values,
                PassengerRecord.Columns.GET_ON_TIME, getOnTime);
        ContentValuesUtils.putDateTime(values,
                PassengerRecord.Columns.GET_OFF_TIME, getOffTime);
        Boolean representative = userId.equals(reservation.userId);
        values.put(PassengerRecord.Columns.REPRESENTATIVE, representative
                ? 1
                : 0);
        Integer passengerCount;
        if (representative) {
            passengerCount = reservation.passengerCount - (reservation.fellowUsers.size() - 1);
        } else {
            passengerCount = 1;
        }

        values.put(PassengerRecord.Columns.PASSENGER_COUNT, passengerCount);
        values.put(PassengerRecord.Columns.EXPECTED_CHARGE, expectedCharge);
        values.put(PassengerRecord.Columns.PAID_CHARGE, paidCharge);
        return values;
    }
}
