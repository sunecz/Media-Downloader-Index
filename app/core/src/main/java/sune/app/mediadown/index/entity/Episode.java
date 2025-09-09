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
import cz.cvut.kbss.jopa.model.annotations.Namespaces;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import sune.app.mediadown.index.util.DatabaseDataStorable;

@Namespaces({
	@Namespace(prefix = "schema", namespace = Types.PREFIX_SCHEMA),
	@Namespace(prefix = "mdi", namespace = Types.PREFIX_MDI)
})
@OWLClass(iri = "schema:TVEpisode")
public class Episode extends DatabaseDataStorable implements HasMedia, Entity {
	
	@Id
	private URI uri;
	
	@OWLDataProperty(iri = "schema:name")
	private String title;

	@OWLDataProperty(iri = "schema:description")
	private String description;

	@OWLObjectProperty(iri = "schema:image")
	private URI image;
	
	@OWLDataProperty(iri = "schema:episodeNumber")
	private Integer number;

	@OWLObjectProperty(iri = "schema:partOfSeason")
	private TVSeason season;

	@OWLObjectProperty(iri = "schema:partOfSeries")
	private TVSeries series;

	@OWLDataProperty(iri = "schema:duration")
	private String duration;

	@OWLDataProperty(iri = "schema:datePublished")
	private OffsetDateTime datePublished;

	@OWLObjectProperty(iri = "schema:video", fetch = FetchType.EAGER)
	private Set<Video> videos;

	@OWLDataProperty(iri = "mdi:seasonNumber")
	private Integer seasonNumber;

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public void setSeries(TVSeries series) {
		this.series = series;
	}

	public void setSeason(TVSeason season) {
		this.season = season;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public void setSeasonNumber(Integer seasonNumber) {
		this.seasonNumber = seasonNumber;
	}

	public void setImage(URI image) {
		this.image = image;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDatePublished(OffsetDateTime datePublished) {
		this.datePublished = datePublished;
	}

	@Override
	public void addVideo(Video video) {
		if(videos == null) {
			videos = new LinkedHashSet<>();
		}

		videos.add(video);
	}

	@Override
	public void removeVideo(Video video) {
		if(videos == null) {
			return;
		}

		videos.remove(video);
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
	public URI getIdentifier() {
		return getUri();
	}

	public URI getUri() {
		return uri;
	}
	
	public String getTitle() {
		return title;
	}
	
	public Integer getNumber() {
		return number;
	}

	public Integer getSeasonNumber() {
		return seasonNumber;
	}

	public TVSeason getSeason() {
		return season;
	}
	
	public TVSeries getSeries() {
		return series;
	}

	public String getDescription() {
		return description;
	}

	public URI getImage() {
		return image;
	}

	public String getDuration() {
		return duration;
	}

	public OffsetDateTime getDatePublished() {
		return datePublished;
	}

	@Override
	public Set<Video> getVideos() {
		return videos;
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
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof Episode episode)) return false;
		return Objects.equals(uri, episode.uri)
					&& Objects.equals(title, episode.title)
					&& Objects.equals(description, episode.description)
					&& Objects.equals(image, episode.image)
					&& Objects.equals(number, episode.number)
					&& Objects.equals(seasonNumber, episode.seasonNumber)
					&& Objects.equals(season, episode.season)
					&& Objects.equals(series, episode.series)
					&& Objects.equals(duration, episode.duration)
					&& Objects.equals(datePublished, episode.datePublished)
					&& Objects.equals(videos, episode.videos);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uri, title, description, image, number, seasonNumber, season, series,
				duration, datePublished, videos);
	}
}