package sune.app.mediadown.index.util;

public interface Transactions {

	<T> T doTransaction(CheckedSupplier<T> action) throws Exception;
	void doTransaction(CheckedRunnable action) throws Exception;
}