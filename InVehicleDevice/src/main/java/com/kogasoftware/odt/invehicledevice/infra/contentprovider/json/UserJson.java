package com.kogasoftware.odt.invehicledevice.infra.contentprovider.json;

import android.content.ContentValues;

import com.google.common.base.Strings;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.User;

/**
 * ユーザーのJSON
 */
public class UserJson {
    public Long id;
    public String firstName;
    public String lastName;
    public String memo;
    public Boolean handicapped;
    public Boolean neededCare;
    public Boolean wheelchair;
    public Boolean licenseReturned;

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(User.Columns._ID, id);
        values.put(User.Columns.FIRST_NAME, firstName);
        values.put(User.Columns.LAST_NAME, lastName);
        values.put(User.Columns.MEMO, Strings.nullToEmpty(memo));
        values.put(User.Columns.HANDICAPPED, handicapped);
        values.put(User.Columns.NEEDED_CARE, neededCare);
        values.put(User.Columns.WHEELCHAIR, wheelchair);
        values.put(User.Columns.LICENSE_RETURNED, licenseReturned);
        return values;
    }
}
