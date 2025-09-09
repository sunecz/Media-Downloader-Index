package sune.app.mediadown.index.service;

import java.net.URI;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.entity.Episode;
import sune.app.mediadown.index.entity.TVSeason;
import sune.app.mediadown.index.entity.TVSeries;
import sune.app.mediadown.index.entity.Types;
import sune.app.mediadown.index.model.TVSeasonDao;
import sune.app.mediadown.index.normalization.Normalizer;

@Service
public class TVSeasonService {

	private final TVSeasonDao dao;
	private final Normalizer normalizer;
	private final EntityManager entityManager;

	public TVSeasonService(TVSeasonDao dao, Normalizer normalizer, EntityManager entityManager) {
		this.dao = dao;
		this.normalizer = normalizer;
		this.entityManager = entityManager;
	}

	private URI uri(TVSeries series, int number) {
		URI identifier = series.getUri();

		return Types.mdiUri(String.format(
			"%s/%s/%d",
			"seasons",
			normalizer.normalizeUriComponent(
				String.format("%s%s%s", identifier.getHost(), identifier.getPath(), identifier.getQuery())
			),
			number
		));
	}

	@Transactional(readOnly = true)
	public TVSeason getByUri(URI uri) {
		return dao.findByUri(uri);
	}

	@Transactional
	public TVSeason getBySeriesOrCreate(TVSeries series, int number) {
		URI uri = uri(series, number);

		TVSeason season;
		if((season = dao.findByUri(uri)) != null) {
			return season;
		}

		season = new TVSeason();
		season.setUri(uri);
		season.setNumber(number);
		season.setSeries(series);
		dao.create(season);
		entityManager.persist(season);
		return season;
	}

	@Transactional
	public void update(TVSeason object) {
		dao.update(object);
	}
	
	@Transactional
	public List<Episode> getEpisodes(TVSeason season) {
		return dao.getEpisodes(season);
	}
}