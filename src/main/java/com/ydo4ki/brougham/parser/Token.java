package com.ydo4ki.brougham.parser;

/**
 * @since 4/6/2025 8:42 PM
 * @author Sulphuris
 */
public class Token extends Element {
	private final String value;
	
	public Token(Group parent, String value) {
		super(parent);
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	@Override
	protected String toStringContent(String linePrefix) {
		return linePrefix + this;
	}
}
