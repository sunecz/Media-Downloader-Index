package sune.app.mediadown.index.segment;

import java.util.List;

public interface FileSegmentsHolder {
	
	List<? extends FileSegment> segments();
	int count();
	double duration();
}