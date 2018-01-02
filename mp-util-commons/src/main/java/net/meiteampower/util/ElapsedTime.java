package net.meiteampower.util;

/**
 * 経過時間を保持する。
 *
 * @author kie
 */
public class ElapsedTime {

	private final int hours;
	private final int minutes;
	private final int seconds;
	private final int millis;

	private ElapsedTime(int hours, int minutes, int seconds, int millis) {
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		this.millis = millis;
	}

	public static ElapsedTime create(long startTime, long endTime) {

		long elapsed = endTime - startTime;
		int millis = (int)(elapsed % 1000);
		int totalSeconds = (int)(elapsed / 1000);
		int seconds = totalSeconds % 60;
		int totalMinutes = (int)(totalSeconds / 60);
		int minutes = totalMinutes % 60;
		int hours = totalMinutes / 60;

		return new ElapsedTime(hours, minutes, seconds, millis);
	}

	public final int getHours() {
		return hours;
	}

	public final int getMinutes() {
		return minutes;
	}

	public final int getSeconds() {
		return seconds;
	}

	public final int getMillis() {
		return millis;
	}
}
