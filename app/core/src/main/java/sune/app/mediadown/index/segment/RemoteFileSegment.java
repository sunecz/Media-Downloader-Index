package sune.app.mediadown.index.segment;

import java.net.URI;
import java.util.Objects;

import sune.app.mediadown.index.media.MediaConstants;

public class RemoteFileSegment implements FileSegment {
	
	protected final URI uri;
	protected final long size;
	protected final double duration;
	
	public RemoteFileSegment(URI uri) {
		this(uri, MediaConstants.UNKNOWN_SIZE, MediaConstants.UNKNOWN_DURATION);
	}
	
	public RemoteFileSegment(URI uri, long size) {
		this(uri, size, MediaConstants.UNKNOWN_DURATION);
	}
	
	public RemoteFileSegment(URI uri, long size, double duration) {
		this.uri = Objects.requireNonNull(uri);
		this.size = size;
		this.duration = duration;
	}
	
	@Override
	public URI uri() {
		return uri;
	}
	
	@Override
	public long size() {
		return size;
	}
	
	@Override
	public double duration() {
		return duration;
	}
}