package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.Blob;
import com.ydo4ki.vird.lang.Scope;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @since 5/9/2025 10:03 AM
 * @author Sulphuris
 */
@RequiredArgsConstructor(staticName = "of")
@Getter
public class ComparisonConstraint implements Constraint {
	private final Blob than;
	private final Op op;
	
	@Override
	public boolean test(Scope scope, Val value) {
		if (value instanceof Blob) {
			return op == Op.GREATER
					? ((Blob) value).compareTo(than) > 0
					: ((Blob) value).compareTo(than) < 0;
		}
		return false;
	}
	
	@Override
	public boolean implies(Scope scope, Constraint other) {
		if (other instanceof ComparisonConstraint) return op == Op.GREATER
				? ((ComparisonConstraint) other).getThan().compareTo(this.getThan()) <= 0
				: ((ComparisonConstraint) other).getThan().compareTo(this.getThan()) >= 0;
		if (other instanceof InstanceOfConstraint) return ((InstanceOfConstraint) other).getTargetClass() == Blob.class;
		return false;
	}
	
	public enum Op {
		GREATER, SMALLER
	}
}
