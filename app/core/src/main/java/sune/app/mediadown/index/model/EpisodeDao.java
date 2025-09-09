package sune.app.mediadown.index.model;

import org.springframework.stereotype.Repository;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.entity.Episode;

@Repository
public class EpisodeDao extends BaseDao<Episode> {

	public EpisodeDao(EntityManager em) {
		super(em);
	}
}