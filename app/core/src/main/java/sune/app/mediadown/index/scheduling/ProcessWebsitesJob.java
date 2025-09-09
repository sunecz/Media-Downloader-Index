package sune.app.mediadown.index.scheduling;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import sune.app.mediadown.index.Arguments;
import sune.app.mediadown.index.Website;
import sune.app.mediadown.index.Websites;
import sune.app.mediadown.index.extract.Extractor;

@Component("job.process_websites")
public class ProcessWebsitesJob extends CoalescingJob {
	
	private static final Logger logger = LoggerFactory.getLogger(ProcessWebsitesJob.class);
	
	private final Arguments arguments;
	private final Extractor extractor;
	
	private List<Website> websites;
	
	public ProcessWebsitesJob(ApplicationArguments args, Extractor extractor) {
		this.arguments = Arguments.parse(args.getSourceArgs());
		this.extractor = extractor;
	}
	
	// The Website list must be created after this component's construction due to
	// the plugins being loaded later (and thus the possible Website registration).
	private final List<Website> websites() {
		List<Website> websites;
		if((websites = this.websites) == null) {
			Set<String> names = Stream.of(arguments.getValue("websites", "").split(","))
				.filter(Predicate.not(String::isBlank))
				.map(String::strip)
				.collect(Collectors.toCollection(LinkedHashSet::new));
			
			List<Website> allWebsites = Websites.all();
			this.websites = websites = (
				names.isEmpty()
					? allWebsites
					: allWebsites.stream().filter((w) -> names.contains(w.name())).toList()
			);
		}
		
		return websites;
	}
	
	@Override
	protected void execute() throws Exception {
		List<Website> websites = websites();
		
		logger.info(
			"Websites: {}",
			String.join(", ", websites.stream().map(Website::name).toArray(String[]::new))
		);
		
		extractor.extract(websites);
	}
}
