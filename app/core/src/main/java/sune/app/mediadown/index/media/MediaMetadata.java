package sune.app.mediadown.index.media;

import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;

import sune.app.mediadown.index.net.Net;
import sune.app.mediadown.index.util.Utils;

public final class MediaMetadata {
	
	private static final MediaMetadata EMPTY = new MediaMetadata(Map.of());
	
	private final Map<String, Object> data;
	
	private MediaMetadata(Map<String, Object> data) {
		this.data = Objects.requireNonNull(data);
	}
	
	private static MediaMetadata ofRaw(Map<String, Object> data) {
		return data.isEmpty() ? empty() : new MediaMetadata(data);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(Builder... builders) {
		Objects.requireNonNull(builders);
		Builder builder = new Builder();
		for(Builder b : builders) builder.add(b);
		return builder;
	}
	
	public static Builder builder(MediaMetadata... metadata) {
		Objects.requireNonNull(metadata);
		Builder builder = new Builder();
		for(MediaMetadata m : metadata) builder.add(m);
		return builder;
	}
	
	public static MediaMetadata empty() {
		return EMPTY;
	}
	
	public static MediaMetadata of(Object... data) {
		return Objects.requireNonNull(data).length == 0 ? empty() : new MediaMetadata(Utils.stringKeyMap(data));
	}
	
	public static MediaMetadata of(Map<String, Object> data) {
		return Objects.requireNonNull(data).isEmpty() ? empty() : new MediaMetadata(new LinkedHashMap<>(data));
	}
	
	public static MediaMetadata of(Builder... builders) {
		return Objects.requireNonNull(builders).length == 0 ? empty() : builder(builders).build();
	}
	
	public static MediaMetadata of(MediaMetadata... metadata) {
		return Objects.requireNonNull(metadata).length == 0 ? empty() : builder(metadata).build();
	}
	
	public boolean has(String name) {
		return data.containsKey(name);
	}
	
	public boolean hasValue(Object value) {
		return data.containsValue(value);
	}
	
	public <T> T get(String name) {
		@SuppressWarnings("unchecked")
		T value = (T) data.get(name);
		return value;
	}
	
	public <T> T get(String name, T defaultValue) {
		@SuppressWarnings("unchecked")
		T value = (T) data.getOrDefault(name, defaultValue);
		return value;
	}
	
	public <T> T getOrSupply(String name, Supplier<T> supplier) {
		@SuppressWarnings("unchecked")
		T value = (T) data.get(name);
		return value == null && !data.containsKey(name) ? supplier.get() : value;
	}
	
	public boolean isEmpty() {
		return data.isEmpty();
	}
	
	public Map<String, Object> data() {
		return Collections.unmodifiableMap(data);
	}
	
	public URI sourceURI() {
		return get(Properties.sourceURI);
	}
	
	public String title() {
		return get(Properties.title);
	}
	
	public boolean isProtected() {
		return has(Properties.protections);
	}
	
	public List<MediaProtection> protections() {
		return get(Properties.protections, List.of());
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
		MediaMetadata other = (MediaMetadata) obj;
		return Objects.equals(data, other.data);
	}
	
	@Override
	public String toString() {
		return data.isEmpty() ? "EMPTY" : "[" + data + "]";
	}
	
	public static final class Properties {
		
		public static final String sourceURI = "sourceURI";
		public static final String title = "title";
		public static final String protections = "protection";
	}
	
	public static final class Builder {
		
		private final Map<String, Object> data;
		
		private Builder() {
			this(new LinkedHashMap<>());
		}
		
		private Builder(Map<String, Object> data) {
			this.data = data;
		}
		
		public MediaMetadata build() {
			return ofRaw(data);
		}
		
		public Builder add(Map<String, Object> data) {
			this.data.putAll(Objects.requireNonNull(data));
			return this;
		}
		
		public Builder add(Object... data) {
			return add(Utils.stringKeyMap(Objects.requireNonNull(data)));
		}
		
		public Builder add(Builder builder) {
			return add(Objects.requireNonNull(builder).data);
		}
		
		public Builder add(MediaMetadata metadata) {
			return add(Objects.requireNonNull(metadata).data);
		}
		
		public Builder addIfAbsent(Map<String, Object> data) {
			Objects.requireNonNull(data);
			data.forEach(this.data::putIfAbsent);
			return this;
		}
		
		public Builder addIfAbsent(Object... data) {
			return addIfAbsent(Utils.stringKeyMap(Objects.requireNonNull(data)));
		}
		
		public Builder addIfAbsent(Builder builder) {
			return addIfAbsent(Objects.requireNonNull(builder).data);
		}
		
		public Builder addIfAbsent(MediaMetadata metadata) {
			return addIfAbsent(Objects.requireNonNull(metadata).data);
		}
		
		public Builder remove(List<String> names) {
			Objects.requireNonNull(names).forEach(this.data::remove);
			return this;
		}
		
		public Builder remove(String... names) {
			return remove(List.of(Objects.requireNonNull(names)));
		}
		
		public Builder sourceURI(URI sourceURI) {
			this.data.put(Properties.sourceURI, Objects.requireNonNull(sourceURI));
			return this;
		}
		
		public Builder sourceURI(URL sourceURL) {
			return sourceURI(Net.uri(sourceURL));
		}
		
		public Builder sourceURI(String sourceURL) {
			return sourceURI(Net.uri(sourceURL));
		}
		
		public Builder title(String title) {
			this.data.put(Properties.title, Objects.requireNonNull(title));
			return this;
		}
		
		public Builder addProtections(MediaProtection... protections) {
			return addProtections(List.of(protections));
		}
		
		public Builder addProtections(List<MediaProtection> protections) {
			this.data.compute(Properties.protections, (key, list) -> {
				if(list == null) {
					list = new ArrayList<>();
				}
				
				Utils.<List<MediaProtection>>cast(list).addAll(protections);
				return list;
			});
			return this;
		}
		
		public Builder removeProtections(MediaProtection... protections) {
			return removeProtections(List.of(protections));
		}
		
		public Builder removeProtections(List<MediaProtection> protections) {
			this.data.computeIfPresent(Properties.protections, (key, list) -> {
				Utils.<List<MediaProtection>>cast(list).removeAll(protections);
				return list;
			});
			return this;
		}
		
		public Builder copy() {
			return new Builder(new LinkedHashMap<>(data));
		}
		
		public boolean has(String name) {
			return this.data.containsKey(name);
		}
		
		public <T> T get(String name) {
			@SuppressWarnings("unchecked")
			T value = (T) this.data.get(name);
			return value;
		}
		
		public URI sourceURI() {
			return get(Properties.sourceURI);
		}
		
		public String title() {
			return get(Properties.title);
		}
		
		public boolean isProtected() {
			return has(Properties.protections);
		}
		
		public List<MediaProtection> protections() {
			return get(Properties.protections);
		}
	}
}