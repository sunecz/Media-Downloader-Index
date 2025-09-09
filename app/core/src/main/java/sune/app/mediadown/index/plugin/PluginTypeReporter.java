package sune.app.mediadown.index.plugin;

import eu.infomas.annotation.AnnotationDetector.TypeReporter;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// Package-private class
final class PluginTypeReporter implements TypeReporter {
	
	@SuppressWarnings("unchecked")
	private static final Class<? extends Annotation>[] annotations = new Class[] { Plugin.class };
	
	private final Path path;
	private ClassLoader classLoader;
	private Class<? extends PluginInstance> pluginClass;
	private Plugin plugin;
	private Exception exception;
	
	public PluginTypeReporter(Path path) {
		this.path = Objects.requireNonNull(path);
	}
	
	private static String toEntryName(String className) {
		return className.replace('.', '/') + ".class";
	}

	private DummyClassLoader createClassLoader() throws MalformedURLException {
		return new DummyClassLoader(path.toUri().toURL(), getClass().getClassLoader());
	}
	
	@Override
	public void reportTypeAnnotation(Class<? extends Annotation> annotation, String className) {
		exception = null;
		
		try(ZipFile zip = new ZipFile(path.toFile())) {
			ZipEntry entry = zip.getEntry(toEntryName(className));
			
			try(InputStream stream = zip.getInputStream(entry)) {
				DummyClassLoader loader = createClassLoader();
				@SuppressWarnings("unchecked")
				var clazz = (Class<? extends PluginInstance>) loader.defineClass(className, stream.readAllBytes());
				classLoader = loader;
				pluginClass = clazz;
				plugin = clazz.getAnnotation(Plugin.class);
			}
		} catch(Exception ex) {
			exception = ex;
		}
	}
	
	@Override
	public Class<? extends Annotation>[] annotations() {
		return annotations;
	}

	public ClassLoader classLoader() {
		return classLoader;
	}

	public Class<? extends PluginInstance> pluginClass() {
		return pluginClass;
	}
	
	public Plugin plugin() {
		return plugin;
	}
	
	public Exception exception() {
		return exception;
	}
	
	// Used for loading plugin information. Since all plugins are later loaded
	// to the system class loader, we do not want to load any of their classes
	// before actually deciding if the plugin should be loaded.
	private static final class DummyClassLoader extends URLClassLoader {
		
		DummyClassLoader(URL url, ClassLoader parentClassLoader) {
			super(new URL[] { Objects.requireNonNull(url) }, parentClassLoader);
		}
		
		public Class<?> defineClass(String name, byte[] buf) throws ClassFormatError {
			return defineClass(name, buf, 0, buf.length);
		}
	}
}