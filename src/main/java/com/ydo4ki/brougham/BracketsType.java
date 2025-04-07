package com.ydo4ki.brougham;

/**
 * @since 4/6/2025 8:43 PM
 * @author Sulphuris
 */
public enum BracketsType {
	ROUND('(',')'),
	SQUARE('[',']'),
	BRACES('{','}')
	;
	final char open;
	final char close;
	
	BracketsType(char open, char close) {
		this.open = open;
		this.close = close;
	}
	
	public static BracketsType byOpen(char ch) {
		switch (ch) {
			case '(': return ROUND;
			case '[': return SQUARE;
			case '{': return BRACES;
		}
		return null;
	}
}
