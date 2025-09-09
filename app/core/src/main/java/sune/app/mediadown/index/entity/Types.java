package sune.app.mediadown.index.entity;

import java.net.URI;

import sune.app.mediadown.index.net.Net;

public final class Types {

	public static final String PREFIX_MDI = "https://schema.mdi.sune.app/";
	public static final String PREFIX_SCHEMA = "https://schema.org/";

	public static final String URI_MDI = "https://data.mdi.sune.app/";
	public static final String URI_SCHEMA = "https://schema.org/";
	
	public static final String TV_SERIES = PREFIX_SCHEMA + "TVSeries";
	public static final String MOVIE = PREFIX_SCHEMA + "Movie";
	public static final String TV_SEASON = PREFIX_SCHEMA + "CreativeWorkSeason";
	public static final String EPISODE = PREFIX_SCHEMA + "TVEpisode";
	public static final String COUNTRY = PREFIX_SCHEMA + "Country";
	public static final String PERSON = PREFIX_SCHEMA + "Person";
	public static final String LANGUAGE = PREFIX_SCHEMA + "Language";
	public static final String GENRE = PREFIX_MDI + "Genre";

	private Types() {
	}

	public static URI schemaUri(String path) {
		return Net.uri(URI_SCHEMA + path);
	}

	public static URI mdiUri(String path) {
		return Net.uri(URI_MDI + path);
	}
}