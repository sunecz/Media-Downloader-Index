package sune.app.mediadown.index.service;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.entity.Genre;
import sune.app.mediadown.index.entity.Types;
import sune.app.mediadown.index.model.GenreDao;
import sune.app.mediadown.index.normalization.Normalizer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

@Service
public class GenreService {

	private final GenreDao dao;
	private final Normalizer normalizer;
	private final EntityManager entityManager;

	public GenreService(GenreDao dao, Normalizer normalizer, EntityManager entityManager) {
		this.dao = dao;
		this.normalizer = normalizer;
		this.entityManager = entityManager;
	}

	private URI uri(String name) {
		return Types.mdiUri("genres/" + normalizer.normalizeUriComponent(name));
	}

	@Transactional(readOnly = true)
	public List<Genre> getAll() {
		return dao.findAll(Types.GENRE);
	}

	@Transactional(readOnly = true)
	public Genre getByUri(URI uri) {
		return dao.findByUri(uri);
	}

	@Transactional
	public Genre getOrCreateByTitle(String title) {
		URI uri = uri(normalizer.normalizeGenreName(title));

		Genre genre;
		if((genre = dao.findByUri(uri)) != null) {
			return genre;
		}

		genre = new Genre();
		genre.setUri(uri);
		genre.setTitle(normalizer.normalizeText(title));
		dao.create(genre);
		entityManager.persist(genre);
		return genre;
	}

	@Transactional
	public void update(Genre object) {
		dao.update(object);
	}
	
	@Transactional(readOnly = true)
	public List<Genre> getAllDistinct() {
		String query = """
			PREFIX mdi: <%1$s>
			SELECT DISTINCT ?x
			WHERE {
				?x a <%2$s> .
				?x mdi:name ?n .
			}
			ORDER BY ASC(?n)
			""".formatted(Types.PREFIX_MDI, Types.GENRE);
		return entityManager.createNativeQuery(query, Genre.class)
					.getResultStream()
					.toList();
	}
}