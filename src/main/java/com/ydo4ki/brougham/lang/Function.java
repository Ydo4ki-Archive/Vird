package com.ydo4ki.brougham.lang;

/**
 * @author Sulphuris
 * @since 4/8/2025 1:13 PM
 */
public final class Function implements Val {
	private final FunctionType type;
	private final Symbol[] paramNames;
	
	public Function(FunctionType type, Symbol[] paramNames) {
		this.type = type;
		if (paramNames.length != type.getParams().length)
			throw new IllegalArgumentException("paramNames and paramTypes length mismatch (" +
					                                   paramNames.length + " != " + type.getParams().length + ")");
		this.paramNames = paramNames;
	}
	
	public Symbol[] getParamNames() {
		return paramNames;
	}
	
	@Override
	public FunctionType getType() {
		return type;
	}
}
