package sune.app.mediadown.index.util;

import java.net.URI;

import org.springframework.stereotype.Component;

import sune.app.mediadown.index.entity.Country;
import sune.app.mediadown.index.entity.Genre;
import sune.app.mediadown.index.entity.Language;
import sune.app.mediadown.index.entity.Person;
import sune.app.mediadown.index.entity.TVSeason;
import sune.app.mediadown.index.entity.TVSeries;
import sune.app.mediadown.index.service.CountryService;
import sune.app.mediadown.index.service.GenreService;
import sune.app.mediadown.index.service.LanguageService;
import sune.app.mediadown.index.service.PersonService;
import sune.app.mediadown.index.service.TVSeasonService;
import sune.app.mediadown.index.service.TVSeriesService;

@Component("common")
public final class DefaultCommon implements Common {

	private final TVSeriesService tvSeriesService;
	private final CountryService countryService;
	private final PersonService personService;
	private final LanguageService languageService;
	private final GenreService genreService;
	private final TVSeasonService tvSeasonService;

	DefaultCommon(
		TVSeriesService tvSeriesService,
		CountryService countryService,
		PersonService personService,
		LanguageService languageService,
		GenreService genreService,
		TVSeasonService tvSeasonService
	) {
		this.tvSeriesService = tvSeriesService;
		this.countryService = countryService;
		this.personService = personService;
		this.languageService = languageService;
		this.genreService = genreService;
		this.tvSeasonService = tvSeasonService;
	}

	@Override
	public TVSeries getTVSeries(URI uri) {
		return tvSeriesService.getByUri(uri);
	}

	@Override
	public Country getCountry(String name) {
		return countryService.getOrCreateByName(name);
	}

	@Override
	public Person getPerson(String name) {
		return personService.getOrCreateByName(name);
	}

	@Override
	public Language getLanguage(String name) {
		return languageService.getOrCreateByTitle(name);
	}

	@Override
	public Genre getGenre(String name) {
		return genreService.getOrCreateByTitle(name);
	}

	@Override
	public TVSeason getTVSeason(TVSeries series, int number) {
		return tvSeasonService.getBySeriesOrCreate(series, number);
	}

	@Override
	public void update(TVSeries object) {
		tvSeriesService.update(object);
	}

	@Override
	public void update(Country object) {
		countryService.update(object);
	}

	@Override
	public void update(Person object) {
		personService.update(object);
	}

	@Override
	public void update(Language object) {
		languageService.update(object);
	}

	@Override
	public void update(Genre object) {
		genreService.update(object);
	}

	@Override
	public void update(TVSeason object) {
		tvSeasonService.update(object);
	}
}
