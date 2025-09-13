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

import cz.cvut.kbss.jopa.exceptions.StorageAccessException;
import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.Arguments;
import sune.app.mediadown.index.Website;
import sune.app.mediadown.index.Websites;
import sune.app.mediadown.index.entity.GraphMeta;
import sune.app.mediadown.index.entity.Types;
import sune.app.mediadown.index.extract.ExtractionContext;
import sune.app.mediadown.index.extract.Extractor;
import sune.app.mediadown.index.extract.WebsiteExtractNode;

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
	
	private final void updateMetadata() throws Exception {
		logger.info("Updating metadata...");
		
		ExtractionContext context = extractor.context();
		EntityManager entityManager = context.entityManager();
		GraphMeta meta = null;
		
		try {
			meta = entityManager.find(GraphMeta.class, GraphMeta.ID);
		} catch(StorageAccessException ex) {
			Exception cause = ex;
			
			for(Exception e = cause;
					(e = (Exception) e.getCause()) != null;
					cause = e);
			
			// Check whether the error is about the graph not existing
			if(!"404 - Not Found".equals(cause.getMessage())) {
				throw ex; // Rethrow anything else
			}
		}
		
		if(meta == null) {
			logger.info("No metadata found, will be created");
			
			// Cannot run persist, because it will throw 404 HTTP error. Instead, run
			// a native query that inserts the initial data directly, JOPA will otherwise
			// check for the graph existance again, thus throwing the same error.
			String query = """
			PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>
			PREFIX mdig: <%1$s>
			INSERT DATA {
				GRAPH <%2$s> {
					<%3$s> a            mdig:meta    ;
					       mdig:version "0"^^xsd:int .
				}
			}
			""".formatted(
				Types.PREFIX_MDI_GRAPH,
				Types.URI_DEFAULT_GRAPH + "#meta",
				Types.URI_DEFAULT_GRAPH
			);
			
			context.runUpdateQuery(query);
		} else {
			int oldVersion = meta.getVersion();
			int newVersion = oldVersion + 1;
			logger.info("Metadata version: current={}, new={}", oldVersion, newVersion);
			
			meta.setVersion(newVersion);
			entityManager.merge(meta);
		}
		
		logger.info("Metadata updated");
	}
	
	@Override
	protected void execute() throws Exception {
		List<Website> websites = websites();
		
		logger.info(
			"Websites: {}",
			String.join(", ", websites.stream().map(Website::name).toArray(String[]::new))
		);
		
		for(Website website : websites) {
			extractor.addNode(new WebsiteExtractNode(website));
		}
		
		extractor.process();
		
		// After everything is processed, update the dataset metadata
		extractor.context().doTransaction(this::updateMetadata);
	}
}
