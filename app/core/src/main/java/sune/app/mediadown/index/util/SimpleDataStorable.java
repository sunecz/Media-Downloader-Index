package sune.app.mediadown.index.util;

import java.util.*;

public class SimpleDataStorable {

	private static final Map<String, Object> EMPTY_DATA = new TreeMap<>(Comparator.naturalOrder());
	
	protected Map<String, Object> data;
	
	public SimpleDataStorable() {
		data = EMPTY_DATA;
	}
	
	public SimpleDataStorable(Map<String, Object> values) {
		// Only initialize the data field if really needed
		if(values != null && !values.isEmpty()) {
			data = new TreeMap<>(Comparator.naturalOrder());
			data.putAll(values);
		} else {
			data = EMPTY_DATA;
		}
	}
	
	protected final boolean isEmptyData() {
		return data == EMPTY_DATA;
	}
	
	protected final Map<String, Object> ensureOwnData() {
		if(isEmptyData()) {
			data = new TreeMap<>(Comparator.naturalOrder());
		}
		
		return data;
	}
	
	public boolean has(String name) {
		if(isEmptyData()) {
			return false;
		}
		
		return data.containsKey(name);
	}
	
	public void set(String name, Object value) {
		ensureOwnData().put(name, value);
	}
	
	public <T> T get(String name) {
		if(isEmptyData()) {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		T value = (T) data.get(name);
		return value;
	}
	
	public <T> T get(String name, T defaultValue) {
		T value;
		return (value = get(name)) != null
					? value
					: defaultValue;
	}
	
	public void remove(String name) {
		if(isEmptyData()) {
			return;
		}
		
		data.remove(name);
	}
	
	public Set<String> keys() {
		if(isEmptyData()) {
			return Set.of();
		}
		
		return Set.copyOf(data.keySet());
	}
	
	public List<Object> values() {
		if(isEmptyData()) {
			return List.of();
		}
		
		return List.copyOf(data.values());
	}
	
	public Map<String, Object> data() {
		if(isEmptyData()) {
			return Map.of();
		}
		
		return Map.copyOf(data);
	}
	
	public int hashCode() {
		return Objects.hash(data);
	}
	
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		SimpleDataStorable other = (SimpleDataStorable) obj;
		return Objects.equals(data, other.data);
	}
}