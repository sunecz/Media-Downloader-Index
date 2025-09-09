package sune.app.mediadown.index.model;

import org.springframework.stereotype.Repository;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.entity.Genre;
import sune.app.mediadown.index.entity.Types;

@Repository
public class GenreDao extends BaseDao<Genre> {

	public GenreDao(EntityManager em) {
		super(em);
	}

	public Genre findByTitle(String title) {
		String query = """
			PREFIX schema: <%1$s>
			SELECT ?x
			WHERE {
				?x a <%2$s> .
				?x schema:name ?name .
			}
			""".formatted(Types.PREFIX_SCHEMA, Types.GENRE);
		return em.createNativeQuery(query, clazz)
				.setParameter("name", title)
				.getResultStream()
				.findFirst().orElse(null);
	}
}