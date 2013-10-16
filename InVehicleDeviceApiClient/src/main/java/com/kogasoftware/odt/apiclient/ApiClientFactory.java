package com.kogasoftware.odt.apiclient;

import java.io.File;

public class ApiClientFactory {
	public static ApiClient newInstance(String serverHost,
			String authenticationToken, File backupFile) {
		return new DefaultApiClient(serverHost, authenticationToken, backupFile);
	}
}
