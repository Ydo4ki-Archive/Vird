package com.ydo4ki.brougham.lang;

import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = false)
public final class FunctionType extends Type {
	private final TypeRef returnType;
	private final TypeRef[] params;
	
	public boolean isVarargFunction() {
		if (params.length == 0) return false;
		TypeRef last = params[params.length-1];
		return last.isVararg();
	}
	
	@Override
	public String toString() {
		return "function$"+returnType+"("+Arrays.stream(params).map(TypeRef::toString).collect(Collectors.joining(" "))+")";
	}
}
