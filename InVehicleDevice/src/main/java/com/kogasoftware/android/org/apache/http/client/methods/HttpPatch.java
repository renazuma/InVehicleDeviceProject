package com.kogasoftware.android.org.apache.http.client.methods;

import java.net.URI;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
 * HTTP PATCH method.
 * <p>
 * The HTTP PATCH method is defined in <a
 * href="http://tools.ietf.org/html/rfc5789">RF5789</a>: <blockquote> The PATCH
 * method requests that a set of changes described in the request entity be
 * applied to the resource identified by the Request- URI. Differs from the PUT
 * method in the way the server processes the enclosed entity to modify the
 * resource identified by the Request-URI. In a PUT request, the enclosed entity
 * origin server, and the client is requesting that the stored version be
 * replaced. With PATCH, however, the enclosed entity contains a set of
 * instructions describing how a resource currently residing on the origin
 * server should be modified to produce a new version. </blockquote>
 * </p>
 * 
 * @since 4.2
 * @see "http://stackoverflow.com/questions/12207373/http-patch-request-from-android"
 */
@NotThreadSafe
public class HttpPatch extends HttpEntityEnclosingRequestBase {
	public final static String METHOD_NAME = "PATCH";

	public HttpPatch() {
		super();
	}

	public HttpPatch(final URI uri) {
		super();
		setURI(uri);
	}

	public HttpPatch(final String uri) {
		super();
		setURI(URI.create(uri));
	}

	@Override
	public String getMethod() {
		return METHOD_NAME;
	}
}
