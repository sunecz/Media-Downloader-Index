package sune.app.mediadown.index.task;

import java.util.List;

import sune.app.mediadown.index.util.CheckedConsumer;
import sune.app.mediadown.index.util.CheckedSupplier;

public final class Tasks {
	
	// Forbid anyone to create an instance of this class
	private Tasks() {
	}
	
	public static <T> ListTask<T> list(CheckedConsumer<ListTask<T>> runnable) {
		return ListTask.of(runnable);
	}
	
	public static <T> ListTask<T> listOfOne(CheckedSupplier<T> supplier) {
		return list((task) -> task.add(supplier.get()));
	}
	
	public static <T> ListTask<T> listOfMany(CheckedSupplier<List<T>> supplier) {
		return list((task) -> task.addAll(supplier.get()));
	}
}