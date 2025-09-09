package sune.app.mediadown.index.concurrent;

public final class SyncObject {
	
	public boolean await() {
		boolean success = true;
		
		synchronized(this) {
			try {
				wait();
			} catch(InterruptedException ex) {
				success = false;
			}
		}
		
		return success;
	}
	
	public void unlock() {
		synchronized(this) {
			notifyAll();
		}
	}
}