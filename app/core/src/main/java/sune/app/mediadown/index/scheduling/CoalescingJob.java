package sune.app.mediadown.index.scheduling;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A job which execution can be run only one at a time, coalescing
 * any pending execution requests.
 * 
 * This means that:
 * - at most one is running at any given time
 * - at most one is pending at any given time
 * 
 * If the job execution ends and there is a pending request, it is
 * executed right after.
 */
public abstract class CoalescingJob {
	
	private static final Logger logger = LoggerFactory.getLogger(CoalescingJob.class);
	
	private final AtomicBoolean running = new AtomicBoolean(false);
	private final AtomicBoolean pending = new AtomicBoolean(false);
	
	public void tick() {
		if(!running.compareAndSet(false, true)) {
			// Job is already running, coalesce to the next tick
			pending.set(true);
			logger.info("Job is already running, will be run in the next tick");
			return; // We're done
		}
		
		try {
			logger.info("Executing the job");
			execute();
			
			while(pending.getAndSet(false)) {
				logger.info("Pending run exists, executing the job");
				execute();
			}
		} catch(Exception ex) {
			logger.error("Job execution failed", ex);
			pending.set(false); // Ensure reset state
		} finally {
			running.set(false);
		}
	}
	
	protected abstract void execute() throws Exception;
}
