package com.kogasoftware.odt.invehicledevice.contentprovider.json;

import android.content.ContentValues;

import com.google.common.base.Strings;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Users;

public class UserJson {
	public Long id;
	public String firstName;
	public String lastName;
	public String memo;
	public Boolean handicapped;
	public Boolean neededCare;
	public Boolean wheelchair;
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(Users.Columns._ID, id);
		values.put(Users.Columns.FIRST_NAME, firstName);
		values.put(Users.Columns.LAST_NAME, lastName);
		values.put(Users.Columns.MEMO, Strings.nullToEmpty(memo));
		values.put(Users.Columns.HANDICAPPED, handicapped);
		values.put(Users.Columns.NEEDED_CARE, neededCare);
		values.put(Users.Columns.WHEELCHAIR, wheelchair);
		return values;
	}
}
