package sune.app.mediadown.index.util;

import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

// Context-dependant Singleton instantiator
public final class InstanceSingleton {
	
	private static final Map<Class<?>, InstanceSingleton> instances = new HashMap<>();
	
	private final Class<?> clazz;
	private Object instance;
	
	private InstanceSingleton(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	public static <T> T getInstance() {
		Class<?> clazz = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE).walk((stream) -> {
			return stream.filter((p) -> p.getDeclaringClass() != InstanceSingleton.class)
					 .map(StackFrame::getDeclaringClass)
					 .findFirst().get();
		});
		return instances.computeIfAbsent(clazz, InstanceSingleton::new).instance();
	}
	
	private <T> T newInstance() {
		try {
			@SuppressWarnings("unchecked")
			Constructor<T> ctor = (Constructor<T>) clazz.getDeclaredConstructor();
			ctor.setAccessible(true);
			T instance = ctor.newInstance();
			ctor.setAccessible(false);
			return instance;
		} catch(Exception ex) {
			// Assume, the class is instantiable
		}
		// This should not happen
		return null;
	}
	
	private <T> T instance() {
		@SuppressWarnings("unchecked")
		T obj = (T) (instance == null ? (instance = newInstance()) : instance);
		return obj;
	}
}