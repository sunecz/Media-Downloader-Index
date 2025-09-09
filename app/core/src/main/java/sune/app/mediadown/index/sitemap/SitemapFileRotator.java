package sune.app.mediadown.index.sitemap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

public final class SitemapFileRotator implements AutoCloseable {
	
	private final Path directory;
	private final String name;
	
	private Path previousFile;
	private Path currentFile;
	
	private boolean doRotate;
	
	public SitemapFileRotator(Path directory, String name) {
		this.directory = Objects.requireNonNull(directory);
		this.name = Objects.requireNonNull(name);
		this.previousFile = directory.resolve(String.format("%s.xml", name)).toAbsolutePath();
		this.currentFile = generateFilePath();
	}
	
	private final Path generateFilePath() {
		Path file;
		
		do {
			file = directory.resolve(String.format("%s-%s.xml", name, UUID.randomUUID()));
		} while(Files.exists(file));
		
		return file.toAbsolutePath();
	}
	
	public void rotate() throws IOException {
		doRotate = true;
	}
	
	@Override
	public void close() throws IOException {
		if(!doRotate) {
			return;
		}
		
		Files.move(currentFile, previousFile, StandardCopyOption.REPLACE_EXISTING);
	}
	
	public Path previousFile() {
		return previousFile;
	}
	
	public Path currentFile() {
		return currentFile;
	}
}
