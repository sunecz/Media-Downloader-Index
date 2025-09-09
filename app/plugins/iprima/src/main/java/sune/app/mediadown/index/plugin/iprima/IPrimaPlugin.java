package sune.app.mediadown.index.plugin.iprima;

import sune.app.mediadown.index.Websites;
import sune.app.mediadown.index.plugin.Plugin;
import sune.app.mediadown.index.plugin.PluginBase;

@Plugin(
	name    = "iprima",
	title   = "iPrima",
	version = "0.0.1",
	author  = "Sune"
)
public class IPrimaPlugin extends PluginBase {
	
	@Override
	public void initialize() throws Exception {
		Websites.register(plugin().name(), IPrimaWebsite.class);
	}
}
