package sune.app.mediadown.index.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sune.app.mediadown.index.entity.Movie;
import sune.app.mediadown.index.entity.Types;
import sune.app.mediadown.index.model.MovieDao;

import java.net.URI;
import java.util.List;

@Service
public class MovieService {
	
	private final MovieDao dao;
	
	public MovieService(MovieDao dao) {
		this.dao = dao;
	}
	
	@Transactional(readOnly = true)
	public List<Movie> getAll() {
		return dao.findAll(Types.MOVIE);
	}

	@Transactional(readOnly = true)
	public Movie getByUri(URI uri) {
		return dao.findByUri(uri);
	}
	
	@Transactional
	public void create(Movie movie) {
		dao.create(movie);
	}
}