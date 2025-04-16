package com.ydo4ki.vird;

import lombok.RequiredArgsConstructor;

/**
 * @since 4/6/2025 8:43 PM
 * @author Sulphuris
 */
@RequiredArgsConstructor
public enum BracketsType {
	ROUND('(',')'),
	SQUARE('[',']'),
	BRACES('{','}')
	;
	public final char open;
	public final char close;
	
	public static BracketsType byOpen(char ch) {
		switch (ch) {
			case '(': return ROUND;
			case '[': return SQUARE;
			case '{': return BRACES;
		}
		return null;
	}
	
	public static boolean isBracket(char ch) {
		for (BracketsType value : values()) {
			if (ch == value.close || ch == value.open) return true;
		}
		return false;
	}
}
