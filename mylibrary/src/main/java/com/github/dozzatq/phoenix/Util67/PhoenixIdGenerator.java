package com.github.dozzatq.phoenix.Util67;

import java.util.Date;

public class PhoenixIdGenerator {
	// Modeled after base64 web-safe chars, but ordered by ASCII.
	private final static String PUSH_CHARS = "-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz";
	private static long lastPushTime = 0L;

	public static String generatePushId() {
		// Timestamp of last push, used to prevent local collisions if you push twice in one ms.

		// We generate 72-bits of randomness which get turned into 12 characters and
		// appended to the timestamp to prevent collisions with other clients. We store the last
		// characters we generated because in the event of a collision, we'll use those same
		// characters except "incremented" by one.
		char[] lastRandChars = new char[72];

		long now = new Date().getTime();

		boolean duplicateTime = (now == lastPushTime);

		char[] timeStampChars = new char[8];
		for (int i = 7; i >= 0; i--) {
			final long module = now % 64;
			timeStampChars[i] = PUSH_CHARS.charAt(Long.valueOf(module).intValue());
			now = (long) Math.floor(now / 64);
		}
		if (now != 0)
			throw new AssertionError("We should have converted the entire timestamp.");

		String id = new String(timeStampChars);
		if (!duplicateTime) {
			for (int i = 0; i < 12; i++) {
				final double times = Math.random() * 64;
				lastRandChars[i] = (char) Math.floor(Double.valueOf(times).intValue());

			}
		} else {
			// If the timestamp hasn't changed since last push, use the same random number,
 			//except incremented by 1.
			int lastValueOfInt=0;
			for (int i = 11; i >= 0 && lastRandChars[i] == 63; i--) {
				lastValueOfInt = i;
				lastRandChars[i] = 0;
			}
			lastRandChars[lastValueOfInt]++;
		}
		for (int i = 0; i < 12; i++) {
			id += PUSH_CHARS.charAt(lastRandChars[i]);
		}
		if (id.length() != 20)
			throw new AssertionError("Length should be 20.");

		return id;
	};
	
}