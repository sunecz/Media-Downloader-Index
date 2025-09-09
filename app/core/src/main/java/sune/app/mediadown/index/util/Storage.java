package sune.app.mediadown.index.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public final class Storage {
	
	private static Storage instance;
	
	private final Path directory;
	
	private Storage(Environment environment) throws IOException {
		directory = Path.of(environment.getProperty("APP_STORAGE_DIR", "temp")).toRealPath();
		Files.createDirectories(directory);
		instance = this;
	}
	
	public static final Path directory() {
		return instance.directory;
	}
}
