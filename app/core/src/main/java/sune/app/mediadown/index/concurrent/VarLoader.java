package sune.app.mediadown.index.concurrent;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Objects;
import java.util.function.Supplier;

import sune.app.mediadown.index.util.CheckedSupplier;
import sune.app.mediadown.index.util.UncheckedException;

public final class VarLoader<T> {
	
	private static final Object UNSET = new Object();
	private static final VarHandle HANDLE;
	
	private final CheckedSupplier<T> supplier;
	@SuppressWarnings("unused")
	private Object value = UNSET;
	
	static {
		try {
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			HANDLE = lookup.findVarHandle(VarLoader.class, "value", Object.class);
		} catch(NoSuchFieldException | IllegalAccessException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	private VarLoader(CheckedSupplier<T> supplier) {
		this.supplier = Objects.requireNonNull(supplier);
	}
	
	public static <T> VarLoader<T> of(Supplier<T> supplier) {
		return new VarLoader<>(supplier::get);
	}
	
	public static <T> VarLoader<T> ofChecked(CheckedSupplier<T> supplier) {
		return new VarLoader<>(supplier);
	}
	
	private void atomicSet(Object value) {
		HANDLE.setRelease(this, value);
	}
	
	private Object atomicGet() {
		return HANDLE.getAcquire(this);
	}
	
	private Object valueRaw() throws Exception {
		Object ref = atomicGet();
		
		if(ref == UNSET) {
			synchronized(this) {
				ref = atomicGet();
				
				if(ref == UNSET) {
					ref = supplier.get();
					atomicSet(ref);
				}
			}
		}
		
		return ref;
	}
	
	public void unset() {
		Object ref = atomicGet();
		
		if(ref != UNSET) {
			synchronized(this) {
				ref = atomicGet();
				
				if(ref != UNSET) {
					atomicSet(UNSET);
				}
			}
		}
	}
	
	public T valueChecked() throws Exception {
		@SuppressWarnings("unchecked")
		T casted = (T) valueRaw();
		return casted;
	}
	
	public T value() {
		try {
			return valueChecked();
		} catch(Exception ex) {
			throw new UncheckedException(ex);
		}
	}
	
	public T valueOrElse(T defaultValue) {
		try {
			return valueChecked();
		} catch(Exception ex) {
			// Ignore
		}
		
		return defaultValue;
	}
	
	public T valueOrElseGet(Supplier<T> supplier) {
		try {
			return valueChecked();
		} catch(Exception ex) {
			// Ignore
		}
		
		return supplier.get();
	}
	
	public boolean isSet() {
		return atomicGet() != UNSET;
	}
	
	public boolean isUnset() {
		return atomicGet() == UNSET;
	}
}