package sune.app.mediadown.index.model;

import org.springframework.stereotype.Repository;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.entity.Person;
import sune.app.mediadown.index.entity.Types;

@Repository
public class PersonDao extends BaseDao<Person> {

	public PersonDao(EntityManager em) {
		super(em);
	}

	public Person findByName(String name) {
		String query = """
			PREFIX schema: <%1$s>
			SELECT ?x
			WHERE {
				?x a <%2$s> .
				?x schema:name ?name .
			}
			""".formatted(Types.PREFIX_SCHEMA, Types.PERSON);
		return em.createNativeQuery(query, clazz)
				.setParameter("name", name)
				.getResultStream()
				.findFirst().orElse(null);
	}
}