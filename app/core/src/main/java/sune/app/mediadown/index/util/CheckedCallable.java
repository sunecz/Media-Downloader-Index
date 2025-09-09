package sune.app.mediadown.index.util;

@FunctionalInterface
public interface CheckedCallable<V> {
	
	V run() throws Throwable;
}