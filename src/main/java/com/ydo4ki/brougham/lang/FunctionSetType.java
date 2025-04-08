package com.ydo4ki.brougham.lang;

public final class FunctionSetType extends Type {
	public static final FunctionSetType instance = new FunctionSetType();
	
	private FunctionSetType() {}
	
	@Override
	public String toString() {
		return "FunctionSet";
	}
}
