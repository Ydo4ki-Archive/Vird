package com.ydo4ki.brougham.data;

/**
 * @since 4/7/2025 9:43 PM
 * @author Sulphuris
 */
public interface Val {
	Type getType();
	
	default boolean isType() {
		return false;
	}
	
	default Val resolve() {
		return this;
	}
}
