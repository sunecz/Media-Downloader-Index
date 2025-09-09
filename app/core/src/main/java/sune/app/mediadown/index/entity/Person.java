package sune.app.mediadown.index.entity;

import java.net.URI;
import java.util.Objects;

import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.Namespace;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;

@Namespace(prefix = "schema", namespace = Types.PREFIX_SCHEMA)
@OWLClass(iri = "schema:Person")
public class Person implements Identifiable {

	@Id
	private URI uri;

	@OWLDataProperty(iri = "schema:name")
	private String name;

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public void setName(String name) {
		this.name = name;
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

	@Override
	public int hashCode() {
		return Objects.hash(name, uri);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof Person other)) return false;
		return Objects.equals(name, other.name) && Objects.equals(uri, other.uri);
	}
}