package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.base.Constraint;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.Scope;
import lombok.RequiredArgsConstructor;

/**
 * @since 4/23/2025 10:55 AM
 * @author Sulphuris
 */
@RequiredArgsConstructor
public class ComparisonConstraint implements Constraint {
	public enum Operator { GT, GTE, LT, LTE }
	
	private final String variable;
	private final Operator operator;
	private final Comparable<Val> value;
	
	@Override
	public boolean test(Scope scope, Val val) {
//		if (!(val instanceof IntValue)) return false;
		boolean result;
		switch (operator) {
			case GT:
				result = value.compareTo(val) < 0;
				break;
			case GTE:
				result = value.compareTo(val) <= 0;
				break;
			case LT:
				result = value.compareTo(val) > 0;
				break;
			case LTE:
				result = value.compareTo(val) >= 0;
				break;
			default:
				throw new AssertionError();
		}
		return result;
	}
	
	@Override
	public boolean implies(Scope scope, Constraint other) {
		if (other instanceof ComparisonConstraint) {
			ComparisonConstraint otherC = (ComparisonConstraint) other;
			if (!variable.equals(otherC.variable)) return false;
			
			// x > 5 -> x >= 5
			return (this.operator == Operator.GT && otherC.operator == Operator.GTE && this.value.compareTo(otherC.value) >= 0)
					|| (this.operator == Operator.LT && otherC.operator == Operator.LTE && this.value.compareTo(otherC.value) <= 0);
		}
		return false;
	}
}