package com.ydo4ki.brougham.lang.constraint;

import com.ydo4ki.brougham.lang.Scope;
import com.ydo4ki.brougham.lang.Val;

public final class FreeConstraint extends Constraint {
	public static final FreeConstraint INSTANCE = new FreeConstraint();
	
	private FreeConstraint() {}
	
	@Override
	public boolean test(Scope scope, Val value) {
		return true;
	}
	
	@Override
	public boolean implies(Scope scope, Constraint other) {
		return true;
	}
	
	@Override
	public String toString() {
		return "Free";
	}
}
