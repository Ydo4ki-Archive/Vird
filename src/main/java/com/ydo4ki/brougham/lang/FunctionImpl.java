package com.ydo4ki.brougham.lang;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Sulphuris
 * @since 4/9/2025 12:50 AM
 */
public final class FunctionImpl implements Val {
	private final FunctionType type;
//	private final Symbol[] paramNames;
	private final Function<Val[], Val> transformer;
	
	public FunctionImpl(FunctionType type, Function<Val[], Val> transformer) {
		this.type = type;
		this.transformer = transformer;
//		if (paramNames.length != type.getParams().length)
//			throw new IllegalArgumentException("paramNames and paramTypes length mismatch (" +
//					paramNames.length + " != " + type.getParams().length + ")");
//		this.paramNames = paramNames;
	}
	
//	public Symbol[] getParamNames() {
//		return paramNames;
//	}
	
	@Override
	public FunctionType getType() {
		return type;
	}
	
	public Val invoke(Val[] args) {
		return transformer.apply(args);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		FunctionImpl function = (FunctionImpl) o;
		return Objects.equals(type, function.type);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type);
	}
}
