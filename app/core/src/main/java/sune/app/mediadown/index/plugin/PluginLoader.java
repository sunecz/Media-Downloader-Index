package sune.app.mediadown.index.plugin;

import eu.infomas.annotation.AnnotationDetector;
import sune.app.mediadown.index.event.*;
import sune.app.mediadown.index.plugin.PluginLoader.PluginLoaderEvent;
import sune.app.mediadown.index.util.Pair;
import sune.app.mediadown.index.util.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class PluginLoader implements EventBindable<PluginLoaderEvent> {
	
	private final Set<PluginFile> loadedPlugins = new LinkedHashSet<>();
	private final EventRegistry<PluginLoaderEvent> eventRegistry = new EventRegistry<>();
	
	private PluginLoader() {
	}
	
	private <V> void call(Event<PluginLoaderEvent, V> event, V value) {
		eventRegistry.call(event, value);
	}
	
	private static PluginFile newPluginFile(Path path) throws Exception {
		PluginTypeReporter reporter = new PluginTypeReporter(path);
		AnnotationDetector detector = new AnnotationDetector(reporter);
		detector.detect(path.toFile());
		
		Exception exception;
		if((exception = reporter.exception()) != null) {
			throw exception;
		}

		return new PluginFile(path, reporter.classLoader(), reporter.pluginClass(), reporter.plugin());
	}
	
	private static <T> T newPluginInstance(Class<? extends T> clazz)
			throws InstantiationException,
				   IllegalAccessException,
				   IllegalArgumentException,
				   InvocationTargetException,
				   NoSuchMethodException,
				   SecurityException,
				   ClassNotFoundException,
				   NoSuchFieldException {
		Constructor<?> constructor = clazz.getDeclaredConstructor();
		@SuppressWarnings("unchecked")
		T instance = (T) constructor.newInstance();
		return instance;
	}
	
	public static PluginLoader instance() {
		return InstanceHolder.instance;
	}
	
	public void load(List<Path> paths) throws Exception {
		for(Path path : paths) {
			PluginFile plugin = newPluginFile(path);
			call(PluginLoaderEvent.LOADING, plugin);
			
			boolean loaded = false;
			try {
				if(!Files.exists(path)) {
					continue;
				}
				
				PluginInstance instance = newPluginInstance(plugin.pluginClass());
				instance.initialize();
				plugin.setInstance(instance);
				loaded = loadedPlugins.add(plugin);
			} catch(Exception ex) {
				call(PluginLoaderEvent.ERROR_LOAD, new Pair<>(plugin, ex));
			} finally {
				call(loaded ? PluginLoaderEvent.LOADED : PluginLoaderEvent.NOT_LOADED, plugin);
			}
		}
	}
	
	public void dispose() throws Exception {
		for(PluginFile plugin : loadedPlugins) {
			try {
				plugin.instance().dispose();
			} catch(Exception ex) {
				call(PluginLoaderEvent.ERROR_DISPOSE, new Pair<>(plugin, ex));
			}
		}
	}
	
	@Override
	public <V> void addEventListener(Event<? extends PluginLoaderEvent, V> event, Listener<V> listener) {
		eventRegistry.add(event, listener);
	}
	
	@Override
	public <V> void removeEventListener(Event<? extends PluginLoaderEvent, V> event, Listener<V> listener) {
		eventRegistry.remove(event, listener);
	}
	
	public Set<PluginFile> loadedPlugins() {
		return Collections.unmodifiableSet(loadedPlugins);
	}
	
	public static final class PluginLoaderEvent implements EventType {
		
		public static final Event<PluginLoaderEvent, PluginFile>                  LOADING       = new Event<>();
		public static final Event<PluginLoaderEvent, PluginFile>                  LOADED        = new Event<>();
		public static final Event<PluginLoaderEvent, PluginFile>                  NOT_LOADED    = new Event<>();
		public static final Event<PluginLoaderEvent, Pair<PluginFile, Exception>> ERROR_LOAD    = new Event<>();
		public static final Event<PluginLoaderEvent, Pair<PluginFile, Exception>> ERROR_DISPOSE = new Event<>();
		
		private static Event<PluginLoaderEvent, ?>[] values;
		
		// Forbid anyone to create an instance of this class
		private PluginLoaderEvent() {
		}
		
		public static Event<PluginLoaderEvent, ?>[] values() {
			if(values == null) {
				values = Utils.array(LOADING, LOADED, NOT_LOADED, ERROR_LOAD, ERROR_DISPOSE);
			}
			
			return values;
		}
	}
	
	private static final class InstanceHolder {
		
		public static final PluginLoader instance = new PluginLoader();
	}
}