package sune.app.mediadown.index.util;

import cz.cvut.kbss.jopa.model.annotations.Namespace;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.Properties;
import sune.app.mediadown.index.entity.Types;
import sune.app.mediadown.index.util.DatabaseDataStorable;

import java.util.*;

@Namespace(prefix = "mdi", namespace = Types.PREFIX_MDI)
@OWLClass(iri = "mdi:DataStorable")
public abstract class DatabaseDataStorable {

	private static final String PREFIX = Types.PREFIX_MDI + "properties/";
	private static final Map<String, Set<String>> EMPTY_DATA = new TreeMap<>(Comparator.naturalOrder());

	@Properties
	protected Map<String, Set<String>> data;

	public DatabaseDataStorable() {
		data = EMPTY_DATA;
	}

	public DatabaseDataStorable(Map<String, Set<String>> values) {
		// Only initialize the data field if really needed
		if(values != null && !values.isEmpty()) {
			data = new TreeMap<>(Comparator.naturalOrder());
			data.putAll(values);
		} else {
			data = EMPTY_DATA;
		}
	}
	
	protected final boolean isEmptyData() {
		return data == null || data == EMPTY_DATA;
	}
	
	protected final Map<String, Set<String>> ensureOwnData() {
		if(isEmptyData()) {
			data = new TreeMap<>(Comparator.naturalOrder());
		}
		
		return data;
	}
	
	public boolean has(String name) {
		if(isEmptyData()) {
			return false;
		}
		
		return data.containsKey(PREFIX + name);
	}
	
	public void setSingle(String name, String value) {
		set(name, Set.of(value));
	}

	public void set(String name, Set<String> values) {
		ensureOwnData().put(PREFIX + name, values);
	}
	
	public Set<String> get(String name) {
		if(isEmptyData()) {
			return null;
		}
		
		return data.get(PREFIX + name);
	}
	
	public Set<String> get(String name, Set<String> defaultValue) {
		Set<String> value;
		return (value = get(name)) != null
					? value
					: defaultValue;
	}

	public String getSingle(String name) {
		if(isEmptyData()) {
			return null;
		}

		Set<String> value;
		return (value = data.get(PREFIX + name)) == null || value.isEmpty()
					? null
					: value.iterator().next();
	}

	public String getSingle(String name, String defaultValue) {
		String value;
		return (value = getSingle(name)) != null
					? value
					: defaultValue;
	}
	
	public void remove(String name) {
		if(isEmptyData()) {
			return;
		}
		
		data.remove(PREFIX + name);
	}
	
	public Set<String> keys() {
		if(isEmptyData()) {
			return Set.of();
		}
		
		return Set.copyOf(data.keySet());
	}
	
	public List<Set<String>> values() {
		if(isEmptyData()) {
			return List.of();
		}
		
		return List.copyOf(data.values());
	}
	
	public Map<String, Set<String>> data() {
		if(isEmptyData()) {
			return Map.of();
		}
		
		return Map.copyOf(data);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(data);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		DatabaseDataStorable other = (DatabaseDataStorable) obj;
		return Objects.equals(data, other.data);
	}
}