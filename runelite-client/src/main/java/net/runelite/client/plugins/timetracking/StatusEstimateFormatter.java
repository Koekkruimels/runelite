package net.runelite.client.plugins.timetracking;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class StatusEstimateFormatter
{
	public static String getFormattedEstimate(long remainingSeconds, boolean useRelativeTime)
	{
		if (useRelativeTime)
		{
			StringBuilder sb = new StringBuilder("in ");
			long duration = (remainingSeconds + 59) / 60;
			long minutes = duration % 60;
			long hours = (duration / 60) % 24;
			long days = duration / (60 * 24);
			if (days > 0)
			{
				sb.append(days).append("d ");
			}
			if (hours > 0)
			{
				sb.append(hours).append("h ");
			}
			if (minutes > 0)
			{
				sb.append(minutes).append("m ");
			}
			return sb.toString();
		}
		else
		{
			StringBuilder sb = new StringBuilder();
			LocalDateTime endTime = LocalDateTime.now().plus(remainingSeconds, ChronoUnit.SECONDS);
			LocalDateTime currentTime = LocalDateTime.now();
			if (endTime.getDayOfWeek() != currentTime.getDayOfWeek())
			{
				sb.append(endTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault())).append(" ");
			}
			sb.append(String.format("at %d:%02d", endTime.getHour(), endTime.getMinute()));
			return sb.toString();
		}
	}
}
