package sune.app.mediadown.index.service;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sune.app.mediadown.index.Website;
import sune.app.mediadown.index.entity.HasMedia;
import sune.app.mediadown.index.entity.Video;
import sune.app.mediadown.index.media.MediaQuality;
import sune.app.mediadown.index.media.MediaResolution;
import sune.app.mediadown.index.media.VideoMedia;
import sune.app.mediadown.index.model.VideoDao;
import sune.app.mediadown.index.normalization.Normalizer;
import sune.app.mediadown.index.util.FormatUtils;

@Service
public class VideoService {

	private final Normalizer normalizer;
	private final VideoDao dao;

	public VideoService(Normalizer normalizer, VideoDao dao) {
		this.normalizer = normalizer;
		this.dao = dao;
	}

	@Transactional
	public Video getOrCreate(Website website, HasMedia owner, VideoMedia media) {
		URI uri = MediaService.mediaUri(normalizer, "videos", website, owner, media);
		boolean updated = true;

		Video video;
		if((video = dao.findByUri(uri)) == null) {
			video = new Video();
			updated = false;
		}

		video.setUri(uri);
		video.setDuration(FormatUtils.formatDuration((long) (media.duration() * 1000), TimeUnit.MILLISECONDS));
		video.setFormat(media.format().mimeTypes().get(0));
		video.setContentSize(media.size());
		video.setContentUrl(media.uri());
		video.setQuality(MediaQuality.removeNameSuffix(media.quality().name()));

		MediaResolution resolution = media.resolution();

		if(resolution != MediaResolution.UNKNOWN) {
			video.setWidth(resolution.width());
			video.setHeight(resolution.height());
		}
		
		video.setRequiresSubscription(
			Optional.<Boolean>ofNullable(media.metadata().get("requiresSubscription")).orElse(false)
		);

		MediaService.hydrate(video, media.metadata());

		if(updated) {
			dao.update(video);
		} else {
			dao.create(video);
		}

		return video;
	}

	@Transactional
	public void update(Video object) {
		dao.update(object);
	}
}