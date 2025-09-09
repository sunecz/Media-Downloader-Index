package sune.app.mediadown.index.media;

import java.util.List;

public interface VideoMediaBase {
	
	MediaResolution resolution();
	double duration();
	List<String> codecs();
	int bandwidth();
	double frameRate();
	
	interface Builder<T extends VideoMediaBase, B extends VideoMediaBase.Builder<T, B>> {
		
		Builder<T, B> resolution(MediaResolution resolution);
		Builder<T, B> duration(double duration);
		Builder<T, B> codecs(List<String> codecs);
		Builder<T, B> bandwidth(int bandwidth);
		Builder<T, B> frameRate(double frameRate);
		
		MediaResolution resolution();
		double duration();
		List<String> codecs();
		int bandwidth();
		double frameRate();
	}
}