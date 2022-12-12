package com.kogasoftware.odt.invehicledevice.infra.contentprovider.table

import android.content.ContentResolver
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

        fun getAccountData(contentResolver: ContentResolver): Triple<String, String, String> {
            var userId: String = "";
            var password: String = "";
            var serviceId: String = "";

            val c: Cursor? = contentResolver.query(ZenrinMapsAccount.CONTENT.URI, null, null, null, null)
            val userIdIndex: Int? = c?.getColumnIndex(ZenrinMapsAccount.Columns.USER_ID)
            val passwordIndex: Int? = c?.getColumnIndex(ZenrinMapsAccount.Columns.PASSWORD)
            val serviceIdIndex: Int? = c?.getColumnIndex(ZenrinMapsAccount.Columns.SERVICE_ID)
            if (userIdIndex != null && passwordIndex != null && serviceIdIndex != null && c.moveToFirst()) {
                userId = c.getString(userIdIndex)
                password = c.getString(passwordIndex)
                serviceId = c.getString(serviceIdIndex)
            }
            c?.close()
            return Triple(userId, password, serviceId)
        }
    }
}