package sune.app.mediadown.index.entity;

import java.util.Set;

public interface HasMedia extends Entity {

	void addVideo(Video video);
	void removeVideo(Video video);
	Set<Video> getVideos();
}
