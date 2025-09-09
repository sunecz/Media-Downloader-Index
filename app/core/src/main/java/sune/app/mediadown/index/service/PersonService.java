package sune.app.mediadown.index.service;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.entity.Person;
import sune.app.mediadown.index.entity.Types;
import sune.app.mediadown.index.model.PersonDao;
import sune.app.mediadown.index.normalization.Normalizer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

@Service
public class PersonService {

	private final PersonDao dao;
	private final Normalizer normalizer;
	private final EntityManager entityManager;

	public PersonService(PersonDao dao, Normalizer normalizer, EntityManager entityManager) {
		this.dao = dao;
		this.normalizer = normalizer;
		this.entityManager = entityManager;
	}

	private URI uri(String name) {
		return Types.mdiUri("people/" + normalizer.normalizeUriComponent(name));
	}

	@Transactional(readOnly = true)
	public List<Person> getAll() {
		return dao.findAll(Types.PERSON);
	}

	@Transactional(readOnly = true)
	public Person getByUri(URI uri) {
		return dao.findByUri(uri);
	}

	@Transactional
	public Person getOrCreateByName(String name) {
		URI uri = uri(normalizer.normalizePersonName(name));

		Person person;
		if((person = dao.findByUri(uri)) != null) {
			return person;
		}

		person = new Person();
		person.setUri(uri);
		person.setName(normalizer.normalizeText(name));
		dao.create(person);
		entityManager.persist(person);
		return person;
	}

	@Transactional
	public void update(Person object) {
		dao.update(object);
	}
	
	@Transactional(readOnly = true)
	public List<Person> getAllDistinct() {
		String query = """
			PREFIX schema: <%1$s>
			SELECT DISTINCT ?v
			WHERE {
				?x schema:actor ?v .
				?x schema:name ?n
			}
			ORDER BY ASC(?n)
			""".formatted(Types.PREFIX_SCHEMA);
		return entityManager.createNativeQuery(query, Person.class)
					.getResultStream()
					.toList();
	}
}