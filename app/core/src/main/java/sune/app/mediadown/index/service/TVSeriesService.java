package sune.app.mediadown.index.service;

import java.net.URI;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sune.app.mediadown.index.entity.TVSeries;
import sune.app.mediadown.index.entity.Types;
import sune.app.mediadown.index.model.TVSeriesDao;

@Service
public class TVSeriesService {

	private final TVSeriesDao dao;

	public TVSeriesService(TVSeriesDao dao) {
		this.dao = dao;
	}

	@Transactional(readOnly = true)
	public List<TVSeries> getAll() {
		return dao.findAll(Types.TV_SERIES);
	}

	@Transactional(readOnly = true)
	public TVSeries getByUri(URI uri) {
		return dao.findByUri(uri);
	}
	
	@Transactional
	public void create(TVSeries tvSeries) {
		dao.create(tvSeries);
	}

	@Transactional
	public void update(TVSeries object) {
		dao.update(object);
	}
}