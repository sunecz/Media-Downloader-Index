package sune.app.mediadown.index.media;

public interface AudioMediaContainer extends MediaContainer, AudioMediaBase {
	
	static CombinedAudioMediaContainer.Builder combined() {
		return CombinedAudioMediaContainer.builder();
	}
	
	static SeparatedAudioMediaContainer.Builder separated() {
		return SeparatedAudioMediaContainer.builder();
	}
	
	interface Builder<T extends AudioMediaContainer,
	                                B extends MediaContainer.Builder<T, B> & AudioMediaBase.Builder<T, B>>
			extends MediaContainer.Builder<T, B>, AudioMediaBase.Builder<T, B> {
	}
}