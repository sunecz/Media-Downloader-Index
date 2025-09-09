package sune.app.mediadown.index.extract;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.github.ledsoft.jopa.spring.transaction.DelegatingEntityManager;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.EntityManagerImpl;
import cz.cvut.kbss.jopa.model.LoadState;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import cz.cvut.kbss.jopa.model.metamodel.EntityType;
import cz.cvut.kbss.jopa.model.metamodel.FieldSpecification;
import cz.cvut.kbss.jopa.sessions.AbstractUnitOfWork;
import sune.app.mediadown.index.entity.Changeable;
import sune.app.mediadown.index.entity.Entity;
import sune.app.mediadown.index.entity.Identifiable;
import sune.app.mediadown.index.util.CheckedRunnable;
import sune.app.mediadown.index.util.CheckedSupplier;
import sune.app.mediadown.index.util.Transactions;

@Component
public class ExtractionContext {

	private final EntityManager entityManager;
	private final Transactions transactions;

	public ExtractionContext(EntityManager entityManager, Transactions transactions) {
		this.entityManager = entityManager;
		this.transactions = transactions;
	}
	
	public boolean canProcessEntity(Changeable entity) {
		return entity.isActive();
	}

	public boolean isChanged(Entity entity, Entity existing) {
		return !entity.equals(existing);
	}
	
	public boolean isChanged(Identifiable entity, Identifiable existing) {
		return !entity.equals(existing);
	}
	
	private <T extends Identifiable> void load(
		EntityManager em,
		AbstractUnitOfWork uow,
		T entity,
		Set<URI> loaded
	) {
		@SuppressWarnings("unchecked")
		EntityType<T> type = (EntityType<T>) em.getMetamodel().entity(entity.getClass());
		loaded.add(entity.getIdentifier());
		
		for(FieldSpecification<? super T, ?> spec : type.getFieldSpecifications()) {
			Object v = uow.loadEntityField(entity, spec);
			
			if(v instanceof Identifiable id && !loaded.contains(id.getIdentifier())) {
				load(em, uow, id, loaded);
			}
		}
	}
	
	private EntityManagerImpl entityManager() {
		return (EntityManagerImpl) ((DelegatingEntityManager) entityManager).getDelegate();
	}
	
	private AbstractUnitOfWork unitOfWork() {
		return (AbstractUnitOfWork) entityManager().getCurrentPersistenceContext();
	}
	
	private void load(Identifiable identifiable) {
		EntityManagerImpl em = entityManager();
		AbstractUnitOfWork uow = unitOfWork();
		
		if(!uow.isObjectManaged(identifiable) && uow.isLoaded(identifiable) != LoadState.LOADED) {
			detach(identifiable);
			identifiable = uow.readObject(identifiable.getClass(), identifiable.getIdentifier(), new EntityDescriptor());
		}
		
		Set<URI> loaded = new HashSet<>();
		load(em, uow, identifiable, loaded);
	}
	
	private <T> T find(Class<T> clazz, URI identifier) {
		return entityManager.find(clazz, identifier);
	}

	private <T> T mergeAndDetach(T object) {
		T merged = entityManager.merge(object);
		
		if(merged != object) {
			detach(object);
		}
		
		return merged;
	}
	
	public void detach(Object entity) {
		entityManager.detach(entity);
	}

	public Entity update(Entity entity) {
		Entity existing;
		if((existing = find(entity.getClass(), entity.getIdentifier())) != null) {
			// Load the object fields so that all are actually set when calculating changes
			load(entity);
			
			if(entity == existing || isChanged(entity, existing)) {
				entity.setChangedDate(OffsetDateTime.now());
				// Update the object and detach the old one
				entity = mergeAndDetach(entity);
			} else {
				// Clean up since nothing is changed
				detach(existing);
			}
		} else {
			entity.setChangedDate(OffsetDateTime.now());
			entityManager.persist(entity);
			
			// Load the object fields so that all are actually set when calculating changes
			load(entity);
		}
		
		return entity;
	}
	
	public Entity doUpdate(Entity entity) throws Exception {
		return doTransaction(() -> update(entity));
	}

	public <T> T doTransaction(CheckedSupplier<T> action) throws Exception {
		return transactions.doTransaction(action);
	}

	public void doTransaction(CheckedRunnable action) throws Exception {
		transactions.doTransaction(action);
	}
}