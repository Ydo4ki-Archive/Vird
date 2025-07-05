package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.lang.Env;
import com.ydo4ki.vird.lang.Val;
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
@RequiredArgsConstructor//(staticName = "of")
@Getter
abstract class BlobSizeConstraint extends AbstractConstraint {
	private final Constraint constraint;
	
	@Override
	public boolean test(Env env, Val value) {
		return value instanceof Blob && constraint.test(env, Blob.ofInt(((Blob) value).getData().length));
	}
	
	@Override
	public boolean implies(Env env, Constraint other) {
		if (other instanceof BlobSizeConstraint) return constraint.implies(env, ((BlobSizeConstraint) other).getConstraint());
		if (other instanceof InstanceOfConstraint) return ((InstanceOfConstraint) other).getTargetClass() == Blob.class;
		return false;
	}
}
