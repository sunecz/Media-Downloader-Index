package sune.app.mediadown.index.media;

import java.util.List;

public interface MediaContainer extends Media, MediaAccessor {
	
	boolean isSeparated();
	default boolean isCombined() {
		return !isSeparated();
	}
	
	interface Builder<T extends Media, B extends Builder<T, B>> extends Media.Builder<T, B> {
		
		B media(List<? extends Media.Builder<?, ?>> media);
		B media(Media.Builder<?, ?>... media);
		B addMedia(List<? extends Media.Builder<?, ?>> media);
		B addMedia(Media.Builder<?, ?>... media);
		B removeMedia(List<? extends Media.Builder<?, ?>> media);
		B removeMedia(Media.Builder<?, ?>... media);
		
		List<Media.Builder<?, ?>> media();
	}
}