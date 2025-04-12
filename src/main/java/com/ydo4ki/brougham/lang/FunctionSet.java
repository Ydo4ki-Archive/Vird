package com.ydo4ki.brougham.lang;

import java.util.Arrays;

/**
 * @author Sulphuris
 * @since 4/12/2025 10:35 AM
 */
public interface FunctionSet extends Val {
	default FunctionCall makeCall(Scope caller, TypeRef expectedType, Val[] args) {
		return makeCall(caller, expectedType, Arrays.stream(args).map(Val::getType).toArray(TypeRef[]::new));
	}
	FunctionCall makeCall(Scope caller, TypeRef expectedType, TypeRef[] argsTypes);
}
