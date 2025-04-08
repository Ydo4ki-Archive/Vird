package com.ydo4ki.brougham.lang;

/**
 * @since 4/7/2025 9:43 PM
 * @author Sulphuris
 */
public interface Val {
	Type getType();
	
	default Val resolve() {
		return this;
	}
}
