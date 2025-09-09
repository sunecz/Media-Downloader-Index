package sune.app.mediadown.index.util;

@FunctionalInterface
public interface CheckedCallback<P, R> {
	
	R call(P param) throws Exception;
}