package com.kogasoftware.odt.invehicledevice.service.statussenderservice;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.util.Log;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ServiceUnitStatusLog;

/**
 * 電波強度を受け取り、現在の電波状況を100分率で表した数値をサービスへ通知
 */
public class SignalStrengthListener extends PhoneStateListener {
    private static final String TAG = SignalStrengthListener.class
            .getSimpleName();
    private final ContentResolver contentResolver;

    public SignalStrengthListener(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    /**
     * SignalStrengthを受け取り、現在の電波状況を100分率で表した数値を取得。数値取得に失敗した場合Optional.absent()
     * を返す
     */
    public static Optional<Integer> convertSignalStrengthToPercentage(
            SignalStrength signalStrength) {
        // GSMでない場合は失敗
        if (!signalStrength.isGsm()) {
            return Optional.absent();
        }

        // GSMの場合は値を100分率に置き換え
        Integer gsmSignalStrength = signalStrength.getGsmSignalStrength();
        if (gsmSignalStrength.equals(99) || gsmSignalStrength <= 2) {
            return Optional.of(0);
        } else if (gsmSignalStrength <= 4) {
            return Optional.of(25);
        } else if (gsmSignalStrength <= 7) {
            return Optional.of(50);
        } else if (gsmSignalStrength <= 11) {
            return Optional.of(75);
        }
        return Optional.of(100);
    }

    /**
     * SignalStrengthを受け取り、現在の電波状況を100分率で表した数値への変換に成功した場合サービスに通知
     */
    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        String message = "onSignalStrengthChanged" + " isGsm=" + signalStrength.isGsm() +
                " cdmaDbm=" + signalStrength.getCdmaDbm() +
                " cdmaEcio=" + signalStrength.getCdmaEcio() +
                " evdoDbm=" + signalStrength.getEvdoDbm() +
                " evdoEcio=" + signalStrength.getEvdoEcio() +
                " evdoSnr=" + signalStrength.getEvdoSnr() +
                " gsmBitErrorRate="
                + signalStrength.getGsmBitErrorRate() +
                " gsmSignalStrength="
                + signalStrength.getGsmSignalStrength() +
                " describeContents=" + signalStrength.describeContents();
        Log.i(TAG, message);
        for (Integer percentage : convertSignalStrengthToPercentage(
                signalStrength).asSet()) {
            final ContentValues values = new ContentValues();
            values.put(ServiceUnitStatusLog.Columns.SIGNAL_STRENGTH,
                    percentage);
            new Thread() {
                @Override
                public void run() {
                    contentResolver.update(ServiceUnitStatusLog.CONTENT.URI,
                            values, null, null);
                }
            }.start();
        }
    }

}
