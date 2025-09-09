package sune.app.mediadown.index.model;

import org.springframework.stereotype.Repository;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.entity.Country;
import sune.app.mediadown.index.entity.Types;

@Repository
public class CountryDao extends BaseDao<Country> {

	public CountryDao(EntityManager em) {
		super(em);
	}
	
	public Country findByTitle(String title) {
		String query = """
			PREFIX schema: <%1$s>
			SELECT ?x
			WHERE {
				?x a <%2$s> .
				?x schema:name ?title .
			}
			""".formatted(Types.PREFIX_SCHEMA, Types.COUNTRY);
		return em.createNativeQuery(query, clazz)
					.setParameter("title", title)
					.getResultStream()
					.findFirst().orElse(null);
	}
}