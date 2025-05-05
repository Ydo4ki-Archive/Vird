package com.ydo4ki.std;

import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @since 4/25/2025 5:42 PM
 * @author Sulphuris
 */
public final class AsciiT {
	public static int maxLength(String[] strings) {
		return maxLength(Arrays.stream(strings));
	}
	public static int maxLength(Iterable<String> strings) {
		return maxLength(StreamSupport.stream(strings.spliterator(), true));
	}
	public static int maxLength(Stream<String> strings) {
		return strings.mapToInt(String::length).max().orElse(0);
	}
}
