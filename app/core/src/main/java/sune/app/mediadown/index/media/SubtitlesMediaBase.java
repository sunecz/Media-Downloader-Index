package sune.app.mediadown.index.media;

public interface SubtitlesMediaBase {
	
	MediaLanguage language();
	
	interface Builder<T extends SubtitlesMediaBase, B extends SubtitlesMediaBase.Builder<T, B>> {
		
		Builder<T, B> language(MediaLanguage language);
		MediaLanguage language();
	}
}