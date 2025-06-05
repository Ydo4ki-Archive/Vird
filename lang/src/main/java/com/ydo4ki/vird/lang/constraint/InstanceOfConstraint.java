package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.Scope;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = false)
@Getter
public class InstanceOfConstraint extends Constraint {
	private final Class<? extends Val> targetClass;
	
	protected InstanceOfConstraint(Class<? extends Val> targetClass) {
		this.targetClass = targetClass;
	}
	
	public static InstanceOfConstraint of(Class<? extends Val> targetClass) {
		return new InstanceOfConstraint(targetClass);
	}
	
	@Override
	public boolean test(Scope scope, Val value) {
		return targetClass.isInstance(value);
	}
	
	@Override
	public boolean implies(Scope scope, Constraint other) {
		if (other instanceof InstanceOfConstraint)
			return ((InstanceOfConstraint) other).targetClass.isAssignableFrom(this.targetClass);
		
		return false;
	}
	
	@Override
	protected <T extends Constraint> T extractImplication0(Class<T> type) {
		return null;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName().replace("Constraint", "") + "(" + targetClass.getSimpleName() + ")";
	}
}
