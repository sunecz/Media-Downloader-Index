package sune.app.mediadown.index.extract;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sune.app.mediadown.index.Website;

public class WebsiteExtractNode implements ExtractNode {

	private static final Logger logger = LoggerFactory.getLogger(WebsiteExtractNode.class);

	private final Website website;

	public WebsiteExtractNode(Website website) {
		this.website = Objects.requireNonNull(website);
	}

	@Override
	public void extract(Extractor extractor, ExtractionContext context) throws Exception {
		logger.info("Processing website: {}", website.title());
		
		for(ProgramExtractNode program : website.getPrograms().startAndGet()) {
			extractor.addNode(program);
		}
	}
}
