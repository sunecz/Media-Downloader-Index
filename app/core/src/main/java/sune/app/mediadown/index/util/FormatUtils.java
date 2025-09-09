package sune.app.mediadown.index.util;

import java.util.concurrent.TimeUnit;

public final class FormatUtils {

	private FormatUtils() {
	}

	public static String formatDuration(long time, TimeUnit unit) {
		StringBuilder builder = new StringBuilder("PT");
		boolean written = false;

		long hours = unit.toHours(time);
		if(hours > 0L) {
			builder.append(hours).append('H');
			time = time - unit.convert(hours, TimeUnit.HOURS);
			written = true;
		}

		long minutes = unit.toMinutes(time);
		if(minutes > 0L || written) {
			if(written) {
				builder.append(' ');
			}

			builder.append(minutes).append('M');
			time = time - unit.convert(minutes, TimeUnit.MINUTES);
			written = true;
		}

		if(written) {
			builder.append(' ');
		}

		long seconds = unit.toSeconds(time);
		builder.append(seconds);
		time = time - unit.convert(seconds, TimeUnit.SECONDS);

		long millis = unit.toMillis(time);
		if(millis > 0L) {
			builder.append('.').append(String.format("%03d", millis));
		}

		builder.append('S');

		return builder.toString();
	}
}