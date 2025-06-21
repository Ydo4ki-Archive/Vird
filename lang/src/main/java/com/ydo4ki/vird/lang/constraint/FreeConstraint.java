package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.Env;

// sys
// bob
// darts
// denmark
// ._?
public final class FreeConstraint extends PrimitiveConstraint {
	public static final FreeConstraint INSTANCE = new FreeConstraint();
	
	private FreeConstraint() {}
	
	@Override
	public boolean test(Env env, Val value) {
		return true;
	}
	
	@Override
	public boolean implies(Env env, Constraint other) {
		return other.equals(InstanceOfConstraint.of(Val.class));
	}
	
	@Override
	protected <T extends PrimitiveConstraint> T extractImplication0(Class<T> type) {
		return null;
	}
	
	@Override
	public String toString() {
		return "Free";
	}
}
