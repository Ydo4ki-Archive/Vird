package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.base.Expr;
import com.ydo4ki.vird.base.ExprList;
import com.ydo4ki.vird.base.Location;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.LangValidationException;
import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.lang.ValidatedValCall;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public final class InstanceOfConstraint implements Constraint {
	private final Class<? extends Val> targetClass;
	
	@Override
	public boolean test(Scope scope, Val value) {
		return targetClass.isInstance(value);
	}
	
	@Override
	public boolean implies(Scope scope, Constraint other) {
		if (other instanceof InstanceOfConstraint)
			return ((InstanceOfConstraint) other).targetClass.isAssignableFrom(this.targetClass);
		
		return false;
	}
	
	@Override
	public ValidatedValCall getInvocationConstraint(Scope scope, ExprList.Round f) throws LangValidationException {
		throw new LangValidationException(f.getLocation(), "Not callable");
	}
	
	@Override
	public String toString() {
		return "InstanceOf(" + targetClass.getSimpleName() + ")";
	}
}
