package sune.app.mediadown.index.util;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import sune.app.mediadown.index.util.DBUtils;

public final class DBUtils {

	private DBUtils() {
	}

	public static String quote(String value) {
		return '"' + value + '"';
	}

	public static String quoteType(String value) {
		return '<' + value + '>';
	}

	@SafeVarargs
	public static <T> String asList(T... types) {
		return asList(Set.of(types));
	}

	public static <T> String asList(Collection<T> collection) {
		return asList(collection, DBUtils::quote);
	}

	public static <T> String asList(Collection<T> collection, Function<String, String> mapper) {
		return String.join(", ", collection.stream().map(T::toString).map(mapper).toArray(String[]::new));
	}

	@SafeVarargs
	public static <T> String asTypeList(T... types) {
		return asTypeList(Set.of(types));
	}

	public static <T> String asTypeList(Collection<T> collection) {
		return String.join(" ", collection.stream().map(T::toString).map(DBUtils::quoteType).toArray(String[]::new));
	}
}