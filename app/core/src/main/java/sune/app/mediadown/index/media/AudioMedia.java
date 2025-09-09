package sune.app.mediadown.index.media;

public interface AudioMedia extends Media, AudioMediaBase {
	
	static SimpleAudioMedia.Builder simple() {
		return SimpleAudioMedia.builder();
	}
	
	static SegmentedAudioMedia.Builder segmented() {
		return SegmentedAudioMedia.builder();
	}
	
	static VirtualAudioMedia.Builder virtual() {
		return VirtualAudioMedia.builder();
	}
	
	interface Builder<T extends AudioMedia,
	                                B extends Media.Builder<T, B> & AudioMediaBase.Builder<T, B>>
			extends Media.Builder<T, B>, AudioMediaBase.Builder<T, B> {
	}
}