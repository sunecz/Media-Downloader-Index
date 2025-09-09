package sune.app.mediadown.index.net;

import java.net.InetSocketAddress;
import java.net.URI;

public final class Proxies {

	public static final Web.Proxy NONE = Web.Proxy.none();
	public static final Web.Proxy CZ;
	public static final Web.Proxy SK;

	static {
		CZ = fromString(System.getenv("APP_PROXY_CZ"), "http://localhost:8001");
		SK = fromString(System.getenv("APP_PROXY_SK"), "http://localhost:8002");
	}
	
	private static final Web.Proxy fromString(String uri, String defaultUri) {
		URI obj = URI.create(uri != null ? uri : defaultUri);
		
		return Web.Proxy.of(
			obj.getScheme(),
			new InetSocketAddress(obj.getHost(), obj.getPort()),
			null
		);
	}
	
	private Proxies() {
	}
}
