package sune.app.mediadown.index.service;

import java.net.URI;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import sune.app.mediadown.index.Website;
import sune.app.mediadown.index.entity.Audio;
import sune.app.mediadown.index.entity.HasMedia;
import sune.app.mediadown.index.entity.Types;
import sune.app.mediadown.index.entity.Video;
import sune.app.mediadown.index.media.AudioMedia;
import sune.app.mediadown.index.media.Media;
import sune.app.mediadown.index.media.MediaMetadata;
import sune.app.mediadown.index.media.MediaQuality;
import sune.app.mediadown.index.media.MediaType;
import sune.app.mediadown.index.media.VideoMedia;
import sune.app.mediadown.index.normalization.Normalizer;
import sune.app.mediadown.index.util.DatabaseDataStorable;

@Service
public class MediaService {

	private final VideoService videoService;
	private final AudioService audioService;

	public MediaService(
		VideoService videoService,
		AudioService audioService
	) {
		this.videoService = videoService;
		this.audioService = audioService;
	}

	private static String mediaQualityName(MediaQuality quality) {
		MediaQuality.QualityValue qualityValue = quality.value();

		if(qualityValue instanceof MediaQuality.VideoQualityValue videoQualityValue) {
			int bandwidth = videoQualityValue.bandwidth();
			return MediaQuality.removeNameSuffix(quality.name()) + (bandwidth > 0 ? "-" + bandwidth : "");
		} else if(qualityValue instanceof MediaQuality.AudioQualityValue audioQualityValue) {
			int bandwidth = audioQualityValue.bandwidth();
			int bitRate = audioQualityValue.bitRate();
			int sampleRate = audioQualityValue.sampleRate();
			return MediaQuality.removeNameSuffix(quality.name()) + (
				bandwidth > 0 || bitRate > 0 || sampleRate > 0
					? "-" + String.format("%s.%s.%s", bandwidth, bitRate, sampleRate)
					: ""
			);
		} else {
			return MediaQuality.removeNameSuffix(quality.name());
		}
	}

	public static URI mediaUri(Normalizer normalizer, String type, Website website, HasMedia owner, Media media) {
		URI identifier = owner.getIdentifier();

		return Types.mdiUri(String.format(
			"%s/%s/%s/%s",
			type, website.name(),
			normalizer.normalizeUriComponent(
				String.format("%s%s%s", identifier.getHost(), identifier.getPath(), identifier.getQuery())
			),
			normalizer.normalizeUriComponent(
				String.format("%s_%s", media.format(), mediaQualityName(media.quality()))
			)
		));
	}

	public static void hydrate(DatabaseDataStorable storable, MediaMetadata metadata) {
		if(metadata.isProtected()) {
			storable.set(
				"protection",
				metadata.protections().stream()
					.map((p) -> String.format("%s:%s:%s:%s", p.scheme(), p.contentType(), p.keyId(), p.content()))
					.collect(Collectors.toSet())
			);
		}
	}

	public Video toVideo(Website website, Media media, HasMedia mediaOwner) {
		VideoMedia video = Media.findOfType(media, MediaType.VIDEO);
		AudioMedia audio = Media.findOfType(media, MediaType.AUDIO);

		Video videoResult = videoService.getOrCreate(website, mediaOwner, video);

		if(audio != null) {
			Audio audioResult = audioService.getOrCreate(website, mediaOwner, audio);
			videoResult.setAudio(audioResult);
		}

		return videoResult;
	}
}