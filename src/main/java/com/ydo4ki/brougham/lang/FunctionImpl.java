package com.ydo4ki.brougham.lang;

import com.ydo4ki.brougham.ThisIsNotTheBookClubException;
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
public final class FunctionImpl implements FunctionSet {
	private final FunctionType type;
	private final BiFunction<Scope, Val[], Val> transformer;
	@Getter
	private final boolean pure;
	
	@Override
	public FunctionType getRawType() {
		return type;
	}
	
	public Val invoke(Scope caller, Val[] args) {
		TypeRef[] params = type.getParams();
		int Len = args.length;
		int paramsLen = params.length;
		boolean vararg = type.isVarargFunction();
		for (int i = 0; i < Len; i++) {
			TypeRef param;
			if (vararg && i >= paramsLen) {
				param = params[params.length-1];
			} else {
				param = params[i];
			}
			if (!param.matches(caller, args[i])) {
				throw new ThisIsNotTheBookClubException("Invalid input args: " +
						Arrays.toString(args) + " (" + Arrays.toString(params) + " types expected)");
			}
		}
		Val ret = Objects.requireNonNull(
				transformer.apply(caller, args),
				"Function just returned null. This is outrageous. " + Arrays.toString(args)
		);
		if (type.getReturnType() != null && !type.getReturnType().matches(caller, ret))
			throw new ThisIsNotTheBookClubException("Invalid return value: " + ret + "( " + type.getReturnType() + " expected)");
		return ret;
	}
	
	@Override
	public String toString() {
		return "f!"+type;
	}
	
	public boolean isTemplate() {
		if (!isPure() || (getRawType().getReturnType() != null
				&& !(getRawType().getReturnType().getBaseType() instanceof FunctionType))) {
			return false;
		}
//		for (TypeRef param : type.getParams()) {
//			if (!(param.getType() instanceof MetaType)) return false;
//		}
		return true;
	}
	
	@Override
	public FunctionCall makeCall(Scope caller, TypeRef expectedType, TypeRef[] argsTypes) {
		return FunctionCall.makeCall(caller, this, expectedType, argsTypes);
	}
}
