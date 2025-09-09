package sune.app.mediadown.index.task;

public final class TaskStates {
	
	public static final int INITIAL = 0;
	public static final int STARTED = 1 << 0;
	public static final int RUNNING = 1 << 1;
	public static final int STOPPED = 1 << 3;
	public static final int DONE    = 1 << 4;
	public static final int ERROR   = 1 << 5;
	
	// Forbid anyone to create an instance of this class
	private TaskStates() {
	}
}