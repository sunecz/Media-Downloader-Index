package sune.app.mediadown.index.service;

import java.net.URI;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.entity.Language;
import sune.app.mediadown.index.entity.Types;
import sune.app.mediadown.index.model.LanguageDao;
import sune.app.mediadown.index.normalization.Normalizer;

@Service
public class LanguageService {

	private final LanguageDao dao;
	private final Normalizer normalizer;
	private final EntityManager entityManager;

	public LanguageService(LanguageDao dao, Normalizer normalizer, EntityManager entityManager) {
		this.dao = dao;
		this.normalizer = normalizer;
		this.entityManager = entityManager;
	}

	private URI uri(String name) {
		return Types.mdiUri("languages/" + normalizer.normalizeUriComponent(name));
	}

	@Transactional(readOnly = true)
	public List<Language> getAll() {
		return dao.findAll(Types.LANGUAGE);
	}

	@Transactional(readOnly = true)
	public Language getByUri(URI uri) {
		return dao.findByUri(uri);
	}

	@Transactional
	public Language getOrCreateByTitle(String title) {
		URI uri = uri(normalizer.normalizeLanguageName(title));

		Language language;
		if((language = dao.findByUri(uri)) != null) {
			return language;
		}

		language = new Language();
		language.setUri(uri);
		language.setTitle(normalizer.normalizeText(title));
		dao.create(language);
		entityManager.persist(language);
		return language;
	}

	@Transactional
	public void update(Language object) {
		dao.update(object);
	}
}