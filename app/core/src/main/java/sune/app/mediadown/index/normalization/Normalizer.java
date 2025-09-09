package sune.app.mediadown.index.normalization;

public interface Normalizer {

	String normalizeUriComponent(String component);
	String normalizeText(String text);
	String normalizeHtmlToText(String html);
	String normalizeCountryName(String name);
	String normalizePersonName(String name);
	String normalizeLanguageName(String name);
	String normalizeGenreName(String name);
}