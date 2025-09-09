package sune.app.mediadown.index.util;

import java.net.URI;

import sune.app.mediadown.index.entity.*;

public interface Common {

	TVSeries getTVSeries(URI uri);
	Country getCountry(String name);
	Person getPerson(String name);
	Language getLanguage(String name);
	Genre getGenre(String name);
	TVSeason getTVSeason(TVSeries series, int number);

	void update(TVSeries object);
	void update(Country object);
	void update(Person object);
	void update(Language object);
	void update(Genre object);
	void update(TVSeason object);
}