package sune.app.mediadown.index;

import sune.app.mediadown.index.extract.ProgramExtractNode;
import sune.app.mediadown.index.task.ListTask;

public interface Website {
	
	ListTask<ProgramExtractNode> getPrograms() throws Exception;
	
	String name();
	String title();
}