package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.base.Constraint;
import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.base.Val;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public final class AndConstraint implements Constraint {
	private final Set<Constraint> constraints;
	
	public static AndConstraint of(Constraint... constraints) {
		return new AndConstraint(Arrays.stream(constraints).collect(Collectors.toSet()));
	}
	
	@Override
	public boolean test(Scope scope, Val value) {
		return constraints.stream().allMatch(c -> c.test(scope, value));
	}
	
	@Override
	public boolean implies(Scope scope, Constraint other) {
		return constraints.stream().anyMatch(c -> c.implies(scope, other));
	}
	
//	@Override
//	public int isStricterThan(Constraint other) {
//		if (other instanceof AndConstraint) {
//			AndConstraint andOther = ((AndConstraint) other);
//			boolean allStricter = true;
//			for (Constraint c : andOther.constraints) {
//				if (constraints.stream().noneMatch(p -> p.isStricterThan(c) <= 0)) {
//					allStricter = false;
//					break;
//				}
//			}
//			return allStricter ? -1 : 0;
//		}
//		return 0;
//	}
	
	@Override
	public String toString() {
		return "And" + constraints;
	}
}
