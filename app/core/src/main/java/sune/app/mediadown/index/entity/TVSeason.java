package sune.app.mediadown.index.entity;

import java.net.URI;
import java.util.Objects;

import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.Namespace;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;

@Namespace(prefix = "schema", namespace = Types.PREFIX_SCHEMA)
@OWLClass(iri = "schema:CreativeWorkSeason")
public class TVSeason implements Identifiable {

	@Id
	private URI uri;

	@OWLDataProperty(iri = "schema:name")
	private String title;

	@OWLDataProperty(iri = "schema:seasonNumber")
	private Integer number;

	@OWLObjectProperty(iri = "schema:partOfSeries")
	private TVSeries series;

	@OWLDataProperty(iri = "schema:numberOfEpisodes")
	private Integer numberOfEpisodes;

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public void setSeries(TVSeries series) {
		this.series = series;
	}

	public void setNumberOfEpisodes(Integer numberOfEpisodes) {
		this.numberOfEpisodes = numberOfEpisodes;
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

	public TVSeries getSeries() {
		return series;
	}

	public Integer getNumberOfEpisodes() {
		return numberOfEpisodes;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(number, numberOfEpisodes, title, uri);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof TVSeason other)) return false;
		return Objects.equals(number, other.number)
					&& Objects.equals(numberOfEpisodes, other.numberOfEpisodes)
					&& Objects.equals(title, other.title)
					&& Objects.equals(uri, other.uri);
	}
}