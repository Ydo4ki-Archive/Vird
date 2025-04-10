package com.ydo4ki.brougham.lang;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * @author Sulphuris
 * @since 4/9/2025 12:50 AM
 */
public final class FunctionImpl implements Val {
	private final FunctionType type;
//	private final Symbol[] paramNames;
	private final BiFunction<DList, Val[], Val> transformer;
	private final boolean pure;
	
	public FunctionImpl(FunctionType type, BiFunction<DList, Val[], Val> transformer, boolean pure) {
		this.type = type;
		this.transformer = transformer;
//		if (paramNames.length != type.getParams().length)
//			throw new IllegalArgumentException("paramNames and paramTypes length mismatch (" +
//					paramNames.length + " != " + type.getParams().length + ")");
//		this.paramNames = paramNames;
		this.pure = pure;
	}
	
	public boolean isPure() {
		return pure;
	}
	
	//	public Symbol[] getParamNames() {
//		return paramNames;
//	}
	
	@Override
	public FunctionType getRawType() {
		return type;
	}
	
	public Val invoke(DList caller, Val[] args) {
		return Objects.requireNonNull(
				transformer.apply(caller, args),
				"Function just returned null. This is outrageous. " + Arrays.toString(args)
		);
	}
	
	@Override
	public String toString() {
		return "f!"+type;
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
