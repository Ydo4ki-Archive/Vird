package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.lang.Val;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public final class InstanceOfConstraint extends Constraint {
	private final Class<? extends Val> targetClass;
	
	@Override
	public boolean test(Scope scope, Val value) {
		return targetClass.isInstance(value);
	}
	
	@Override
	public boolean implies(Scope scope, Constraint other) {
		if (other instanceof InstanceOfConstraint) {
			return ((InstanceOfConstraint) other).targetClass.isAssignableFrom(this.targetClass);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "InstanceOf(" + targetClass.getSimpleName() + ")";
	}
}
