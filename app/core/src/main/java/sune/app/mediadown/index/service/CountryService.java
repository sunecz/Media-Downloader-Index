package sune.app.mediadown.index.service;

import java.net.URI;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.entity.Country;
import sune.app.mediadown.index.entity.Types;
import sune.app.mediadown.index.model.CountryDao;
import sune.app.mediadown.index.normalization.Normalizer;

@Service
public class CountryService {

	private final CountryDao dao;
	private final Normalizer normalizer;
	private final EntityManager entityManager;

	public CountryService(CountryDao dao, Normalizer normalizer, EntityManager entityManager) {
		this.dao = dao;
		this.normalizer = normalizer;
		this.entityManager = entityManager;
	}

	private URI uri(String name) {
		return Types.mdiUri("countries/" + normalizer.normalizeUriComponent(name));
	}

	@Transactional(readOnly = true)
	public List<Country> getAll() {
		return dao.findAll(Types.COUNTRY);
	}

	@Transactional(readOnly = true)
	public Country getByUri(URI uri) {
		return dao.findByUri(uri);
	}

	@Transactional
	public Country getOrCreateByName(String name) {
		URI uri = uri(normalizer.normalizeCountryName(name));

		Country country;
		if((country = dao.findByUri(uri)) != null) {
			return country;
		}

		country = new Country();
		country.setUri(uri);
		country.setTitle(normalizer.normalizeText(name));
		dao.create(country);
		entityManager.persist(country);
		return country;
	}

	@Transactional
	public void update(Country object) {
		dao.update(object);
	}
	
	@Transactional(readOnly = true)
	public List<Country> getAllDistinct() {
		String query = """
			PREFIX schema: <%1$s>
			SELECT DISTINCT ?x
			WHERE {
				?x a <%2$s> .
				?x schema:name ?n .
			}
			ORDER BY ASC(?n)
			""".formatted(Types.PREFIX_SCHEMA, Types.COUNTRY);
		return entityManager.createNativeQuery(query, Country.class)
				.getResultStream()
				.toList();
	}
}