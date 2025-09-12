package sune.app.mediadown.index.plugin.iprima;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.zip.GZIPInputStream;

import sune.app.mediadown.index.entity.Movie;
import sune.app.mediadown.index.entity.Program;
import sune.app.mediadown.index.entity.TVSeries;
import sune.app.mediadown.index.net.Net;
import sune.app.mediadown.index.net.Web;
import sune.app.mediadown.index.net.Web.Request;
import sune.app.mediadown.index.net.Web.Response;
import sune.app.mediadown.index.plugin.iprima.IPrimaWebsite.IPrima;
import sune.app.mediadown.index.sitemap.Sitemap;
import sune.app.mediadown.index.sitemap.SitemapFileRotator;
import sune.app.mediadown.index.sitemap.SitemapProcessor;
import sune.app.mediadown.index.sitemap.SitemapUrlEntry;
import sune.app.mediadown.index.task.ListTask;
import sune.app.mediadown.index.util.Storage;

final class PrimaPlus implements IPrima {

	private static final URI URI_SITEMAP_MOVIES = Net.uri("https://www.iprima.cz/sitemap-movie.xml");
	private static final URI URI_SITEMAP_TVSERIES = Net.uri("https://www.iprima.cz/sitemap-series.xml");
	
	private IPrimaWebsite context;
	
	PrimaPlus(IPrimaWebsite context) {
		this.context = context;
	}
	
	private final <T> void processSitemap(
		String name,
		URI sitemapUri,
		BiFunction<Sitemap, Sitemap, SitemapProcessor<T>> ctor,
		ListTask<? super T> task
	) throws Exception {
		Path storageDir = Storage.directory().resolve("plugin/iprima/primaplus");
		
		try(SitemapFileRotator rotator = new SitemapFileRotator(storageDir, name)) {
			Sitemap oldSitemap = Sitemap.of(rotator.previousFile());
			Sitemap newSitemap;
			
			Request request = Requests.of(sitemapUri)
				.header("Accept-Encoding", "gzip")
				.GET();
			
			try(Response.OfStream response = Web.requestStream(request)) {
				InputStream stream = response.stream();
				
				String encoding = response.headers()
					.firstValue("Content-Encoding")
					.map(String::toLowerCase)
					.orElse(null);
				
				if("gzip".equals(encoding)) {
					stream = new GZIPInputStream(stream);
				}
				
				newSitemap = Sitemap.of(stream, rotator.currentFile());
			}
			
			SitemapProcessor<T> processor = ctor.apply(oldSitemap, newSitemap);
			processor.process(task);
			
			rotator.rotate();
		}
	}
	
	@Override
	public ListTask<Program> getPrograms() throws Exception {
		return ListTask.of(PrimaCommon.handleErrors((task) -> {
			processSitemap(
				"sitemap-movie",
				URI_SITEMAP_MOVIES,
				MovieSitemapProcessor::new,
				task
			);
			
			processSitemap(
				"sitemap-series",
				URI_SITEMAP_TVSERIES,
				TVSeriesSitemapProcessor::new,
				task
			);
		}));
	}
	
	private final class MovieSitemapProcessor extends SitemapProcessor<Movie> {
		
		public MovieSitemapProcessor(Sitemap oldSitemap, Sitemap newSitemap) {
			super(oldSitemap, newSitemap);
		}
		
		@Override
		public Movie processEntry(SitemapUrlEntry entry, SitemapUrlEntryType type) {
			Movie movie = new Movie();
			movie.setSource(context.name());
			movie.setUri(Net.uri(entry.loc()));
			
			boolean isActive = type != SitemapUrlEntryType.REMOVED;
			movie.setIsActive(isActive);
			
			return movie;
		}
	}
	
	private final class TVSeriesSitemapProcessor extends SitemapProcessor<TVSeries> {
		
		public TVSeriesSitemapProcessor(Sitemap oldSitemap, Sitemap newSitemap) {
			super(oldSitemap, newSitemap);
		}
		
		@Override
		public TVSeries processEntry(SitemapUrlEntry entry, SitemapUrlEntryType type) {
			TVSeries series = new TVSeries();
			series.setSource(context.name());
			series.setUri(Net.uri(entry.loc()));
			
			boolean isActive = type != SitemapUrlEntryType.REMOVED;
			series.setIsActive(isActive);
			
			return series;
		}
	}
}
