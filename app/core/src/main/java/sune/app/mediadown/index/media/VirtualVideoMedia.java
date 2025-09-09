package sune.app.mediadown.index.media;

import java.net.URI;
import java.util.List;
import java.util.Objects;

public class VirtualVideoMedia extends SimpleVideoMedia {
	
	protected VirtualVideoMedia(
			MediaSource source, URI uri, MediaType type, MediaFormat format, MediaQuality quality,
			long size, MediaMetadata metadata, Media parent, MediaResolution resolution, double duration,
			List<String> codecs, int bandwidth, double frameRate
	) {
		super(
			source, uri, type, format, quality, size, metadata, parent,
			resolution, duration, codecs, bandwidth, frameRate
		);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@Override
	public boolean isVirtual() {
		return true;
	}
	
	@Override
	public boolean isPhysical() {
		return false;
	}
	
	public static class Builder extends SimpleVideoMedia.Builder {
		
		protected Builder() {
		}
		
		@Override
		public VirtualVideoMedia build() {
			return new VirtualVideoMedia(
				Objects.requireNonNull(source), uri, MEDIA_TYPE,
				Objects.requireNonNull(format), Objects.requireNonNull(quality),
				size, Objects.requireNonNull(metadata), parent,
				Objects.requireNonNull(resolution), duration,
				Objects.requireNonNull(codecs), bandwidth, frameRate
			);
		}
	}
}