package sune.app.mediadown.index.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

public final class InternalState {
	
	private final AtomicInteger state;
	
	public InternalState() {
		this(0);
	}
	
	public InternalState(int initialState) {
		state = new AtomicInteger(initialState);
	}
	
	private void setValue(IntUnaryOperator op) {
		int oldValue = state.get();
		int newValue = op.applyAsInt(oldValue);
		
		while(oldValue != newValue
				&& !state.compareAndSet(oldValue, newValue)) {
			oldValue = state.get();
			newValue = op.applyAsInt(oldValue);
		}
	}
	
	public void clear(int value) {
		state.set(value);
	}
	
	public void set(int value) {
		setValue((current) -> current | value);
	}
	
	public void unset(int value) {
		setValue((current) -> current & (~value));
	}
	
	public int get() {
		return state.get();
	}
	
	public boolean is(int value) {
		return (state.get() & value) == value;
	}
}