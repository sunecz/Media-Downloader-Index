package sune.app.mediadown.index.service;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sune.app.mediadown.index.Website;
import sune.app.mediadown.index.entity.Audio;
import sune.app.mediadown.index.entity.HasMedia;
import sune.app.mediadown.index.media.AudioMedia;
import sune.app.mediadown.index.media.MediaQuality;
import sune.app.mediadown.index.model.AudioDao;
import sune.app.mediadown.index.normalization.Normalizer;
import sune.app.mediadown.index.util.FormatUtils;

@Service
public class AudioService {

	private final Normalizer normalizer;
	private final AudioDao dao;

	public AudioService(Normalizer normalizer, AudioDao dao) {
		this.normalizer = normalizer;
		this.dao = dao;
	}

	@Transactional
	public Audio getOrCreate(Website website, HasMedia owner, AudioMedia media) {
		URI uri = MediaService.mediaUri(normalizer, "audios", website, owner, media);
		boolean updated = true;

		Audio audio;
		if((audio = dao.findByUri(uri)) == null) {
			audio = new Audio();
			updated = false;
		}

		audio.setUri(uri);
		audio.setDuration(FormatUtils.formatDuration((long) (media.duration() * 1000), TimeUnit.MILLISECONDS));
		audio.setFormat(media.format().mimeTypes().get(0));
		audio.setContentSize(media.size());
		audio.setContentUrl(media.uri());
		audio.setQuality(MediaQuality.removeNameSuffix(media.quality().name()));

		MediaQuality.QualityValue qualityValue = media.quality().value();

		if(qualityValue instanceof MediaQuality.AudioQualityValue audioQualityValue) {
			audio.setBitrate((long) audioQualityValue.bitRate());
		}
		
		audio.setRequiresSubscription(
			Optional.<Boolean>ofNullable(media.metadata().get("requiresSubscription")).orElse(false)
		);

		MediaService.hydrate(audio, media.metadata());

		if(updated) {
			dao.update(audio);
		} else {
			dao.create(audio);
		}

		return audio;
	}

	@Transactional
	public void update(Audio object) {
		dao.update(object);
	}
}