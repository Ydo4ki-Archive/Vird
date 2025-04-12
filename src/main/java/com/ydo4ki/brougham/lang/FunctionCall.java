package com.ydo4ki.brougham.lang;

import com.ydo4ki.brougham.Interpreter;
import com.ydo4ki.brougham.lang.constraint.EqualityConstraint;
import com.ydo4ki.brougham.lib.Std;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Sulphuris
 * @since 4/9/2025 9:38 AM
 */
@Getter
@EqualsAndHashCode
public final class FunctionCall {
	private final FunctionImpl function;
	private final ConversionRule cast_result;
	private final ConversionRule[] implicit_cast_calls;
	private final int castsCount;
	
	public FunctionCall(ConcreteFunction function, ConversionRule castResult, ConversionRule[] implicitCasts) {
		this.function = function.asFunctionImpl();
		cast_result = castResult;
		implicit_cast_calls = implicitCasts;
		int casts = 0;
		for (ConversionRule implicitCast : implicitCasts) {
			if (implicitCast != null) casts++;
		}
		this.castsCount = casts;
	}
	
	public static FunctionCall makeCall(Scope caller, ConcreteFunction function, TypeRef expectedType, TypeRef[] argsTypes) {
		int argsCount = argsTypes.length;
		TypeRef[] params = function.getRawType().getParams();
		if (params.length > 0) {
			TypeRef lastParam = params[params.length-1];
			if (lastParam.isVararg()) {
				int oldLen = params.length;
				params = Arrays.copyOf(params, argsCount);
				for (int i = oldLen; i < argsCount; i++) {
					params[i] = lastParam;
				}
			}
		}
		if (params.length != argsCount) return null;
		
		
		
		ConversionRule return_type_cast = null;
		TypeRef returnType = function.getRawType().getReturnType();
		if (expectedType != null) {
			if (returnType != null && !expectedType.isCompatibleWith(caller, returnType)) {
//				if (amIaCastFunction) return null;
				ConversionRule cast = caller.resolveConversionRule(new ConversionRule.ConversionTypes(expectedType, returnType));
				if (cast == null) {
					return null;
				}
				return_type_cast = cast;
			}
		}
		
		ConversionRule[] casts = new ConversionRule[argsTypes.length];
		for (int i = 0; i < argsTypes.length; i++) {
			TypeRef param;
			param = params[i];
			if (argsTypes[i].isCompatibleWith(caller, param)) continue; // cast = null (not needed)
			else {
				ConversionRule cast;
				TypeRef inputWeHave = argsTypes[i];
				ConversionRule.ConversionTypes ct = new ConversionRule.ConversionTypes(param, inputWeHave);
				if (Objects.equals(param, expectedType)) {
					cast = null;
				} else {
					cast = caller.resolveConversionRule(ct);
				}
				if (cast == null) {
					// todo make this normal
					if (inputWeHave.isCompatibleWith(caller, DList.TYPE(BracketsType.ROUND))) {
						DList value = (DList)((EqualityConstraint)inputWeHave.getConstraint()).getExpected();
						FunctionCall call = Interpreter.get_function_call(caller, param, value);
						if (param.isCompatibleWith(caller, call.getReturnType())) {
							cast = new ConversionRule(ct, Std.evaluate);
						} else return null;
					} else if (inputWeHave.isCompatibleWith(caller, Symbol.TYPE)) {
						Symbol value = (Symbol)((EqualityConstraint)inputWeHave.getConstraint()).getExpected();
						Val call = Interpreter.resolve(value);
						if (call != null && param.isCompatibleWith(caller, call.getType())) {
							cast = new ConversionRule(ct, Std.evaluate);
						} else return null;
					} else return null;
				}
				casts[i] = cast;
			}
		}
		return new FunctionCall(function, return_type_cast, casts);
	}
	
	public boolean needsResultCast() {
		return cast_result != null;
	}
	
	public TypeRef getReturnType() {
		if (needsResultCast()) return cast_result.getReturnType();
		return function.getRawType().getReturnType();
	}
	
	public Val invoke(Scope caller, Val[] args) {
		for (int i = 0; i < args.length; i++) {
			ConversionRule cast = implicit_cast_calls[i];
			if (cast == null) continue;
			args[i] = cast.invoke(caller, args[i]);
		}
		Val result = function.invoke(caller, args);
		if (cast_result != null) result = cast_result.invoke(caller, result);
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
	
	public boolean isExactMatch() {
		return castsCount == 0 && cast_result == null;
	}
}
