package sune.app.mediadown.index;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;

import sune.app.mediadown.index.plugin.PluginLoader;

@Component
public class ApplicationInitializer implements ApplicationRunner {
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationInitializer.class);
	
	public ApplicationInitializer() {
	}
	
	private final void loadPlugins() throws Exception {
		ApplicationHome home = new ApplicationHome(Application.class);
		Path dir = home.getDir().toPath();
		
		// Special case when run directly from an IDE
		if(Files.isDirectory(home.getSource().toPath())) {
			dir = dir.getParent().getParent().getParent().resolve("dist");
		}
		
		dir = dir.resolve("plugins");
		logger.info("Searching for plugins in '{}'", dir);
		
		if(!Files.exists(dir)) {
			logger.info("Directory '{}' does not exist. Skipping loading plugins.", dir);
			return;
		}
		
		List<Path> paths = Files.list(dir)
			.filter((p) -> p.getFileName().toString().endsWith(".jar"))
			.toList();
		
		logger.info("Found {} plugins", paths.size());
		
		PluginLoader loader = PluginLoader.instance();
		
		loader.addEventListener(PluginLoader.PluginLoaderEvent.LOADING, (plugin) -> {
			logger.info("Loading plugin '{}'...", plugin.plugin().name());
		});
		
		loader.addEventListener(PluginLoader.PluginLoaderEvent.LOADED, (plugin) -> {
			logger.info("Loading plugin '{}'... Loaded", plugin.plugin().name());
		});
		
		loader.addEventListener(PluginLoader.PluginLoaderEvent.ERROR_LOAD, (pair) -> {
			logger.error("", pair.b);
		});
		
		loader.addEventListener(PluginLoader.PluginLoaderEvent.NOT_LOADED, (plugin) -> {
			logger.info("Loading plugin '{}'... Error", plugin.plugin().name());
		});
		
		loader.load(paths);
	}
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		loadPlugins();
	}
}
