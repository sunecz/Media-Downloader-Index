package sune.app.mediadown.index.entity;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.Namespace;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.Properties;

@Namespace(prefix = "schema", namespace = Types.PREFIX_SCHEMA)
@OWLClass(iri = "schema:Language")
public class Language implements Identifiable {

	@Id
	private URI uri;

	@OWLDataProperty(iri = "schema:name")
	private String title;

	@Properties
	protected Map<String, Set<String>> data;

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public void setCode(String code) {
		data.put("code", Set.of(code));
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

	public String getCode() {
		return Optional.ofNullable((Set<String>) data.get("code")).map((v) -> v.iterator().next()).orElse(null);
	}

	public String getTitle() {
		return title;
	}

	@Override
	public int hashCode() {
		return Objects.hash(data, title, uri);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof Language other)) return false;
		return Objects.equals(data, other.data) && Objects.equals(title, other.title) && Objects.equals(uri, other.uri);
	}
}