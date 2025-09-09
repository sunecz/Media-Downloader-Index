package sune.app.mediadown.index.normalization;

import org.springframework.stereotype.Component;

import sune.app.mediadown.index.net.HTML;
import sune.app.mediadown.index.util.JavaScript;
import sune.app.mediadown.index.util.Regex;

@Component("normalizer")
public class DefaultNormalizer implements Normalizer {

	// See: https://www.regular-expressions.info/unicode.html
	private final Regex regexNonBaseGlyph = Regex.of("(?u)\\p{M}");

	DefaultNormalizer() {
	}

	private String removeNonBaseGlyphs(String string) {
		return regexNonBaseGlyph.replaceAll(string, "");
	}

	// See: https://www.unicode.org/reports/tr15/
	private String normalize(String string) {
		return java.text.Normalizer.normalize(string, java.text.Normalizer.Form.NFKD);
	}

	@Override
	public String normalizeUriComponent(String component) {
		return component == null ? null : JavaScript.encodeURIComponent(component);
	}

	@Override
	public String normalizeText(String text) {
		return text == null ? null : normalize(text).strip();
	}

	@Override
	public String normalizeHtmlToText(String html) {
		return html == null ? null : normalize(HTML.parse(html).text()).strip();
	}

	@Override
	public String normalizeCountryName(String name) {
		return name == null ? null : removeNonBaseGlyphs(normalize(name)).strip();
	}

	@Override
	public String normalizePersonName(String name) {
		return name == null ? null : removeNonBaseGlyphs(normalize(name)).strip();
	}

	@Override
	public String normalizeLanguageName(String name) {
		return name == null ? null : removeNonBaseGlyphs(normalize(name)).strip();
	}

	@Override
	public String normalizeGenreName(String name) {
		return name == null ? null : removeNonBaseGlyphs(normalize(name)).strip();
	}
}