package com.ydo4ki.brougham.data;

/**
 * @since 4/7/2025 10:34 PM
 * @author Sulphuris
 */
public final class SymbolType extends Type {
	public static final SymbolType instance = new SymbolType();
	
	private SymbolType() {}
	
	@Override
	public String toString() {
		return "Symbol";
	}
}
