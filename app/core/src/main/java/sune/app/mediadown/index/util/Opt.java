package sune.app.mediadown.index.util;

import java.lang.invoke.MethodHandles;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.*;

public final class Opt<T> {
	
	private static final Opt<?> EMPTY = new Opt<>();
	private static final Function<?, ?> FN_NULL = ((v) -> null);
	
	private final Supplier<OptValue<T>> supplier;
	private final Supplier<OptValue<?>> originalSupplier;
	private Function<OptValue<T>, OptValue<T>> pipeline;
	private OptValue<?> value;
	private boolean isEvaluated;
	private Class<?> primitiveClass;
	
	private Opt() {
		this.supplier = nullSupplier();
		this.originalSupplier = typeErasure(this.supplier);
		this.pipeline = fnNull();
	}
	
	private Opt(Supplier<T> supplier) {
		this.supplier = valueSupplier(Objects.requireNonNull(supplier));
		this.originalSupplier = typeErasure(this.supplier);
		this.pipeline = fnIdentity();
	}
	
	// Used for Opt::map and Opt::copy
	private Opt(Supplier<OptValue<?>> originalSupplier, Supplier<OptValue<T>> supplier) {
		this.supplier = Objects.requireNonNull(supplier);
		this.originalSupplier = Objects.requireNonNull(originalSupplier);
		this.pipeline = fnIdentity();
	}
	
	private static <T> Function<T, T> fnNull() {
		@SuppressWarnings("unchecked")
		Function<T, T> fn = (Function<T, T>) FN_NULL;
		return fn;
	}
	
	private static <T> Function<T, T> fnIdentity() {
		return Function.identity();
	}
	
	private static <T> Supplier<OptValue<T>> nullSupplier() {
		return (() -> null);
	}
	
	private static <T> Supplier<OptValue<T>> valueSupplier(Supplier<T> supplier) {
		return (() -> OptValue.of(supplier.get()));
	}
	
	@SuppressWarnings("unchecked")
	private static <T> Supplier<OptValue<?>> typeErasure(Supplier<OptValue<T>> supplier) {
		return (Supplier<OptValue<?>>) (Supplier<?>) supplier;
	}
	
	@SuppressWarnings("unchecked")
	private static <U> Supplier<OptValue<U>> typeCast(Supplier<OptValue<?>> supplier) {
		return (Supplier<OptValue<U>>) (Supplier<?>) supplier;
	}
	
	public static <T> Opt<T> empty() {
		@SuppressWarnings("unchecked")
		Opt<T> empty = (Opt<T>) EMPTY;
		return empty;
	}
	
	public static <T> Opt<T> of(T value) {
		return new Opt<>(() -> value);
	}
	
	public static <T> Opt<T> ofSupplier(Supplier<T> supplier) {
		return new Opt<>(supplier);
	}
	
	private void ensureEvaluated() {
		if(!isEvaluated) {
			value = pipeline.apply(supplier.get());
			isEvaluated = true;
		}
	}
	
