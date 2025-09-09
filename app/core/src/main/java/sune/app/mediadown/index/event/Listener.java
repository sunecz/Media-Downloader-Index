package sune.app.mediadown.index.event;

public interface Listener<V> {
	
	void call(V value);
}