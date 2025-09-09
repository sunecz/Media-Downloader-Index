package sune.app.mediadown.index.segment;

import java.net.URI;

public interface FileSegment {
	
	URI uri();
	long size();
	double duration();
}