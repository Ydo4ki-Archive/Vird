package com.ydo4ki.brougham.lang;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * @author Sulphuris
 * @since 4/9/2025 12:50 AM
 */
@EqualsAndHashCode
@RequiredArgsConstructor
public final class Func implements Val {
	private final FunctionType type;
	private final BiFunction<Scope, Val[], Val> transformer;
	@Getter
	private final boolean pure;
	
	@Override
	public FunctionType getRawType() {
		return type;
	}
	
	public static Func intrinsic(FunctionType type, boolean pure, BiFunction<Scope, Val[], Val> transformer) {
		return new Func(type, transformer, pure);
	}
	public static Func intrinsic(TypeRef returnType, TypeRef[] params, boolean pure, BiFunction<Scope, Val[], Val> transformer) {
		return intrinsic(new FunctionType(returnType, params), pure, transformer);
	}
	
	public Val invoke(Scope caller, Val[] args) {
		TypeRef[] params = type.getParams();
		int argsLen = args.length;
		int paramsLen = params.length;
		boolean vararg = type.isVarargFunction();
		if (!vararg ? argsLen != paramsLen : argsLen < paramsLen-1)
			throw new IllegalArgumentException("Invalid amount of input args: " + Arrays.toString(args) + " (" + paramsLen + " expected)");
		for (int i = 0; i < argsLen; i++) {
			TypeRef param;
			if (vararg && i >= paramsLen) {
				param = params[params.length - 1];
			} else {
				param = params[i];
			}
			if (!param.matches(caller, args[i])) {
				ConversionRule rule = caller.resolveConversionRule(new ConversionRule.ConversionTypes(param, args[i].getType()));
				if (rule == null) {
					throw new IllegalArgumentException("Cannot implicitly cast " +
							args[i].getType() + " (" + (args[i]) + ") to " + param);
				}
				args[i] = rule.invoke(caller, args[i]);
//
//				throw new ThisIsNotTheBookClubException("Invalid input args: " +
//						Arrays.toString(args) + " (" + Arrays.toString(params) + " types expected)");
			}
		}
		// lmao
		Val ret = Objects.requireNonNull(
				transformer.apply(caller, args),
				"Function just returned null. This is outrageous. It's unfair. How can you be a function, and not return a value?" + Arrays.toString(args)
		);
		if (type.getReturnType() != null && !type.getReturnType().matches(caller, ret))
			throw new IllegalArgumentException("Invalid return value: " + ret + "( " + type.getReturnType() + " expected)");
		return ret;
	}
	
	@Override
	public String toString() {
		return "f!" + type;
	}
}
