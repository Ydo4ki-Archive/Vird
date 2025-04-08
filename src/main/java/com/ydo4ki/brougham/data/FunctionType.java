package com.ydo4ki.brougham.data;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Sulphuris
 * @since 4/8/2025 1:14 PM
 */
public final class FunctionType extends Type {
	private final Type returnType;
	private final Type[] params;
	
	public FunctionType(Type returnType, Type[] params) {
		this.returnType = returnType;
		this.params = params;
	}
	
	public Type getReturnType() {
		return returnType;
	}
	
	public Type[] getParams() {
		return params;
	}
	
	@Override
	public String toString() {
		return "function$"+returnType+"("+Arrays.stream(params).map(Type::toString).collect(Collectors.joining(" "))+")";
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		FunctionType that = (FunctionType) o;
		return Objects.equals(returnType, that.returnType) && Objects.deepEquals(params, that.params);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(returnType, Arrays.hashCode(params));
	}
}
