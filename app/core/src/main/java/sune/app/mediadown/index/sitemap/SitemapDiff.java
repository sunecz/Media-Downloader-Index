package sune.app.mediadown.index.sitemap;

import java.util.ArrayList;
import java.util.List;

public final record SitemapDiff(
	List<SitemapUrlEntry> added,
	List<SitemapUrlEntry> removed,
	List<Change> updated
) {
	
	public static final SitemapDiff compute(Sitemap prev, Sitemap curr) {
		List<SitemapUrlEntry> added = new ArrayList<>();
		List<SitemapUrlEntry> removed = new ArrayList<>();
		List<Change> updated = new ArrayList<>();
		
		for(SitemapUrlEntry currEntry : curr) {
			SitemapUrlEntry prevEntry = prev.get(currEntry.loc());
			
			if(prevEntry == null) {
				added.add(currEntry);
			} else if(!prevEntry.equals(currEntry)) {
				updated.add(new Change(prevEntry, currEntry));
			}
		}
		
		for(SitemapUrlEntry prevEntry : prev) {
			SitemapUrlEntry currEntry = curr.get(prevEntry.loc());
			
			if(currEntry == null) {
				removed.add(prevEntry);
			}
		}
		
		return new SitemapDiff(added, removed, updated);
	}
	
	public static final record Change(SitemapUrlEntry oldValue, SitemapUrlEntry newValue) {
	}
}
