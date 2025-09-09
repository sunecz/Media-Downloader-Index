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
public class Video extends DatabaseDataStorable implements Identifiable {

	@Id
	private URI uri;

	@OWLDataProperty(iri = "schema:width")
	private Integer width;

	@OWLDataProperty(iri = "schema:height")
	private Integer height;

	@OWLObjectProperty(iri = "schema:audio")
	private Audio audio;

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

	public void setAudio(Audio audio) {
		this.audio = audio;
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

	public void setHeight(Integer height) {
		this.height = height;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	@Override
	public URI getIdentifier() {
		return getUri();
	}

	public URI getUri() {
		return uri;
	}

	public Integer getWidth() {
		return width;
	}

	public Integer getHeight() {
		return height;
	}

	public Audio getAudio() {
		return audio;
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
		return Objects.hash(audio, bitrate, contentSize, contentUrl, duration, format, height,
				languages, quality, regions, requiresSubscription, uri, width);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof Video other)) return false;
		return Objects.equals(audio, other.audio)
					&& Objects.equals(bitrate, other.bitrate)
					&& Objects.equals(contentSize, other.contentSize)
					&& Objects.equals(contentUrl, other.contentUrl)
					&& Objects.equals(duration, other.duration)
					&& Objects.equals(format, other.format)
					&& Objects.equals(height, other.height)
					&& Objects.equals(languages, other.languages)
					&& Objects.equals(quality, other.quality)
					&& Objects.equals(regions, other.regions)
					&& Objects.equals(requiresSubscription, other.requiresSubscription)
					&& Objects.equals(uri, other.uri)
					&& Objects.equals(width, other.width);
	}
}