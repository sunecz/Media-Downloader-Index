package sune.app.mediadown.index.entity;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import cz.cvut.kbss.jopa.model.annotations.FetchType;
import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.Namespace;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import sune.app.mediadown.index.util.DatabaseDataStorable;

@Namespace(prefix = "schema", namespace = Types.PREFIX_SCHEMA)
@OWLClass(iri = "schema:Movie")
public class Movie extends DatabaseDataStorable implements Program, HasMedia {

	@Id
	private URI uri;

	@OWLDataProperty(iri = "schema:name")
	private String title;

	@OWLDataProperty(iri = "schema:description")
	private String description;

	@OWLObjectProperty(iri = "schema:image")
	private URI image;

	@OWLObjectProperty(iri = "schema:countryOfOrigin", fetch = FetchType.EAGER)
	private Set<Country> countries;

	@OWLObjectProperty(iri = "schema:actor", fetch = FetchType.EAGER)
	private Set<Person> actors;

	@OWLObjectProperty(iri = "schema:director", fetch = FetchType.EAGER)
	private Set<Person> directors;

	@OWLObjectProperty(iri = "schema:genre", fetch = FetchType.EAGER)
	private Set<Genre> genres;

	@OWLObjectProperty(iri = "schema:video", fetch = FetchType.EAGER)
	private Set<Video> videos;

	@OWLDataProperty(iri = "schema:duration")
	private String duration;

	@OWLDataProperty(iri = "schema:datePublished")
	private OffsetDateTime datePublished;

	@Override
	public void setUri(URI uri) {
		this.uri = uri;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setImage(URI image) {
		this.image = image;
	}

	@Override
	public void addCountry(Country country) {
		if(countries == null) {
			countries = new LinkedHashSet<>();
		}

		countries.add(country);
	}

	@Override
	public void addActor(Person person) {
		if(actors == null) {
			actors = new LinkedHashSet<>();
		}

		actors.add(person);
	}

	@Override
	public void addDirector(Person person) {
		if(directors == null) {
			directors = new LinkedHashSet<>();
		}

		directors.add(person);
	}

	@Override
	public void addGenre(Genre genre) {
		if(genres == null) {
			genres = new LinkedHashSet<>();
		}

		genres.add(genre);
	}

	@Override
	public void addVideo(Video video) {
		if(videos == null) {
			videos = new LinkedHashSet<>();
		}

		videos.add(video);
	}

	@Override
	public void removeCountry(Country country) {
		if(countries == null) {
			return;
		}

		countries.remove(country);
	}

	@Override
	public void removeActor(Person person) {
		if(actors == null) {
			return;
		}

		actors.remove(person);
	}

	@Override
	public void removeDirector(Person person) {
		if(directors == null) {
			return;
		}

		directors.remove(person);
	}

	@Override
	public void removeGenre(Genre genre) {
		if(genres == null) {
			return;
		}

		genres.remove(genre);
	}

	@Override
	public void removeVideo(Video video) {
		if(videos == null) {
			return;
		}

		videos.remove(video);
	}

	public void setCountries(Set<Country> countries) {
		this.countries = countries;
	}

	public void setActors(Set<Person> actors) {
		this.actors = actors;
	}

	public void setDirectors(Set<Person> directors) {
		this.directors = directors;
	}

	public void setGenres(Set<Genre> genres) {
		this.genres = genres;
	}

	public void setVideos(Set<Video> videos) {
		this.videos = videos;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public void setDatePublished(OffsetDateTime datePublished) {
		this.datePublished = datePublished;
	}

	@Override
	public void setIsActive(boolean value) {
		setSingle("active", Boolean.toString(value));
	}

	@Override
	public void setChangedDate(OffsetDateTime dateTime) {
		setSingle("changedDate", dateTime.toString());
	}
	
	@Override
	public void setSource(String source) {
		setSingle("source", source);
	}

	@Override
	public URI getUri() {
		return uri;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public URI getImage() {
		return image;
	}

	@Override
	public Set<Country> getCountries() {
		return countries;
	}

	@Override
	public Set<Person> getActors() {
		return actors;
	}

	@Override
	public Set<Person> getDirectors() {
		return directors;
	}

	@Override
	public Set<Genre> getGenres() {
		return genres;
	}

	@Override
	public Set<Video> getVideos() {
		return videos;
	}

	public String getDuration() {
		return duration;
	}

	public OffsetDateTime getDatePublished() {
		return datePublished;
	}

	@Override
	public boolean isActive() {
		return Boolean.parseBoolean(getSingle("active", "true"));
	}

	@Override
	public OffsetDateTime getChangedDate() {
		return Optional.ofNullable(getSingle("changedDate")).map(OffsetDateTime::parse).orElseGet(OffsetDateTime::now);
	}
	
	@Override
	public String getSource() {
		return getSingle("source");
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof Movie movie)) return false;
		return Objects.equals(uri, movie.uri)
					&& Objects.equals(title, movie.title)
					&& Objects.equals(description, movie.description)
					&& Objects.equals(image, movie.image)
					&& Objects.equals(countries, movie.countries)
					&& Objects.equals(actors, movie.actors)
					&& Objects.equals(directors, movie.directors)
					&& Objects.equals(genres, movie.genres)
					&& Objects.equals(videos, movie.videos)
					&& Objects.equals(duration, movie.duration)
					&& Objects.equals(datePublished, movie.datePublished);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uri, title, description, image, countries, actors, directors, genres,
				videos, duration, datePublished);
	}
}