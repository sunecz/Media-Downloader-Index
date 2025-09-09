package sune.app.mediadown.index.model;

import org.springframework.stereotype.Repository;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.entity.Movie;

@Repository
public class MovieDao extends BaseDao<Movie> {
	
	public MovieDao(EntityManager em) {
		super(em);
	}
}