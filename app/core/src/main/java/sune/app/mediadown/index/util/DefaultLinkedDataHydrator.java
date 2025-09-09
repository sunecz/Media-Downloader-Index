package sune.app.mediadown.index.util;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import sune.app.mediadown.index.entity.Country;
import sune.app.mediadown.index.entity.Episode;
import sune.app.mediadown.index.entity.Genre;
import sune.app.mediadown.index.entity.Movie;
import sune.app.mediadown.index.entity.Person;
import sune.app.mediadown.index.entity.Program;
import sune.app.mediadown.index.entity.TVSeason;
import sune.app.mediadown.index.entity.TVSeries;
import sune.app.mediadown.index.net.HTML;
import sune.app.mediadown.index.net.Net;
import sune.app.mediadown.index.net.Web;
import sune.app.mediadown.index.net.Web.Request;
import sune.app.mediadown.index.net.Web.Response;
import sune.app.mediadown.index.normalization.Normalizer;

@Component
public class DefaultLinkedDataHydrator implements LinkedDataHydrator {

	private static final Regex REGEX_SPLIT_COUNTRIES = Regex.of("\\s*[/,]\\s*");
	
	protected final Common common;
	protected final Normalizer normalizer;
	
	protected DefaultLinkedDataHydrator(Common common, Normalizer normalizer) {
		this.common = Objects.requireNonNull(common);
		this.normalizer = Objects.requireNonNull(normalizer);
	}
	
	private static JSON.JSONCollection linkedData(Request request, String type) throws Exception {
		try(Response.OfStream response = Web.requestStream(request)) {
			final int code = response.statusCode();
			
			if(code < 200 || code >= 300) {
				return null; // Treat non-successfull HTTP status code as failure
			}
			
			return Optional.ofNullable(LinkedData.from(HTML.from(response), type))
					.map(LinkedData::data)
					.orElseGet(JSON.JSONCollection::empty);
		}
	}

	private static URI imageObject(JSON.JSONNode node) {
		if(node instanceof JSON.JSONCollection coll) {
			return Optional.ofNullable(coll.getString("url")).map(Net::uri).orElse(null);
		} else if(node instanceof JSON.JSONObject object && object.type() == JSON.JSONType.STRING) {
			return Net.uri(object.stringValue());
		} else {
			return null;
		}
	}

	private static <T, R> R nonNull(T value, Function<T, R> mapper) {
		return value == null ? null : mapper.apply(value);
	}

	private static <T> Set<T> jsonCollectionToSetAsCollections(JSON.JSONCollection collection,
			Function<JSON.JSONCollection, T> mapper) {
		return switch(collection.type()) {
			case ARRAY -> Utils.stream(collection.collectionsIterable())
								.map(mapper)
								.filter(Objects::nonNull)
								.collect(Collectors.toSet());
			case OBJECT -> nonNull(mapper.apply(collection), Set::of);
			default -> null;
		};
	}

	private static <T> Set<T> jsonCollectionToSetMultiAsCollections(JSON.JSONCollection collection,
			Function<JSON.JSONCollection, Stream<T>> mapper) {
		return switch(collection.type()) {
			case ARRAY -> Utils.stream(collection.collectionsIterable())
					.flatMap(mapper)
					.filter(Objects::nonNull)
					.collect(Collectors.toSet());
			case OBJECT -> mapper.apply(collection).filter(Objects::nonNull).collect(Collectors.toSet());
			default -> null;
		};
	}

	private static <T> Set<T> jsonCollectionToSetAsObjects(JSON.JSONNode node,
			Function<JSON.JSONObject, T> mapper) {
		if(node instanceof JSON.JSONCollection collection) {
			return switch(collection.type()) {
				case ARRAY -> Utils.stream(collection.objectsIterable())
									.map(mapper)
									.filter(Objects::nonNull)
									.collect(Collectors.toSet());
				default -> null;
			};
		}

		if(node instanceof JSON.JSONObject object) {
			return nonNull(mapper.apply(object), Set::of);
		}

		return null;
	}
	
	private static Integer getInteger(JSON.JSONCollection data, String name) {
		return Optional.ofNullable(data.getObject(name)).map(JSON.JSONObject::intValue).orElse(null);
	}

	private static Integer getInteger(JSON.JSONCollection data, String name, int defaultValue) {
		return Optional.ofNullable(data.getObject(name)).map(JSON.JSONObject::intValue).orElse(defaultValue);
	}

	private static OffsetDateTime parseDate(String date) {
		return OffsetDateTime.parse(date);
	}

	private static OffsetDateTime datePublished(JSON.JSONCollection collection) {
		JSON.JSONObject object;
		if((object = collection.getObject("datePublished")) != null
				|| (object = collection.getObject("dateCreated")) != null) {
			return parseDate(object.stringValue());
		}

		return null;
	}

	private Set<Country> countries(String name) {
		return Stream.of(REGEX_SPLIT_COUNTRIES.split(name))
					.map(common::getCountry)
					.collect(Collectors.toSet());
	}

