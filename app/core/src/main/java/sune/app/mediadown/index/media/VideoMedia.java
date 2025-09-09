package sune.app.mediadown.index.media;

public interface VideoMedia extends Media, VideoMediaBase {
	
	static SimpleVideoMedia.Builder simple() {
		return SimpleVideoMedia.builder();
	}
	
	static SegmentedVideoMedia.Builder segmented() {
		return SegmentedVideoMedia.builder();
	}
	
	static VirtualVideoMedia.Builder virtual() {
		return VirtualVideoMedia.builder();
	}
	
	interface Builder<T extends VideoMedia,
	                                B extends Media.Builder<T, B> & VideoMediaBase.Builder<T, B>>
			extends Media.Builder<T, B>, VideoMediaBase.Builder<T, B> {
	}
}