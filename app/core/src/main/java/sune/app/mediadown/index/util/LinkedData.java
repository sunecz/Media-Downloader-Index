package sune.app.mediadown.index.util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class LinkedData {

	private final JSON.JSONCollection data;

	private LinkedData(JSON.JSONCollection data) {
		this.data = Objects.requireNonNull(data);
	}

	public static Stream<LinkedData> streamFrom(Document document) {
		Objects.requireNonNull(document);

		return StreamSupport.stream(
			Spliterators.spliteratorUnknownSize(
				new _Iterator(document.select("script[type='application/ld+json']").iterator()),
				Spliterator.ORDERED
			),
			false
		);
	}

	public static Stream<LinkedData> streamFrom(Document document, String type) {
		return streamFrom(document).filter((ld) -> type.equalsIgnoreCase(ld.data().getString("@type")));
	}

	public static Stream<LinkedData> streamFrom(Document document, Set<String> types) {
		return streamFrom(document).filter((ld) -> types.contains(ld.data().getString("@type")));
	}

	public static List<LinkedData> from(Document document) {
		return streamFrom(document).toList();
	}

	public static LinkedData from(Document document, String type) {
		return streamFrom(document, type).findFirst().orElse(null);
	}

	public static LinkedData from(Document document, Set<String> types) {
		return streamFrom(document, types).findFirst().orElse(null);
	}

	public JSON.JSONCollection data() {
		return data;
	}

	private static final class _Iterator implements Iterator<LinkedData> {

		private final Iterator<Element> it;

		public _Iterator(Iterator<Element> it) {
			this.it = Objects.requireNonNull(it);
		}

		@Override
		public boolean hasNext() {
			return it.hasNext();
		}

		@Override
		public LinkedData next() {
			Element script = it.next();
			JSON.JSONCollection data = JSON.read(script.html());
			return new LinkedData(data);
		}
	}
}