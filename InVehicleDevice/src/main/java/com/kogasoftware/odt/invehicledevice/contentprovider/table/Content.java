package com.kogasoftware.odt.invehicledevice.contentprovider.table;

import android.content.UriMatcher;
import android.net.Uri;

import com.kogasoftware.odt.invehicledevice.contentprovider.InVehicleDeviceContentProvider;

public class Content {
	public final Uri URI;
	private final String name;
	private final int code;

	public Content(int code, String name) {
		this.name = name;
		this.code = code;
		URI = new Uri.Builder().scheme("content")
				.authority(InVehicleDeviceContentProvider.AUTHORITY)
				.appendPath(name).build();
	}

	public void addTo(UriMatcher matcher) {
		matcher.addURI(InVehicleDeviceContentProvider.AUTHORITY, name, code);
	}
}
