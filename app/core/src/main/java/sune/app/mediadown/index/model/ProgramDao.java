package sune.app.mediadown.index.model;

import org.springframework.stereotype.Repository;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.entity.Program;

@Repository
public class ProgramDao extends BaseDao<Program> {

	public ProgramDao(EntityManager em) {
		super(em);
	}
}