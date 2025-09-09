package sune.app.mediadown.index.scheduling;

import java.time.ZoneId;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

/**
 * Simple application scheduler that takes a single coalescing job.
 */
@Component
public class ApplicationScheduler {
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationScheduler.class);
	
	private final TaskScheduler scheduler;
	private final CoalescingJob job;
	private final String cron;
	private final ZoneId zone;
	
	public ApplicationScheduler(
		TaskScheduler scheduler,
		CoalescingJob job,
		@Value("${app.scheduler.cron:0 */30 * * * *}") String cron,
		@Value("${app.scheduler.zone:system}") String zone
	) {
		this.scheduler = Objects.requireNonNull(scheduler);
		this.job = Objects.requireNonNull(job);
		this.cron = cron;
		this.zone = (
			"system".equalsIgnoreCase(zone)
				? ZoneId.systemDefault()
				: ZoneId.of(zone)
		);
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void initialize() {
		logger.info("Immediately executing the job...");
		job.tick();
		
		logger.info("Scheduling the job, CRON '{}' in ZONE '{}'", cron, zone);
		scheduler.schedule(job::tick, new CronTrigger(cron, zone));
	}
}
