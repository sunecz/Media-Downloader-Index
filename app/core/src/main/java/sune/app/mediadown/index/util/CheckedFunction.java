package sune.app.mediadown.index.util;

@FunctionalInterface
public interface CheckedFunction<T, R> {
	
	R apply(T t) throws Exception;
}