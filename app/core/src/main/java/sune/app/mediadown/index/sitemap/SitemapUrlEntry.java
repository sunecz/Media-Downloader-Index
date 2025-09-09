package sune.app.mediadown.index.sitemap;

import java.util.Objects;

public final record SitemapUrlEntry(String loc, String lastmod) {
	
	public SitemapUrlEntry(String loc, String lastmod) {
		this.loc = Objects.requireNonNull(loc);
		this.lastmod = Objects.requireNonNull(lastmod);
	}
	
	public static final class Builder {
		
		private String loc;
		private String lastmod;
		
		public Builder() {
		}
		
		public Builder reset() {
			loc = null;
			lastmod = null;
			return this;
		}
		
		public SitemapUrlEntry build() {
			return new SitemapUrlEntry(loc, lastmod);
		}
		
		public Builder setLoc(String loc) {
			this.loc = loc;
			return this;
		}
		
		public Builder setLastmod(String lastmod) {
			this.lastmod = lastmod;
			return this;
		}
	}
}
