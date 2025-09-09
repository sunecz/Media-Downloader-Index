package sune.app.mediadown.index.service;

import java.net.URI;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.entity.Episode;
import sune.app.mediadown.index.entity.Types;
import sune.app.mediadown.index.model.EpisodeDao;
import sune.app.mediadown.index.util.JavaScript;

@Service
public class EpisodeService {

	private final EpisodeDao dao;
	private final EntityManager entityManager;

	public EpisodeService(EpisodeDao dao, EntityManager entityManager) {
		this.dao = dao;
		this.entityManager = entityManager;
	}

	@Transactional(readOnly = true)
	public List<Episode> getAll() {
		return dao.findAll(Types.EPISODE);
	}

	@Transactional(readOnly = true)
	public Episode getByUri(URI uri) {
		return dao.findByUri(uri);
	}

	@Transactional(readOnly = true)
	protected Episode findByUri(String uri) {
		String query = """
			PREFIX schema: <%1$s>
			SELECT ?x
			WHERE {
				?x a <%2$s> .
				FILTER(REPLACE(STR(?x), "^.*?://", "") = ?uri)
			}
			""".formatted(Types.PREFIX_SCHEMA, Types.EPISODE);
		return entityManager.createNativeQuery(query, Episode.class)
				.setParameter("uri", uri)
				.getResultStream()
				.findFirst().orElse(null);
	}

	@Transactional(readOnly = true)
	public Episode getBySlug(String slug) {
		slug = JavaScript.decodeURIComponent(slug);
		return findByUri(slug);
	}
}