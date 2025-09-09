package sune.app.mediadown.index.model;

import org.springframework.stereotype.Repository;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.entity.Language;
import sune.app.mediadown.index.entity.Types;

@Repository
public class LanguageDao extends BaseDao<Language> {

	public LanguageDao(EntityManager em) {
		super(em);
	}

	public Language findByTitle(String title) {
		String query = """
			PREFIX schema: <%1$s>
			SELECT ?x
			WHERE {
				?x a <%2$s> .
				?x schema:name ?name .
			}
			""".formatted(Types.PREFIX_SCHEMA, Types.LANGUAGE);
		return em.createNativeQuery(query, clazz)
					.setParameter("name", title)
					.getResultStream()
					.findFirst().orElse(null);
	}
}