package com.ydo4ki.brougham.lang;

/**
 * @author Sulphuris
 * @since 4/11/2025 4:36 PM
 */
public interface SyntaxStructure extends Val {
	@Override
	default Type getRawType() {
		return SyntaxStructureType.instance;
	}
	
	@Override
	TypeRef getType();
}