	private Set<Country> countries(JSON.JSONNode node) {
		if(node instanceof JSON.JSONCollection coll) {
			return jsonCollectionToSetMultiAsCollections(
				coll,
				(c) -> Optional.ofNullable(coll.getString("name")).map(this::countries).map(Set::stream).orElseGet(Stream::empty)
			);
		} else if(node instanceof JSON.JSONObject object && object.type() == JSON.JSONType.STRING) {
			return countries(object.stringValue());
		} else {
			return null;
		}
	}

	private Person person(String name) {
		return common.getPerson(name);
	}

	private Set<Person> persons(JSON.JSONNode node) {
		if(node instanceof JSON.JSONCollection coll) {
			return jsonCollectionToSetAsCollections(
				coll,
				(c) -> Optional.ofNullable(coll.getString("name")).map(this::person).orElse(null)
			);
		} else {
			return null;
		}
	}

	private Genre genre(String name) {
		return common.getGenre(name);
	}

	private Set<Genre> genres(JSON.JSONNode node) {
		if(node instanceof JSON.JSONCollection coll) {
			return jsonCollectionToSetAsObjects(coll, (o) -> genre(o.stringValue()));
		} else if(node instanceof JSON.JSONObject object && object.type() == JSON.JSONType.STRING) {
			return Set.of(genre(object.stringValue()));
		} else {
			return null;
		}
	}

	private TVSeason season(TVSeries series, JSON.JSONCollection data) {
		if(series == null || data == null) {
			return null;
		}

		int number = getInteger(data, "seasonNumber", 0);
		TVSeason season = common.getTVSeason(series, number);
		boolean changed = false;

		String title = data.getString("name");
		Integer numberOfEpisodes = getInteger(data, "numberOfEpisodes");

		if(title != null && season.getTitle() == null) {
			season.setTitle(title);
			changed = true;
		}

		if(numberOfEpisodes != null && season.getNumberOfEpisodes() == null) {
			season.setNumberOfEpisodes(numberOfEpisodes);
			changed = true;
		}

		if(changed) {
			common.update(season);
		} else {
			series.addSeason(season);
		}

		return season;
	}

	private TVSeries series(JSON.JSONCollection data) {
		return data == null ? null : common.getTVSeries(Net.uri(data.getString("url")));
	}
	
	private JSON.JSONCollection linkedData(URI uri, String type) throws Exception {
		Web.Request request = createRequest(uri);
		
		if(request == null) {
			throw new IllegalArgumentException("Invalid request");
		}
		
		return linkedData(request, type);
	}
	
	// Overridable
	protected Web.Request createRequest(URI uri) {
		return Web.Request.of(uri).GET();
	}
	
	protected TVSeries hydrate(TVSeries series) throws Exception {
		JSON.JSONCollection data = linkedData(series.getUri(), "TVSeries");
		
		if(data == null) {
			return null;
		}
		
		series.setTitle(normalizer.normalizeText(data.getString("name")));
		series.setDescription(normalizer.normalizeHtmlToText(data.getString("description")));
		series.setImage(imageObject(data.get("image")));
		series.setCountries(countries(data.get("countryOfOrigin")));
		series.setActors(persons(data.get("actor")));
		series.setDirectors(persons(data.get("director")));
		series.setGenres(genres(data.get("genre")));
		return series;
	}

	protected Movie hydrate(Movie movie) throws Exception {
		JSON.JSONCollection data = linkedData(movie.getUri(), "Movie");
		
		if(data == null) {
			return null;
		}
		
		movie.setTitle(normalizer.normalizeText(data.getString("name")));
		movie.setDescription(normalizer.normalizeHtmlToText(data.getString("description")));
		movie.setImage(imageObject(data.get("image")));
		movie.setCountries(countries(data.get("countryOfOrigin")));
		movie.setActors(persons(data.get("actor")));
		movie.setDirectors(persons(data.get("director")));
		movie.setGenres(genres(data.get("genre")));
		return movie;
	}

	protected Program hydrate(Program program) throws Exception {
		if(program instanceof TVSeries series) {
			return hydrate(series);
		} else if(program instanceof Movie movie) {
			return hydrate(movie);
		}
		
		return program;
	}

	protected Episode hydrate(Episode episode) throws Exception {
		JSON.JSONCollection data = linkedData(episode.getUri(), "TVEpisode");
		
		if(data == null) {
			return null;
		}
		
		episode.setTitle(data.getString("name"));
		episode.setDescription(normalizer.normalizeHtmlToText(data.getString("description")));
		episode.setImage(imageObject(data.get("image")));
		episode.setNumber(getInteger(data, "episodeNumber", 0));
		
		TVSeries series = series(data.getCollection("partOfSeries"));
		episode.setSeries(series);
		episode.setSeason(season(series, data.getCollection("partOfSeason")));

		episode.setDuration(data.getString("duration"));
		episode.setDatePublished(datePublished(data));
		return episode;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T hydrate(T object) throws Exception {
		if(object instanceof Program program) {
			return (T) hydrate(program);
		} else if(object instanceof Episode episode) {
			return (T) hydrate(episode);
		}
		
		return object;
	}
}
