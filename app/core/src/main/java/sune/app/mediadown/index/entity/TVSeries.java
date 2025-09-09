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
@OWLClass(iri = "schema:TVSeries")
public class TVSeries extends DatabaseDataStorable implements Program {

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

	@OWLDataProperty(iri = "schema:startDate")
	private OffsetDateTime startDate;

	@OWLDataProperty(iri = "schema:endDate")
	private OffsetDateTime endDate;

	@OWLDataProperty(iri = "schema:numberOfSeasons")
	private Integer numberOfSeasons;

	@OWLObjectProperty(iri = "schema:season", fetch = FetchType.EAGER)
	private Set<TVSeason> seasons;

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

	public void addSeason(TVSeason season) {
		if(seasons == null) {
			seasons = new LinkedHashSet<>();
		}

		seasons.add(season);
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

	public void removeSeason(TVSeason season) {
		if(seasons == null) {
			return;
		}

		seasons.remove(season);
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

	public void setStartDate(OffsetDateTime startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(OffsetDateTime endDate) {
		this.endDate = endDate;
	}

	public void setNumberOfSeasons(Integer numberOfSeasons) {
		this.numberOfSeasons = numberOfSeasons;
	}

	public void setSeasons(Set<TVSeason> seasons) {
		this.seasons = seasons;
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

	public OffsetDateTime getStartDate() {
		return startDate;
	}

	public OffsetDateTime getEndDate() {
		return endDate;
	}

	public Integer getNumberOfSeasons() {
		return numberOfSeasons;
	}

	public Set<TVSeason> getSeasons() {
		return seasons;
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
		if(!(o instanceof TVSeries tvSeries)) return false;
		return Objects.equals(uri, tvSeries.uri)
					&& Objects.equals(title, tvSeries.title)
					&& Objects.equals(description, tvSeries.description)
					&& Objects.equals(image, tvSeries.image)
					&& Objects.equals(countries, tvSeries.countries)
					&& Objects.equals(actors, tvSeries.actors)
					&& Objects.equals(directors, tvSeries.directors)
					&& Objects.equals(genres, tvSeries.genres)
					&& Objects.equals(startDate, tvSeries.startDate)
					&& Objects.equals(endDate, tvSeries.endDate)
					&& Objects.equals(numberOfSeasons, tvSeries.numberOfSeasons)
					&& Objects.equals(seasons, tvSeries.seasons);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uri, title, description, image, countries, actors, directors, genres,
				startDate, endDate, numberOfSeasons, seasons);
	}
}