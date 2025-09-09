package sune.app.mediadown.index.plugin.iprima;

import java.util.Objects;

import sune.app.mediadown.index.task.ListTask;
import sune.app.mediadown.index.util.CheckedConsumer;

public final class PrimaCommon {
	
	// Forbid anyone to create an instance of this class
	private PrimaCommon() {
	}
	
	public static void error(Throwable throwable) {
		if(throwable == null) {
			return;
		}
		
		throw new RuntimeException(throwable);
	}
	
	public static <T> CheckedConsumer<ListTask<T>> handleErrors(CheckedConsumer<ListTask<T>> action) {
		return ((task) -> {
			try {
				action.accept(task);
			} catch(Exception ex) {
				// More user-friendly error messages
				error(ex);
			}
		});
	}
	
	public static class TranslatableException extends Exception {
		
		private static final long serialVersionUID = 2966737246033320221L;
		
		protected final String translationPath;
		
		protected TranslatableException(String translationPath) {
			super();
			this.translationPath = Objects.requireNonNull(translationPath);
		}
		
		protected TranslatableException(String translationPath, Throwable cause) {
			super(cause);
			this.translationPath = Objects.requireNonNull(translationPath);
		}
		
		public String translationPath() {
			return translationPath;
		}
	}
	
	public static class MessageException extends Exception {
		
		private static final long serialVersionUID = -3290197439712588070L;
		
		protected final String message;
		
		protected MessageException(String message) {
			super();
			this.message = Objects.requireNonNull(message);
		}
		
		protected MessageException(String message, Throwable cause) {
			super(cause);
			this.message = Objects.requireNonNull(message);
		}
		
		public String message() {
			return message;
		}
	}
}