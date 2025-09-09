package sune.app.mediadown.index.plugin;

import java.nio.file.Path;
import java.util.Objects;

public final class PluginFile {
	
	private final Path path;
	private final ClassLoader classLoader;
	private final Class<? extends PluginInstance> pluginClass;
	private final Plugin plugin;
	private PluginInstance instance;
	
	public PluginFile(Path path, ClassLoader classLoader, Class<? extends PluginInstance> pluginClass, Plugin plugin) {
		this.path = Objects.requireNonNull(path);
		this.pluginClass = Objects.requireNonNull(pluginClass);
		this.plugin = Objects.requireNonNull(plugin);
		this.classLoader = Objects.requireNonNull(classLoader);
	}

	void setInstance(PluginInstance instance) {
		this.instance = instance;
	}
	
	public Path path() {
		return path;
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

	public PluginInstance instance() {
		return instance;
	}
}