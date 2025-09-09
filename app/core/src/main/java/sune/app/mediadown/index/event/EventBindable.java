package sune.app.mediadown.index.event;

public interface EventBindable<T extends EventType> {
	
	<V> void addEventListener(Event<? extends T, V> event, Listener<V> listener);
	<V> void removeEventListener(Event<? extends T, V> event, Listener<V> listener);
}