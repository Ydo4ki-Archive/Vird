package com.ydo4ki.brougham.lang;

public final class SyntaxElementType extends Type {
	public static final SyntaxElementType INSTANCE = new SyntaxElementType();
	
	private SyntaxElementType() {}
	
	@Override
	public String toString() {
		return "SyntaxElement";
	}
}
