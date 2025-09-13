package sune.app.mediadown.index.util;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import cz.cvut.kbss.jopa.exceptions.RollbackException;

@Component("transactions")
public class DefaultTransactions implements Transactions {

	private final PlatformTransactionManager transactionManager;

	public DefaultTransactions(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	@Override
	public <T> T doTransaction(CheckedSupplier<T> action) throws Exception {
		DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(definition);
		T result;
		
		try {
			result = action.get();
		} catch(Exception ex) {
			transactionManager.rollback(status);
			throw ex;
		}
		
		if(!status.isRollbackOnly()) {
			try {
				transactionManager.commit(status);
				return result;
			} catch(RollbackException ex) {
				// Ignore
			} catch(TransactionSystemException ex) {
				if(!(ex.getCause() instanceof RollbackException)) {
					throw ex;
				}
			}
		}
		
		if(!status.isCompleted()) {
			transactionManager.rollback(status);
		}
		
		return result;
	}
	
	@Override
	public void doTransaction(CheckedRunnable action) throws Exception {
		doTransaction(() -> {
			action.run();
			return null;
		});
	}
}
