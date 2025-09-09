package sune.app.mediadown.index.extract;

import java.util.Objects;

import sune.app.mediadown.index.entity.Program;

public abstract class ProgramExtractNode implements ExtractNode {
	
	protected final Program program;
	
	public ProgramExtractNode(Program program) {
		this.program = Objects.requireNonNull(program);
	}
	
	public abstract void extract(Extractor extractor, ExtractionContext context) throws Exception;
}
