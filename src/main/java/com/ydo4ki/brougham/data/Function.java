package com.ydo4ki.brougham.data;

/**
 * @author Sulphuris
 * @since 4/8/2025 1:13 PM
 */
public final class Function implements Val {
	private final FunctionType type;
	
	public Function(FunctionType type) {
		this.type = type;
	}
	
	@Override
	public Type getType() {
		return type;
	}
}
