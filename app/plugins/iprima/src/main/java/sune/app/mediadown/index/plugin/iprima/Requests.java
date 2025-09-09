package sune.app.mediadown.index.plugin.iprima;

import java.net.URI;

import sune.app.mediadown.index.net.Proxies;
import sune.app.mediadown.index.net.Web;

/**
 * Helper class to create Requests without the need to always
 * specify a Proxy that the Website requires.
 */
public final class Requests {

	private static final Web.Proxy PROXY = Proxies.CZ;

	private Requests() {
	}

	public static Web.Request.Builder of(URI uri) {
		return Web.Request.of(uri).proxy(PROXY);
	}
}