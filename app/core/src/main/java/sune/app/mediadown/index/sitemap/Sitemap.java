package sune.app.mediadown.index.sitemap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import sune.app.mediadown.index.Shared;

public final class Sitemap implements Iterable<SitemapUrlEntry> {
	
	private static final OpenOption[] OPTS_WRITE = {
		StandardOpenOption.WRITE,
		StandardOpenOption.CREATE,
	};
	
	private static final OpenOption[] OPTS_READ = {
		StandardOpenOption.READ,
	};
	
	private final Path path;
	private Map<String, SitemapUrlEntry> entries;
	
	private Sitemap(Path path) {
		this.path = Objects.requireNonNull(path);
	}
	
	private static final ByteBuffer emptyContentBytes() {
		String content = ""
			+ "<urlset xmlns=\"https://www.sitemaps.org/schemas/sitemap/0.9\">"
			+ "</urlset>";
		return ByteBuffer.wrap(content.getBytes(Shared.CHARSET));
	}
	
	public static final Sitemap of(InputStream stream) throws IOException {
		return of(stream, Files.createTempFile("sitemap_", ".xml"));
	}
	
	public static final Sitemap of(Path path) throws IOException {
		Objects.requireNonNull(path);
		
		if(!Files.exists(path)) {
			try(FileChannel fch = FileChannel.open(path, OPTS_WRITE)) {
				fch.write(emptyContentBytes());
			}
		}
		
		return new Sitemap(path);
	}
	
	public static final Sitemap of(InputStream stream, Path path) throws IOException {
		try(FileChannel fch = FileChannel.open(path, OPTS_WRITE)) {
			if(stream != null) {
				try(ReadableByteChannel rbc = Channels.newChannel(stream)) {
					for(long pos = 0L, num;
						(num = fch.transferFrom(rbc, pos, Long.MAX_VALUE)) > 0;
						pos += num);
				}
			} else {
				fch.write(emptyContentBytes());
			}
		}
		
		return new Sitemap(path);
	}
	
	private final SitemapUrlIterator newIterator() throws IOException {
		return new SitemapUrlIterator(
			new BufferedInputStream(Files.newInputStream(path, OPTS_READ))
		);
	}
	
	private final void loadEntries() {
		Map<String, SitemapUrlEntry> mapping = new LinkedHashMap<>();
		for(SitemapUrlEntry entry : this) mapping.put(entry.loc(), entry);
		entries = mapping;
	}
	
	public SitemapUrlEntry get(String url) {
		if(entries == null) {
			loadEntries();
		}
		
		return entries.get(url);
	}
	
	@Override
	public SitemapUrlIterator iterator() {
		try {
			return newIterator();
		} catch(IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
