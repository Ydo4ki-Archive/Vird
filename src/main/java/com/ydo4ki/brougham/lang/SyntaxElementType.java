package com.ydo4ki.brougham.lang;

public final class SyntaxElementType extends Type {
	public static final SyntaxElementType instance = new SyntaxElementType();
	
	private SyntaxElementType() {}
	
	@Override
	public String toString() {
		return "SyntaxElement";
	}
}
