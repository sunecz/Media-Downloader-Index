package sune.app.mediadown.index;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class Shared {
	
	public static final String  USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
	public static final Charset CHARSET    = StandardCharsets.UTF_8;
	
	// Forbid anyone to create an instance of this class
	private Shared() {
	}
}