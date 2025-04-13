package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.lang.Val;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
public final class EqualityConstraint extends Constraint {
	private final Val expected;
	
	@Override
	public boolean test(Scope scope, Val value) {
		return value.equals(expected);
	}
	
	@Override
	public boolean implies(Scope scope, Constraint other) {
		if (other instanceof EqualityConstraint) {
			return this.expected.equals(((EqualityConstraint) other).expected);
		}
		return other.test(scope, expected);
	}
	@Override
	public String toString() {
		return "Equals(" + expected + ")";
	}
}
