package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.base.ExprList;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.LangValidationException;
import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.lang.ValidatedValCall;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
public final class EqualityConstraint extends Constraint {
	@NonNull
	private final Val expected;
	
	@Override
	public boolean test(Scope scope, Val value) {
		return value.equals(expected);
	}
	
	@Override
	public boolean implies(Scope scope, Constraint other) {
		return other.test(scope, expected);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T extends Constraint> T extractImplication0(Class<T> type) {
		// bruh
		if (type == InstanceOfConstraint.class) return (T) InstanceOfConstraint.of(expected.getClass());
		if (type == OrConstraint.class) return (T) OrConstraint.of(this); // useless but must have proper behavior
		if (type == Struct.class) {
			if (expected instanceof Struct.StructVal) {
				return (T) ((Struct.StructVal) expected).struct();
			}
		}
		// ok the rest is probably too useless
		return null;
	}
	
	@Override
	public ValidatedValCall getInvocationConstraint(Scope scope, ExprList f) throws LangValidationException {
		return expected.invocation(scope, f);
	}
	
	@Override
	public String toString() {
		return "Equals(" + expected + ")";
	}
	
}
