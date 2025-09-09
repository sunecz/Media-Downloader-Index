package sune.app.mediadown.index.model;

import org.springframework.stereotype.Repository;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.entity.Video;

@Repository
public class VideoDao extends BaseDao<Video> {

	public VideoDao(EntityManager em) {
		super(em);
	}
}