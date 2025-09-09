package sune.app.mediadown.index.util;

public interface Ref<T> {
	
	T get();
	
	class Immutable<T> implements Ref<T> {
		
		private final T value;
		
		public Immutable(T value) {
			this.value = value;
		}
		
		@Override
		public T get() {
			return value;
		}
	}
	
	class Mutable<T> implements Ref<T> {
		
		private T value;
		
		public Mutable() {
			this.value = null;
		}
		
		public Mutable(T value) {
			this.value = value;
		}
		
		public void set(T value) {
			this.value = value;
		}
		
		@Override
		public T get() {
			return value;
		}
	}
}