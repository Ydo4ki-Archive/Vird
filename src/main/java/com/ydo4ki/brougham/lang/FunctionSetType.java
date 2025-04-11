package com.ydo4ki.brougham.lang;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public final class FunctionSetType extends Type {
	public static final FunctionSetType instance = new FunctionSetType();
	
	private FunctionSetType() {}
	
	@Override
	public String toString() {
		return "FunctionSet";
	}
}
