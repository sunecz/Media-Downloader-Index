package sune.app.mediadown.index.entity;

import java.net.URI;
import java.util.Objects;
import java.util.Set;

import cz.cvut.kbss.jopa.model.annotations.FetchType;
import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.Namespace;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import sune.app.mediadown.index.util.DatabaseDataStorable;

@Namespace(prefix = "schema", namespace = Types.PREFIX_SCHEMA)
@OWLClass(iri = "schema:VideoObject")
public class Audio extends DatabaseDataStorable implements Identifiable {

	@Id
	private URI uri;

	@OWLDataProperty(iri = "schema:bitrate")
	private Long bitrate;

	@OWLDataProperty(iri = "schema:contentSize")
	private Long contentSize;

	@OWLObjectProperty(iri = "schema:contentUrl")
	private URI contentUrl;

	@OWLDataProperty(iri = "schema:duration")
	private String duration;

	@OWLDataProperty(iri = "schema:encodingFormat")
	private String format;

	@OWLObjectProperty(iri = "schema:inLanguage", fetch = FetchType.EAGER)
	private Set<Language> languages;

	@OWLObjectProperty(iri = "schema:regionsAllowed", fetch = FetchType.EAGER)
	private Set<Country> regions;

	@OWLDataProperty(iri = "schema:requiresSubscription")
	private Boolean requiresSubscription;

	@OWLDataProperty(iri = "schema:videoQuality")
	private String quality;

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public void setBitrate(Long bitrate) {
		this.bitrate = bitrate;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setLanguages(Set<Language> languages) {
		this.languages = languages;
	}

	public void setRequiresSubscription(Boolean requiresSubscription) {
		this.requiresSubscription = requiresSubscription;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public void setRegions(Set<Country> regions) {
		this.regions = regions;
	}

	public void setContentUrl(URI contentUrl) {
		this.contentUrl = contentUrl;
	}

	public void setContentSize(Long contentSize) {
		this.contentSize = contentSize;
	}

	@Override
	public URI getIdentifier() {
		return getUri();
	}

	public URI getUri() {
		return uri;
	}

	public Long getBitrate() {
		return bitrate;
	}

	public Long getContentSize() {
		return contentSize;
	}

	public URI getContentUrl() {
		return contentUrl;
	}

	public String getDuration() {
		return duration;
	}

	public String getFormat() {
		return format;
	}

	public Set<Language> getLanguages() {
		return languages;
	}

	public Set<Country> getRegions() {
		return regions;
	}

	public Boolean getRequiresSubscription() {
		return requiresSubscription;
	}

	public String getQuality() {
		return quality;
	}

	@Override
	public int hashCode() {
		return Objects.hash(bitrate, contentSize, contentUrl, duration, format, languages, quality,
				regions, requiresSubscription, uri);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof Audio other)) return false;
		return Objects.equals(bitrate, other.bitrate)
					&& Objects.equals(contentSize, other.contentSize)
					&& Objects.equals(contentUrl, other.contentUrl)
					&& Objects.equals(duration, other.duration)
					&& Objects.equals(format, other.format)
					&& Objects.equals(languages, other.languages)
					&& Objects.equals(quality, other.quality)
					&& Objects.equals(regions, other.regions)
					&& Objects.equals(requiresSubscription, other.requiresSubscription)
					&& Objects.equals(uri, other.uri);
	}
}