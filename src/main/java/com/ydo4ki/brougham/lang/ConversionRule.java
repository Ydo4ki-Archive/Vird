package com.ydo4ki.brougham.lang;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * @author Sulphuris
 * @since 4/12/2025 10:39 AM
 */
@EqualsAndHashCode
@Getter
public class ConversionRule implements ConcreteFunction {
	private final ConversionTypes types;
	private final FunctionImpl function;
	
	public ConversionRule(ConversionTypes types, FunctionImpl function) {
		this.types = types;
		this.function = function;
		if (!function.isPure() || !function.getRawType().getReturnType().equals(types.getTargetType())
				|| function.getRawType().getParams().length != 1
				|| function.getRawType().getParams()[0].equals(types.getFrom()))
			throw new IllegalArgumentException("Invalid function signature: " + function.getType() + " (" + types + " expected)");
	}
	
	public TypeRef getReturnType() {
		return types.targetType;
	}
	
	public Val invoke(Scope caller, Val arg) {
		return function.invoke(caller, new Val[]{arg});
	}
	
	@Override
	public FunctionType getRawType() {
		return function.getRawType();
	}
	
	@Override
	public FunctionImpl asFunctionImpl() {
		return function;
	}
	
	@Override
	public FunctionCall makeCall(Scope caller, TypeRef expectedType, TypeRef[] argsTypes) {
		return function.makeCall(caller, expectedType, argsTypes);
	}
	
	public static @Data class ConversionTypes {
		private final TypeRef targetType;
		private final TypeRef from;
		
		@Override
		public String toString() {
			return "(" + from + ") -> " + targetType;
		}
	}
}
