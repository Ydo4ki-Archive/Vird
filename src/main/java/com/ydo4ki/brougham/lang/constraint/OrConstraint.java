package com.ydo4ki.brougham.lang.constraint;

import com.ydo4ki.brougham.lang.Scope;
import com.ydo4ki.brougham.lang.Val;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public final class OrConstraint extends Constraint {
	private final Set<Constraint> constraints;
	
	public static OrConstraint of(Constraint... constraints) {
		return new OrConstraint(Arrays.stream(constraints).collect(Collectors.toSet()));
	}
	
	@Override
	public boolean test(Scope scope, Val value) {
		return constraints.stream().anyMatch(c -> c.test(scope, value));
	}
	
	@Override
	public boolean implies(Scope scope, Constraint other) {
		return constraints.stream().allMatch(c -> c.implies(scope, other));
	}
	
	@Override
	public String toString() {
		return "Or" + constraints;
	}
}
