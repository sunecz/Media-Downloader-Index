package sune.app.mediadown.index.sitemap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sune.app.mediadown.index.task.ListTask;

public abstract class SitemapProcessor<T> {
	
	private static final Logger logger = LoggerFactory.getLogger(SitemapProcessor.class);
	
	public static enum SitemapUrlEntryType {
		ADDED, REMOVED, UPDATED;
	}
	
	private final Sitemap oldSitemap;
	private final Sitemap newSitemap;
	
	public SitemapProcessor(Sitemap oldSitemap, Sitemap newSitemap) {
		this.oldSitemap = oldSitemap;
		this.newSitemap = newSitemap;
	}
	
	public abstract T processEntry(SitemapUrlEntry entry, SitemapUrlEntryType type);
	
	public void process(ListTask<? super T> task) throws Exception {
		SitemapFingerprint oldFingerprint = SitemapFingerprint.compute(oldSitemap);
		SitemapFingerprint newFingerprint = SitemapFingerprint.compute(newSitemap);
		
		logger.info("Old sitemap fingerprint: {}", oldFingerprint);
		logger.info("New sitemap fingerprint: {}", newFingerprint);
		
		if(oldFingerprint.equals(newFingerprint)) {
			logger.info("Sitemaps are the same, not continuing");
			return;
		}
		
		logger.info("Sitemaps are different, continuing...");
		SitemapDiff diff = SitemapDiff.compute(oldSitemap, newSitemap);
		
		logger.info(
			"Diff: added={}, removed={}, updated={}",
			diff.added().size(),
			diff.removed().size(),
			diff.updated().size()
		);
		
		for(SitemapUrlEntry entry : diff.added()) {
			T newEntry = processEntry(entry, SitemapUrlEntryType.ADDED);
			
			if(!task.add(newEntry)) {
				break; // Exit early
			}
		}
		
		for(SitemapDiff.Change change : diff.updated()) {
			T newEntry = processEntry(change.newValue(), SitemapUrlEntryType.UPDATED);
			
			if(!task.add(newEntry)) {
				break; // Exit early
			}
		}
		
		for(SitemapUrlEntry entry : diff.removed()) {
			T newEntry = processEntry(entry, SitemapUrlEntryType.REMOVED);
			
			if(!task.add(newEntry)) {
				break; // Exit early
			}
		}
	}
}
