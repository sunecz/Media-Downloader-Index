package sune.app.mediadown.index.extract;

public interface ExtractNode {

	void extract(Extractor extractor, ExtractionContext context) throws Exception;
}