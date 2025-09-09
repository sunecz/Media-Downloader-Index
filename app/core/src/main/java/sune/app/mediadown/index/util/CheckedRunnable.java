package sune.app.mediadown.index.util;

@FunctionalInterface
public interface CheckedRunnable {
	
	void run() throws Exception;
}