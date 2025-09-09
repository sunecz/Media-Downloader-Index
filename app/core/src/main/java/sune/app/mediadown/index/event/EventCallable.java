package sune.app.mediadown.index.event;

public interface EventCallable<T extends EventType> {
	
	<V> void call(Event<? extends T, V> event);
	<V> void call(Event<? extends T, V> event, V value);
}