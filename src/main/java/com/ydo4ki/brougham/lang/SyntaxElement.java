package com.ydo4ki.brougham.lang;

/**
 * @author Sulphuris
 * @since 4/11/2025 4:36 PM
 */
public interface SyntaxElement extends Val {
	@Override
	default Type getRawType() {
		return SyntaxElementType.instance;
	}
	
	@Override
	TypeRef getType();
}

