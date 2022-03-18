package com.kogasoftware.odt.invehicledevice.infra.contentprovider.table;

import android.provider.BaseColumns;

/**
 * 予約テーブル
 */
public class Reservation {
    public static final int TABLE_CODE = 6;
    public static final String TABLE_NAME = "reservations";
    public static final Content CONTENT = new Content(TABLE_CODE, TABLE_NAME);

    public static class Columns implements BaseColumns {
        public static final String USER_ID = "user_id";
        public static final String MEMO = "memo";
        public static final String DEPARTURE_SCHEDULE_ID = "departure_schedule_id";
        public static final String ARRIVAL_SCHEDULE_ID = "arrival_schedule_id";
        public static final String SETTLED  = "settled";
    }
}
