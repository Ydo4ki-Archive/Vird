package com.ydo4ki.brougham;

import java.io.IOException;
import java.io.PrintStream;

/**
 * @author Sulphuris
 * @since 4/11/2025 1:04 AM
 */
public final class Location {
	private final Source src;
	private final int start, end;
	
	public Location(Source src, int start, int end) {
		this.src = src;
		this.start = start - 5;
		this.end = end - 5;
	}
	
	public void print(PrintStream out) {
		out.println(src + ":" + start + " - " + end);
		if (src != null) try {
			src.print(out, start, end);
		} catch (IOException e) {
			System.err.println("Cannot print src location");
			throw new RuntimeException(e);
		}
	}
}
