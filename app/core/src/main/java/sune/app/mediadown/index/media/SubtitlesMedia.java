package sune.app.mediadown.index.media;

public interface SubtitlesMedia extends Media, SubtitlesMediaBase {
	
	static SimpleSubtitlesMedia.Builder simple() {
		return SimpleSubtitlesMedia.builder();
	}
	
	static SegmentedSubtitlesMedia.Builder segmented() {
		return SegmentedSubtitlesMedia.builder();
	}
	
	interface Builder<T extends SubtitlesMedia,
	                                B extends Media.Builder<T, B> & SubtitlesMediaBase.Builder<T, B>>
			extends Media.Builder<T, B>, SubtitlesMediaBase.Builder<T, B> {
	}
}