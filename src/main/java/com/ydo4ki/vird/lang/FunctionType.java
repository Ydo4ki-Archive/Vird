package com.ydo4ki.vird.lang;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Sulphuris
 * @since 4/8/2025 1:14 PM
 */
@Getter
@RequiredArgsConstructor
public final class FunctionType extends Type {
	private final TypeRef returnType;
	private final TypeRef[] params;
	
	public TypeRef[] getParams() {
		return params.clone();
	}
	
	public boolean isVarargFunction() {
		if (params.length == 0) return false;
		TypeRef last = params[params.length-1];
		return last.isVararg();
	}
	
	@Override
	public String toString() {
		return "function$"+returnType+"("+Arrays.stream(params).map(TypeRef::toString).collect(Collectors.joining(" "))+")";
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
