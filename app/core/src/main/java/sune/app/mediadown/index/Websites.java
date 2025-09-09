package sune.app.mediadown.index;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sune.app.mediadown.index.util.InstanceFactory;

@Component
public final class Websites {
	
	private static Websites instance;
	
	private final Map<String, Website> websites = new HashMap<>();
	private final Map<Class<?>, String> names = new HashMap<>();
	private final InstanceFactory instanceFactory;
	
	@Autowired
	Websites(InstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
		instance = this;
	}
	
	private final void doRegister(String name, Class<? extends Website> clazz) {
		Website website = instanceFactory.newInstance(Objects.requireNonNull(clazz));
		websites.putIfAbsent(name, website);
		names.putIfAbsent(website.getClass(), name);
	}
	
	private final String doGetName(Class<? extends Website> clazz) {
		return names.get(clazz);
	}
	
	private final List<Website> doGetAll() {
		return List.copyOf(websites.values());
	}
	
	public static void register(String name, Class<? extends Website> clazz) {
		instance.doRegister(name, clazz);
	}

	public static String getName(Class<? extends Website> clazz) {
		return instance.doGetName(clazz);
	}

	public static List<Website> all() {
		return instance.doGetAll();
	}
}