package com.ydo4ki.brougham.lang.constraint;

import com.ydo4ki.brougham.lang.BracketsType;
import com.ydo4ki.brougham.lang.DList;
import com.ydo4ki.brougham.lang.Scope;
import com.ydo4ki.brougham.lang.Val;

public final class DListBracketsConstraint extends Constraint {
	private final BracketsType expectedType;
	
	public DListBracketsConstraint(BracketsType expectedType) {
		this.expectedType = expectedType;
	}
	
	@Override
	public boolean test(Scope scope, Val value) {
		return value instanceof DList
				&& ((DList) value).getBracketsType() == expectedType;
	}
	
	@Override
	public boolean implies(Scope scope, Constraint other) {
		// Не влечёт другие ограничения, кроме идентичного
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
