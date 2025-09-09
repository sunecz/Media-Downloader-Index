package sune.app.mediadown.index.util;

public interface HasTaskState {
	
	boolean isRunning();
	boolean isDone();
	boolean isStarted();
	boolean isStopped();
	boolean isError();
}