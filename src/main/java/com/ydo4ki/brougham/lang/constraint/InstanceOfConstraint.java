package com.ydo4ki.brougham.lang.constraint;

import com.ydo4ki.brougham.lang.BracketsType;
import com.ydo4ki.brougham.lang.DList;
import com.ydo4ki.brougham.lang.Scope;
import com.ydo4ki.brougham.lang.Val;

public final class InstanceOfConstraint extends Constraint {
	private final Class<? extends Val> targetClass;
	
	public InstanceOfConstraint(Class<? extends Val> targetClass) {
		this.targetClass = targetClass;
	}
	
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
