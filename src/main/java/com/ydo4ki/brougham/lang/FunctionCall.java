package com.ydo4ki.brougham.lang;

import java.util.Arrays;

/**
 * @author Sulphuris
 * @since 4/9/2025 9:38 AM
 */
public final class FunctionCall {
	private final FunctionImpl function;
	private final FunctionCall cast_result;
	private final FunctionCall[] implicit_cast_calls;
	private final int casts;
	
	public FunctionCall(FunctionImpl function, FunctionCall castResult, FunctionCall[] implicitCasts) {
		this.function = function;
		cast_result = castResult;
		implicit_cast_calls = implicitCasts;
		int casts = 0;
		for (FunctionCall implicitCast : implicitCasts) {
			if (implicitCast != null) casts++;
		}
		this.casts = casts;
	}
	
	public boolean needsResultCast() {
		return cast_result != null;
	}
	
	public int castsCount() {
		return casts;
	}
	
	public TypeRef getReturnType() {
		if (needsResultCast()) return cast_result.getReturnType();
		return function.getRawType().getReturnType();
	}
	
	public Val invoke(DList caller, Val[] args) {
		for (int i = 0; i < args.length; i++) {
			FunctionCall cast = implicit_cast_calls[i];
			if (cast == null) continue;
			args[i] = cast.invoke(caller, new Val[]{args[i]});
		}
		Val result = function.invoke(caller, args);
		if (cast_result != null) result = cast_result.invoke(caller, new Val[]{result});
		return result;
	}
	
	@Override
	public String toString() {
		return "FunctionCall{" +
				"function=" + function +
				", cast_result=" + cast_result +
				", implicit_cast_calls=" + Arrays.toString(implicit_cast_calls) +
				'}';
	}
}
