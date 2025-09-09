package sune.app.mediadown.index.net;

import java.net.URI;
import java.nio.charset.Charset;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import sune.app.mediadown.index.Shared;
import sune.app.mediadown.index.net.Web.Request;
import sune.app.mediadown.index.net.Web.Response;

public final class HTML {
	
	private static final Charset CHARSET = Shared.CHARSET;
	
	// Forbid anyone to create an instance of this class
	private HTML() {
	}
	
	private static String jsoupBaseUri(URI baseUri) {
		return baseUri == null ? "" : baseUri.normalize().toString();
	}
	
	public static Document from(URI uri) throws Exception {
		return from(Request.of(uri).GET());
	}
	
	public static Document from(Request request) throws Exception {
		try(Response.OfStream response = Web.requestStream(request)) {
			return from(response);
		}
	}
	
	public static Document from(Response.OfStream response) throws Exception {
		return Jsoup.parse(response.stream(), CHARSET.name(), jsoupBaseUri(response.uri()));
	}
	
	public static Document parse(String content) {
		return parse(content, null);
	}
	
	public static Document parse(String content, URI baseUri) {
		return Jsoup.parse(content, jsoupBaseUri(baseUri));
	}
}