package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.lang.BracketsType;
import com.ydo4ki.vird.lang.expr.ExprList;
import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.lang.Val;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public final class DListBracketsConstraint extends Constraint {
	private final BracketsType expectedType;
	
	@Override
	public boolean test(Scope scope, Val value) {
		return value instanceof ExprList
				&& ((ExprList) value).getBracketsType() == expectedType;
	}
	
	@Override
	public boolean implies(Scope scope, Constraint other) {
		if (other instanceof DListBracketsConstraint) {
			return this.expectedType == ((DListBracketsConstraint) other).expectedType;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "DListBrackets(" + expectedType + ")";
	}
}
