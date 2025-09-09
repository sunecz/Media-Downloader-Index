package sune.app.mediadown.index.util;

public interface LinkedDataHydrator {
	
	<T> T hydrate(T object) throws Exception;
}
