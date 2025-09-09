package sune.app.mediadown.index.util;

import java.util.Objects;

@FunctionalInterface
public interface CheckedBiConsumer<T, U> {
	
	void accept(T t, U u) throws Exception;
	
	default CheckedBiConsumer<T, U> andThen(CheckedBiConsumer<? super T, ? super U> after) {
		Objects.requireNonNull(after);
		return (l, r) -> {
			accept(l, r);
			after.accept(l, r);
		};
	}
}