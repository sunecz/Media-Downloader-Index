package sune.app.mediadown.index.model;

import org.springframework.stereotype.Repository;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.entity.TVSeries;

@Repository
public class TVSeriesDao extends BaseDao<TVSeries> {

	public TVSeriesDao(EntityManager em) {
		super(em);
	}
}