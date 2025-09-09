package sune.app.mediadown.index.model;

import org.springframework.stereotype.Repository;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.entity.Audio;

@Repository
public class AudioDao extends BaseDao<Audio> {

	public AudioDao(EntityManager em) {
		super(em);
	}
}