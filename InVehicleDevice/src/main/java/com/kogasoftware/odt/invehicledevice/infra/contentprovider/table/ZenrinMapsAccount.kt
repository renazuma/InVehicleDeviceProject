package com.kogasoftware.odt.invehicledevice.infra.contentprovider.table

import android.database.Cursor
import android.provider.BaseColumns
import com.kogasoftware.android.CursorReader
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.InVehicleDeviceContentProvider

/**
 * 住宅地図APIアカウントテーブル
 */
class ZenrinMapsAccount(cursor: Cursor?) {
    object Columns : BaseColumns {
        const val _ID = "_id"
        const val USER_ID = "user_id"
        const val PASSWORD = "password"
        const val SERVICE_ID = "service_id"
    }

    val id: Long
    val userId: String
    val password: String
    val serviceId: String

    init {
        val reader = CursorReader(cursor)
        id = reader.readLong(Columns._ID)
        userId = reader.readString(Columns.USER_ID)
        password = reader.readString(Columns.PASSWORD)
        serviceId = reader.readString(Columns.SERVICE_ID)
    }

    companion object {
        const val TABLE_CODE = 12
        const val TABLE_NAME = "zenrin_maps_accounts"
        @JvmField
        val CONTENT = Content(TABLE_CODE, TABLE_NAME)
        @JvmStatic
        fun query(contentProvider: InVehicleDeviceContentProvider, projection: Array<String?>?, selection: String?, selectionArgs: Array<String?>?, sortOrder: String?): Cursor {
            val cursor = contentProvider.database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            cursor.setNotificationUri(contentProvider.context!!.contentResolver, CONTENT.URI)
            return cursor
        }
    }
}