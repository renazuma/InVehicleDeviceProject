package com.kogasoftware.odt.webapi;

public class CacheMissException extends WebAPIException {
	public CacheMissException(WebAPI.CacheKey cacheKey) {
		super(false, cacheKey + "not found");
	}

	private static final long serialVersionUID = -2799641981584351325L;
}
