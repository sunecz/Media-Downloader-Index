package sune.app.mediadown.index.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import cz.cvut.kbss.jopa.model.EntityManager;
import sune.app.mediadown.index.util.DBUtils;

public abstract class BaseDao<T> {

	protected final Class<T> clazz;
	protected final EntityManager em;
	
	public BaseDao(EntityManager em) {
		Type[] types = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) types[0];
		this.clazz = clazz;
		this.em = em;
	}

	public List<T> findAll(String type) {
		String query = """
			SELECT DISTINCT ?x
			WHERE {
				?x a <%1$s> .
			}
			""".formatted(type);
		return em.createNativeQuery(query, clazz)
					.getResultList();
	}

	public List<T> findAll(Set<String> types) {
		String query = """
			SELECT DISTINCT ?x
			WHERE {
				VALUES ?type { %1$s }
				?x a ?type .
			}
			""".formatted(DBUtils.asTypeList(types));
		return em.createNativeQuery(query, clazz)
					.getResultList();
	}

	public T findByUri(URI uri) {
		return em.find(clazz, uri);
	}

	public void create(T entity) {
		Objects.requireNonNull(entity);
		em.persist(entity);
	}

	public void update(T entity) {
		Objects.requireNonNull(entity);
		em.merge(entity);
	}
	
	public EntityManager getEntityManager() {
		return em;
	}
}