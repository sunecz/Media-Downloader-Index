package sune.app.mediadown.index.media;

import java.util.List;
import java.util.Objects;

// Package-private
abstract class ChildMediaBuilderContext {
	
	protected final List<? extends Media.Builder<?, ?>> media;
	
	public ChildMediaBuilderContext(List<? extends Media.Builder<?, ?>> media) {
		this.media = Objects.requireNonNull(media);
	}
	
	public abstract List<Media> build(Media parent);
}