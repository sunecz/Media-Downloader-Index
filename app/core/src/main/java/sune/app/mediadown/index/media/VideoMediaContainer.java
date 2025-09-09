package sune.app.mediadown.index.media;

public interface VideoMediaContainer extends MediaContainer, VideoMediaBase {
	
	static CombinedVideoMediaContainer.Builder combined() {
		return CombinedVideoMediaContainer.builder();
	}
	
	static SeparatedVideoMediaContainer.Builder separated() {
		return SeparatedVideoMediaContainer.builder();
	}
	
	interface Builder<T extends VideoMediaContainer,
	                                B extends MediaContainer.Builder<T, B> & VideoMediaBase.Builder<T, B>>
			extends MediaContainer.Builder<T, B>, VideoMediaBase.Builder<T, B> {
	}
}