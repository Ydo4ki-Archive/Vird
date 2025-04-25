package com.ydo4ki.vird.lang.layout;

import java.util.NoSuchElementException;

/**
 * @since 4/25/2025 7:06 AM
 * @author Sulphuris
 */
public class EnumLayout extends Layout {
	private final String[] possibilities;
	
	public EnumLayout(String... possibilities) {
		super(getSizeFor(possibilities), getSizeFor(possibilities));
		this.possibilities = possibilities;
	}
	
	private static int getSizeFor(String[] possibilities) {
		if (possibilities.length == 0) return 0;
		return possibilities.length > 255 ? 2 : 1;
	}
	
	public int index(String element) throws NoSuchElementException {
		for (int i = 0; i < possibilities.length; i++) {
			if (element.equals(possibilities[i])) return i;
		}
		throw new NoSuchElementException("Enum value not found: " + element);
	}
}
