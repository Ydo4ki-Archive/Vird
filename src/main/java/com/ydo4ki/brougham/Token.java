package com.ydo4ki.brougham;

/**
 * @since 4/6/2025 8:42 PM
 * @author Sulphuris
 */
public class Token extends Element {
	private final String value;
	
	public Token(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
