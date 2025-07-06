package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.lang.constraint.Constraint;
import com.ydo4ki.vird.lang.constraint.FreeConstraint;

/**
 * @since 7/5/2025 10:52 PM
 * @author Sulphuris
 */
public interface Type extends Val {
	
	Constraint getImplications();
	
	Type ROOT_FUNCTION = new IdentityType(FreeConstraint.INSTANCE);
	Type TYPE = new IdentityType(FreeConstraint.INSTANCE);
}
