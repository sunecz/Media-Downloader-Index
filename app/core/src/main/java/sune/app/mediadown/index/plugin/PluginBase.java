package sune.app.mediadown.index.plugin;

import java.util.Objects;

public abstract class PluginBase implements PluginInstance {
	
	private final Plugin plugin;
	
	protected PluginBase() {
		plugin = Objects.requireNonNull(getClass().getAnnotation(Plugin.class));
	}
	
	@Override
	public void initialize() throws Exception {
		// Do nothing
	}
	
	@Override
	public void dispose() throws Exception {
		// Do nothing
	}
	
	public Plugin plugin() {
		return plugin;
	}
}