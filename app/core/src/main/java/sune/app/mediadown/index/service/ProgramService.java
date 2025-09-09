package sune.app.mediadown.index.service;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.entity.Movie;
import sune.app.mediadown.index.entity.Program;
import sune.app.mediadown.index.entity.TVSeries;
import sune.app.mediadown.index.entity.Types;
import sune.app.mediadown.index.util.DBUtils;
import sune.app.mediadown.index.util.JavaScript;

@Service
public class ProgramService {

	private final EntityManager entityManager;

	public ProgramService(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	protected <T> T get(Class<T> clazz, URI uri) {
		return entityManager.getReference(clazz, uri);
	}

	private Program mapToProgram(Object v) {
		Object[] args = (Object[]) v;
		URI uri = (URI) args[0];
		URI schemaUri = (URI) args[1];
		return switch(schemaUri.toString()) {
			case Types.TV_SERIES -> get(TVSeries.class, uri);
			case Types.MOVIE -> get(Movie.class, uri);
			default -> null;
		};
	}

	@Transactional(readOnly = true)
	public List<Program> getAll() {
		String query = """
			PREFIX schema: <%1$s>
			SELECT DISTINCT ?x ?t
			WHERE {
				?x a ?t .
				VALUES ?t { %2$s }
			}
			""".formatted(Types.PREFIX_SCHEMA, DBUtils.asTypeList(Types.TV_SERIES, Types.MOVIE));
		@SuppressWarnings("unchecked")
		var stream = ((Stream<Object>) entityManager.createNativeQuery(query).getResultStream());
		return stream.map(this::mapToProgram).toList();
	}

	@Transactional(readOnly = true)
	public Set<URI> getAllUris() {
		String query = """
			PREFIX schema: <%1$s>
			SELECT DISTINCT ?x
			WHERE {
				?x a ?t .
				VALUES ?t { %2$s }
			}
			""".formatted(Types.PREFIX_SCHEMA, DBUtils.asTypeList(Types.TV_SERIES, Types.MOVIE));
		return entityManager.createNativeQuery(query, URI.class).getResultStream()
					.collect(Collectors.toSet());
	}

	@Transactional(readOnly = true)
	public Set<URI> findAliases(TVSeries series) {
		String query = """
			PREFIX schema: <%1$s>
			SELECT ?y
			WHERE {
				?x a <%2$s> .
				?x schema:name ?nx .
				OPTIONAL {
					?x schema:startDate ?sdx .
					?x schema:endDate ?edx .
				}
				{
					SELECT ?y ?ny
					WHERE {
						?y a <%2$s> .
						?y schema:name ?ny .
						OPTIONAL {
							?y schema:startDate ?sdy .
							?y schema:endDate ?edy .
						}
					}
				}
				FILTER (?x != ?y
					&& ?nx = ?ny
					&& (!BOUND(?sdx) || !BOUND(?sdy) || (?sdx = ?sdy))
					&& (!BOUND(?edx) || !BOUND(?edy) || (?edx = ?edy))
				)
				FILTER (?x = <%3$s>)
			}
			""".formatted(Types.PREFIX_SCHEMA, Types.TV_SERIES, series.getIdentifier());
		return entityManager.createNativeQuery(query, URI.class)
					.getResultStream()
					.collect(Collectors.toSet());
	}

	@Transactional(readOnly = true)
	public Set<URI> findAliases(Movie movie) {
		String query = """
			PREFIX schema: <%1$s>
			SELECT ?y
			WHERE {
				?x a <%2$s> .
				?x schema:name ?nx .
				OPTIONAL {
					?x schema:datePublished ?dpx .
				}
				{
					SELECT ?y ?ny
					WHERE {
						?y a <%2$s> .
						?y schema:name ?ny .
						OPTIONAL {
							?y schema:datePublished ?dpy .
						}
					}
				}
				FILTER (?x != ?y
					&& ?nx = ?ny
					&& (!BOUND(?dpx) || !BOUND(?dpy) || (?dpx = ?dpy))
				)
				FILTER (?x = <%3$s>)
			}
			""".formatted(Types.PREFIX_SCHEMA, Types.MOVIE, movie.getIdentifier());
		return entityManager.createNativeQuery(query, URI.class)
					.getResultStream()
					.collect(Collectors.toSet());
	}

	@Transactional(readOnly = true)
	public List<Integer> getAllYears() {
		class YearsMapper {

			static OffsetDateTime parseDate(Object value) {
				if(value instanceof String string) {
					return OffsetDateTime.parse(string);
				}

				return null;
			}

			static void mapMulti(Object value, Consumer<Integer> consumer) {
				Object[] array = (Object[]) value;
				OffsetDateTime datePublished = parseDate((String) array[1]);
				OffsetDateTime startDate = parseDate((String) array[2]);
				OffsetDateTime endDate = parseDate((String) array[3]);

				if(datePublished != null) {
					consumer.accept(datePublished.getYear());
				}

				if(startDate != null && endDate != null) {
					IntStream.rangeClosed(startDate.getYear(), endDate.getYear()).boxed().forEachOrdered(consumer::accept);
				}
			}
		}
		String query = """
			PREFIX schema: <%1$s>
			SELECT ?x ?dp ?sd ?ed
			WHERE {
				?x a ?t .
				OPTIONAL {
					?x schema:datePublished ?dp .
					?x schema:startDate ?sd .
					?x schema:endDate ?ed .
				}
				VALUES ?t { %2$s }
				FILTER (BOUND(?dp) || BOUND(?sd) || BOUND(?ed))
			}
			""".formatted(Types.PREFIX_SCHEMA, DBUtils.asTypeList(Types.TV_SERIES, Types.MOVIE));
		@SuppressWarnings("unchecked")
		var stream = (Stream<Object>) entityManager.createNativeQuery(query).getResultStream();
		return stream.<Integer>mapMulti(YearsMapper::mapMulti)
					.distinct().sorted()
					.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	protected <T> T findByUri(Class<T> clazz, String type, String uri) {
		String query = """
			PREFIX schema: <%1$s>
			SELECT ?x
			WHERE {
				?x a <%2$s> .
				FILTER(REPLACE(STR(?x), "^.*?://", "") = ?uri)
			}
			""".formatted(Types.PREFIX_SCHEMA, type);
		return entityManager.createNativeQuery(query, clazz)
					.setParameter("uri", uri)
					.getResultStream()
					.findFirst().orElse(null);
	}

	public Program getBySlug(String slug) {
		slug = JavaScript.decodeURIComponent(slug);
		int index = slug.indexOf('/');

		if(index < 0) {
			return null;
		}

		String type = slug.substring(0, index);
		String path = slug.substring(index + 1);

		return switch(type) {
			case "tv-series" -> findByUri(TVSeries.class, Types.TV_SERIES, path);
			case "movie" -> findByUri(Movie.class, Types.MOVIE, path);
			default -> null;
		};
	}
}