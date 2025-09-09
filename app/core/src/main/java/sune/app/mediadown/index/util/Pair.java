package sune.app.mediadown.index.util;

public final class Pair<A, B> {
	
	public final A a;
	public final B b;
	
	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
	@Override
	public String toString() {
		return String.format("Pair<%s, %s>(%s, %s)",
			Utils.getClass(a).getSimpleName(),
			Utils.getClass(b).getSimpleName(),
                a,
                b);
	}
}