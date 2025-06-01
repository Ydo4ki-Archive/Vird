package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.Blob;
import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.project.Stability;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @since 5/9/2025 9:34 AM
 * @author Sulphuris
 */
@Stability(Stability.LOW)
@Deprecated
@RequiredArgsConstructor(staticName = "of")
@Getter
public class BlobSizeConstraint extends Constraint {
	private final Constraint constraint;
	
	@Override
	public boolean test(Scope scope, Val value) {
		return value instanceof Blob && constraint.test(scope, Blob.ofInt(((Blob) value).getData().length));
	}
	
	@Override
	public boolean implies(Scope scope, Constraint other) {
		if (other instanceof BlobSizeConstraint) return constraint.implies(scope, ((BlobSizeConstraint) other).getConstraint());
		if (other instanceof InstanceOfConstraint) return ((InstanceOfConstraint) other).getTargetClass() == Blob.class;
		return false;
	}
}
