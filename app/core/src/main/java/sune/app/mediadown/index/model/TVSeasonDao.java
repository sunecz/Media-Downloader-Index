package sune.app.mediadown.index.model;

import java.util.List;

import org.springframework.stereotype.Repository;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.entity.Episode;
import sune.app.mediadown.index.entity.TVSeason;
import sune.app.mediadown.index.entity.Types;

@Repository
public class TVSeasonDao extends BaseDao<TVSeason> {

	public TVSeasonDao(EntityManager em) {
		super(em);
	}

	public List<Episode> getEpisodes(TVSeason season) {
		String query = """
			PREFIX schema: <%1$s>
			SELECT ?x
			WHERE {
				?x a <%2$s> .
				?x schema:partOfSeason ?season .
				?x schema:episodeNumber ?number .
			}
			ORDER BY DESC(?number)
			""".formatted(Types.PREFIX_SCHEMA, Types.EPISODE);
		return em.createNativeQuery(query, Episode.class)
					.setParameter("season", season.getUri())
					.getResultList();
	}
}