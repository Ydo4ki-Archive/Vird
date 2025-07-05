package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.ast.ExprList;
import com.ydo4ki.vird.lang.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
public final class EqualityConstraint extends PrimitiveConstraint {
	@NonNull
	private final Val expected;
	
	@Override
	public boolean test(Env env, Val value) {
		return value.equals(expected);
	}
	
	@Override
	public boolean implies(Env env, Constraint other) {
		return other.test(env, expected);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T extends PrimitiveConstraint> T extractImplication0(Class<T> type) {
		// bruh
		if (type == InstanceOfConstraint.class) return (T) InstanceOfConstraint.of(expected.getClass());
		if (type == Struct.class) {
			if (expected instanceof Struct.StructVal) {
				return (T) ((Struct.StructVal) expected).struct();
			}
		}
		// ok the rest is probably too useless
		return null;
	}
	
	@Override
	public ValidatedValCall getInvocationConstraint(Env env, ExprList f) throws LangValidationException {
		return expected.invocation(env, f);
	}
	
	@Override
	public String toString() {
		return "Equals(" + expected + ")";
	}
	
}
