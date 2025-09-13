package sune.app.mediadown.index.entity;

import java.net.URI;
import java.util.Objects;

import cz.cvut.kbss.jopa.model.annotations.Context;
import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.Namespace;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;

@Namespace(prefix = "mdig", namespace = Types.PREFIX_MDI_GRAPH)
@Context(Types.URI_DEFAULT_GRAPH + "#meta")
@OWLClass(iri = "mdig:meta")
public class GraphMeta implements Identifiable {
	
	public static final URI ID = URI.create(Types.URI_DEFAULT_GRAPH);
	
	@Id
	private URI id = ID;
	
	@OWLDataProperty(iri = "mdig:version")
	private Integer version;
	
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Override
	public URI getIdentifier() {
		return id;
	}
	
	public URI getId() {
		return id;
	}
	
	public Integer getVersion() {
		return version;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, version);
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj || (
			obj instanceof GraphMeta other
				&& Objects.equals(id, other.id)
				&& Objects.equals(version, other.version)
		);
	}
}
