package sune.app.mediadown.index.media;

import java.net.URI;
import java.util.List;
import java.util.Objects;

public class VirtualAudioMedia extends SimpleAudioMedia {
	
	protected VirtualAudioMedia(
			MediaSource source, URI uri, MediaType type, MediaFormat format, MediaQuality quality,
			long size, MediaMetadata metadata, Media parent, MediaLanguage language, double duration,
			List<String> codecs, int bandwidth, int sampleRate
	) {
		super(
			source, uri, type, format, quality, size, metadata, parent,
			language, duration, codecs, bandwidth, sampleRate
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
	
	public static class Builder extends SimpleAudioMedia.Builder {
		
		protected Builder() {
		}
		
		@Override
		public VirtualAudioMedia build() {
			return new VirtualAudioMedia(
				Objects.requireNonNull(source), uri, MEDIA_TYPE,
				Objects.requireNonNull(format), Objects.requireNonNull(quality),
				size, Objects.requireNonNull(metadata), parent,
				Objects.requireNonNull(language), duration,
				Objects.requireNonNull(codecs), bandwidth, sampleRate
			);
		}
	}
}