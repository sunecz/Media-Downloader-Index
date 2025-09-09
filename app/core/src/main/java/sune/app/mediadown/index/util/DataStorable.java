package sune.app.mediadown.index.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface DataStorable {
	
	boolean has(String name);
	void set(String name, Object value);
	<T> T get(String name);
	<T> T get(String name, T defaultValue);
	void remove(String name);
	Set<String> keys();
	Collection<Object> values();
	Map<String, Object> data();
}