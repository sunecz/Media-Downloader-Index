package sune.app.mediadown.index.entity;

import java.net.URI;
import java.util.Objects;

import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.Namespace;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;

// Note: There is no Genre type on schema.org
@Namespace(prefix = "mdi", namespace = Types.PREFIX_MDI)
@OWLClass(iri = "mdi:Genre")
public class Genre implements Identifiable {

	@Id
	private URI uri;

	@OWLDataProperty(iri = "mdi:slug")
	private String name;

	@OWLDataProperty(iri = "mdi:name")
	private String title;

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public URI getIdentifier() {
		return getUri();
	}

	public URI getUri() {
		return uri;
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, title, uri);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof Genre other)) return false;
		return Objects.equals(name, other.name) && Objects.equals(title, other.title) && Objects.equals(uri, other.uri);
	}
}