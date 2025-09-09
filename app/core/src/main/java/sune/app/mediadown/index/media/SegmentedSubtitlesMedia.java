package sune.app.mediadown.index.media;

import java.net.URI;
import java.util.Objects;

import sune.app.mediadown.index.segment.FileSegmentsHolder;

public class SegmentedSubtitlesMedia extends SegmentedMedia implements SubtitlesMedia {
	
	public static final MediaType MEDIA_TYPE = MediaType.SUBTITLES;
	
	protected final MediaLanguage language;
	
	protected SegmentedSubtitlesMedia(MediaSource source, URI uri, MediaFormat format, long size,
			MediaMetadata metadata, Media parent, FileSegmentsHolder segments, MediaLanguage language) {
		super(source, uri, MEDIA_TYPE, checkFormat(format), MediaQuality.UNKNOWN, size, metadata, parent,
		      Objects.requireNonNull(segments));
		this.language = Objects.requireNonNull(language);
	}
	
	private static final MediaFormat checkFormat(MediaFormat format) {
		if(!isValidFormat(format))
			throw new IllegalArgumentException("Invalid subtitles format");
		return format;
	}
	
	protected static final boolean isValidFormat(MediaFormat format) {
		return format.is(MediaFormat.UNKNOWN) || format.mediaType().is(MEDIA_TYPE);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@Override
	public MediaLanguage language() {
		return language;
	}
	
	public static class Builder extends SegmentedMedia.Builder<SegmentedSubtitlesMedia, Builder>
			implements SubtitlesMedia.Builder<SegmentedSubtitlesMedia, Builder> {
		
		protected MediaLanguage language;
		
		protected Builder() {
			type = MEDIA_TYPE;
			language = MediaLanguage.UNKNOWN;
		}
		
		@Override
		public SegmentedSubtitlesMedia build() {
			return new SegmentedSubtitlesMedia(
				Objects.requireNonNull(source), uri,
				Objects.requireNonNull(format),
				size, Objects.requireNonNull(metadata), parent,
				Objects.requireNonNull(segments),
				Objects.requireNonNull(language)
			);
		}
		
		@Override
		public Builder type(MediaType type) {
			throw new UnsupportedOperationException("Cannot set media type");
		}
		
		@Override
		public Builder format(MediaFormat format) {
			return super.format(checkFormat(format));
		}
		
		@Override
		public Builder quality(MediaQuality quality) {
			throw new UnsupportedOperationException("Cannot set media quality");
		}
		
		@Override
		public Builder language(MediaLanguage language) {
			this.language = Objects.requireNonNull(language);
			return this;
		}
		
		@Override
		public MediaLanguage language() {
			return language;
		}
	}
}