	private T primitiveZeroValue() {
		try {
			return (T) MethodHandles.zero(primitiveClass).invoke();
		} catch(Throwable ex) {
			// Should not happen
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private T value() {
		T val = (T) value.value();
		return primitiveClass != null && val == null ? primitiveZeroValue() : val;
	}
	
	public Opt<T> ifTrue(OptPredicate<T> condition) {
		Objects.requireNonNull(condition);
		final Function<OptValue<T>, OptValue<T>> ref = this.pipeline;
		this.pipeline = ((v) -> {
			OptValue<T> t = ref.apply(v);
			return !t.isEmpty() && condition.test(t.value()) ? t : OptValue.empty();
		});
		return this;
	}
	
	public Opt<T> ifFalse(OptPredicate<T> condition) {
		Objects.requireNonNull(condition);
		return ifTrue(condition.negate());
	}
	
	public Opt<T> filter(OptPredicate<T> filter) {
		Objects.requireNonNull(filter);
		final Function<OptValue<T>, OptValue<T>> ref = this.pipeline;
		this.pipeline = ((v) -> {
			OptValue<T> t = ref.apply(v);
			return !t.isEmpty() && filter.test(t.value()) ? t : OptValue.empty();
		});
		return this;
	}
	
	public <U> Opt<U> map(Function<T, U> mapper) {
		Objects.requireNonNull(mapper);
		final Function<OptValue<T>, OptValue<T>> ref = this.pipeline;
		final Supplier<OptValue<T>> sup = this.supplier;
		return new Opt<>(originalSupplier, () -> {
			OptValue<T> t = ref.apply(sup.get());
			return !t.isEmpty() ? OptValue.of(mapper.apply(t.value())) : OptValue.empty();
		});
	}
	
	public Opt<T> ifEmpty(Consumer<? super T> action) {
		if(isEmpty()) Objects.requireNonNull(action).accept(value());
		return this;
	}
	
	public Opt<T> ifEmptyOrElse(Consumer<? super T> action, Consumer<? super T> orElse) {
		if(isEmpty()) Objects.requireNonNull(action).accept(value());
		else          Objects.requireNonNull(orElse).accept(value());
		return this;
	}
	
	public Opt<T> ifPresent(Consumer<? super T> action) {
		if(isPresent()) Objects.requireNonNull(action).accept(value());
		return this;
	}
	
	public Opt<T> ifPresentOrElse(Consumer<? super T> action, Consumer<? super T> orElse) {
		if(isPresent()) Objects.requireNonNull(action).accept(value());
		else            Objects.requireNonNull(orElse).accept(value());
		return this;
	}
	
	public <S extends T> Opt<S> cast() {
		@SuppressWarnings("unchecked")
		Opt<S> casted = (Opt<S>) this;
		return casted;
	}
	
	public <S> Opt<S> castAny() {
		@SuppressWarnings("unchecked")
		Opt<S> casted = (Opt<S>) this;
		return casted;
	}
	
	public T get() {
		ensureEvaluated();
		return value();
	}
	
	public T orNull() {
		return orElse(null);
	}
	
	public T orElse(T other) {
		ensureEvaluated();
		return !isEmpty() ? value() : other;
	}
	
	public T orElseGet(Supplier<T> supplier) {
		ensureEvaluated();
		return !isEmpty() ? value() : supplier.get();
	}
	
	public T orElseThrow() throws NoSuchElementException {
		ensureEvaluated();
		if(isEmpty())
			throw new NoSuchElementException("No value present");
		return value();
	}
	
	public <X extends Throwable> T orElseThrow(Supplier<? extends X> supplier) throws X {
		ensureEvaluated();
		if(isEmpty()) throw supplier.get();
		return value();
	}
	
	public Opt<T> unbox(Class<?> clazz) {
		if(!Objects.requireNonNull(clazz).isPrimitive())
			throw new IllegalArgumentException("Not a primitive class");
		primitiveClass = clazz;
		return this;
	}
	
	public Opt<T> box() {
		primitiveClass = null;
		return this;
	}
	
	public <U> Opt<T> or(Function<Opt<U>, Opt<T>> transformer) {
		Opt<U> opt = new Opt<>(originalSupplier, typeCast(originalSupplier));
		return isPresent() ? this : Objects.requireNonNull(Objects.requireNonNull(transformer).apply(opt));
	}
	
	public Opt<T> or(Supplier<Opt<T>> supplier) {
		return isPresent() ? this : Objects.requireNonNull(Objects.requireNonNull(supplier).get());
	}
	
	public Opt<T> copy() {
		Opt<T> copy = new Opt<>(originalSupplier, supplier);
		copy.pipeline = pipeline;
		copy.value = value;
		copy.isEvaluated = isEvaluated;
		copy.primitiveClass = primitiveClass;
		return copy;
	}
	
	public boolean isEmpty() {
		ensureEvaluated();
		return value.isEmpty();
	}
	
	public boolean isPresent() {
		ensureEvaluated();
		return !value.isEmpty();
	}
	
	public boolean isEvaluated() {
		return isEvaluated;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(isEvaluated, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Opt<?> other = (Opt<?>) obj;
		return isEvaluated == other.isEvaluated && Objects.equals(value, other.value);
	}
	
	@Override
	public String toString() {
		return "Opt[value=" + value + ", isEvaluated=" + isEvaluated + "]";
	}
	
	private static final class OptValue<T> {
		
		private static final OptValue<?> EMPTY = new OptValue<>();
		
		private final T value;
		private final boolean isEmpty;
		
		private OptValue() {
			this.value = null;
			this.isEmpty = true;
		}
		
		private OptValue(T value) {
			this.value = value;
			this.isEmpty = false;
		}
		
		public static <T> OptValue<T> of(T value) {
			return new OptValue<>(value);
		}
		
		public static <T> OptValue<T> empty() {
			@SuppressWarnings("unchecked")
			OptValue<T> v = (OptValue<T>) EMPTY;
			return v;
		}
		
		public T value() {
			return value;
		}
		
		public boolean isEmpty() {
			return isEmpty;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(isEmpty, value);
		}
		
		@Override
		public boolean equals(Object obj) {
			if(this == obj)
				return true;
			if(obj == null)
				return false;
			if(getClass() != obj.getClass())
				return false;
			OptValue<?> other = (OptValue<?>) obj;
			return isEmpty == other.isEmpty && Objects.equals(value, other.value);
		}
		
		@Override
		public String toString() {
			return "OptValue[value=" + value + ", isEmpty=" + isEmpty + "]";
		}
	}
	
	@FunctionalInterface
	public interface OptPredicate<T> extends Predicate<T> {
		
		boolean test(T value);
		
		default OptPredicate<T> negate() {
			return ((t) -> !test(t));
		}
		
		default Predicate<T> predicate() {
			return this::test;
		}
	}
	
	public static final class OptCondition<T> implements OptPredicate<T> {
		
		private static final Predicate<?> TRUE  = ((t) -> true);
		private static final Predicate<?> FALSE = ((t) -> false);
		
		private Predicate<? super T> condition;
		private boolean result;
		private boolean evaluated;
		
		private OptCondition(Predicate<? super T> condition) {
			this.condition = Objects.requireNonNull(condition);
		}
		
		public static <T> OptCondition<T> of(Predicate<? super T> condition) {
			return new OptCondition<>(condition);
		}
		
		@SafeVarargs
		public static <T> OptCondition<T> ofAll(Predicate<? super T>... conditions) {
			if(conditions.length == 0) return ofTrue();
			OptCondition<T> opt = new OptCondition<>(conditions[0]);
			for(int i = 1, l = conditions.length; i < l; ++i) opt = opt.and(conditions[i]);
			return opt;
		}
		
		@SafeVarargs
		public static <T> OptCondition<T> ofAny(Predicate<? super T>... conditions) {
			if(conditions.length == 0) return ofFalse();
			OptCondition<T> opt = new OptCondition<>(conditions[0]);
			for(int i = 1, l = conditions.length; i < l; ++i) opt = opt.or(conditions[i]);
			return opt;
		}
		
		public static <T> OptCondition<T> ofTrue() {
			@SuppressWarnings("unchecked")
			OptCondition<T> cond = of((Predicate<T>) TRUE);
			return cond;
		}
		
		public static <T> OptCondition<T> ofFalse() {
			@SuppressWarnings("unchecked")
			OptCondition<T> cond = of((Predicate<T>) FALSE);
			return cond;
		}
		
		// Can be used as a method reference for predicates
		public static <T> boolean returnTrue(T t) {
			return ofTrue().test(t);
		}
		
		// Can be used as a method reference for predicates
		public static <T> boolean returnFalse(T t) {
			return ofFalse().test(t);
		}
		
		private void ensureEvaluated() {
			if(!evaluated)
				throw new IllegalStateException("Not evaluated yet");
		}
		
		private OptCondition<T> merge(
                Predicate<? super T> condition,
                BiFunction<Predicate<? super T>, Predicate<? super T>, Predicate<? super T>> merger
        ) {
			final Predicate<? super T> ref = this.condition;
			this.condition = merger.apply(ref, condition);
			return this;
		}
		
		public OptCondition<T> and(Predicate<? super T> condition) {
			return merge(Objects.requireNonNull(condition), (a, b) -> ((t) -> a.test(t) && b.test(t)));
		}
		
		public OptCondition<T> andOpt(OptPredicate<? super T> condition) {
			return and(condition.predicate());
		}
		
		public OptCondition<T> or(Predicate<? super T> condition) {
			return merge(Objects.requireNonNull(condition), (a, b) -> ((t) -> a.test(t) || b.test(t)));
		}
		
		public OptCondition<T> orOpt(OptPredicate<? super T> condition) {
			return or(condition.predicate());
		}
		
		public OptCondition<T> not() {
			return merge(null, (a, b) -> ((t) -> !a.test(t)));
		}
		
		public OptCondition<T> evaluate(T value) {
			result = condition.test(value);
			evaluated = true;
			return this;
		}
		
		@Override
		public boolean test(T value) {
			evaluate(value);
			return isTrue();
		}
		
		public boolean isTrue() {
			ensureEvaluated();
			return result;
		}
		
		public boolean isFalse() {
			ensureEvaluated();
			return !result;
		}
	}
	
	public static final class OptMapper<A, B> implements Function<A, B> {
		
		private static final Function<?, ?> IDENTITY = Function.identity();
		
		private final Function<A, B> mapper;
		
		private OptMapper(Function<A, B> mapper) {
			this.mapper = Objects.requireNonNull(mapper);
		}
		
		public static <A> OptMapper<A, A> identity() {
			@SuppressWarnings("unchecked")
			Function<A, A> mapper = (Function<A, A>) IDENTITY;
			return new OptMapper<>(mapper);
		}
		
		public static <A, B> OptMapper<A, B> of(Function<A, B> mapper) {
			return new OptMapper<>(mapper);
		}
		
		@Override
		public B apply(A a) {
			return mapper.apply(a);
		}
		
		public <C> OptMapper<A, C> then(Function<B, C> then) {
			Objects.requireNonNull(then);
			return new OptMapper<>((a) -> then.apply(mapper.apply(a)));
		}
	}
}