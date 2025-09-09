package sune.app.mediadown.index.service;

import java.net.URI;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.entity.Episode;
import sune.app.mediadown.index.entity.Identifiable;
import sune.app.mediadown.index.entity.Movie;
import sune.app.mediadown.index.entity.TVSeries;
import sune.app.mediadown.index.entity.Types;

@Service
public class IdentifiableService {

	private final EntityManager entityManager;

	public IdentifiableService(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public String toSchemaType(Identifiable identifiable) {
		if(identifiable instanceof TVSeries) return Types.TV_SERIES;
		if(identifiable instanceof Movie) return Types.MOVIE;
		if(identifiable instanceof Episode) return Types.EPISODE;
		return null;
	}
	
	public Class<?> toType(Identifiable identifiable) {
		return toType(toSchemaType(identifiable));
	}
	
	public Class<?> toType(String schemaType) {
		return switch(schemaType) {
			case Types.TV_SERIES -> TVSeries.class;
			case Types.MOVIE -> Movie.class;
			case Types.EPISODE -> Episode.class;
			default -> Object.class;
		};
	}

	@Transactional(readOnly = true)
	public <T> T getByUri(Identifiable identifiable) {
		@SuppressWarnings("unchecked")
		T result = (T) entityManager.find(toType(identifiable), identifiable.getIdentifier());
		return result;
	}
	
	@Transactional(readOnly = true)
	public <T> T getByUri(URI uri) {
		String query = """
			SELECT ?t
			WHERE {
				?x a ?t
				FILTER(?x = <?uri>)
			}
			""";
		@SuppressWarnings("unchecked")
		Object queryResult = (
			(Stream<Object>) entityManager.createNativeQuery(query)
				.setUntypedParameter("uri", uri)
				.getResultStream()
		).findFirst().orElse(null);
		
		if(queryResult == null) {
			return null;
		}
		
		String type = ((URI) queryResult).toString();
		@SuppressWarnings("unchecked")
		T result = (T) entityManager.find(toType(type), uri);
		return result;
	}
}