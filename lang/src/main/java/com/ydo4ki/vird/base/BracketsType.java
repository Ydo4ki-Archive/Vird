package com.ydo4ki.vird.base;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * @since 4/6/2025 8:43 PM
 * @author Sulphuris
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BracketsType {
	public static final BracketsType ROUND = new BracketsType('(',')');
	public static final BracketsType SQUARE = new BracketsType('[',']');
	public static final BracketsType BRACES = new BracketsType('{','}');
	
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
		BracketsType[] values = new BracketsType[]{ROUND, SQUARE, BRACES};
		for (BracketsType value : values) {
			if (ch == value.close || ch == value.open) return true;
		}
		return false;
	}
}
