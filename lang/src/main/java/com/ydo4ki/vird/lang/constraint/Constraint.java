package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.base.Expr;
import com.ydo4ki.vird.base.Location;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.LangValidationException;
import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.lang.ValidatedValCall;

/**
 * @since 4/18/2025 12:21 AM
 * @author Sulphuris
 */
public interface Constraint {
	boolean test(Scope scope, Val value);
	
	// checks if the current constraint implies other (f.e., x > 0 => x >= 0)
	boolean implies(Scope scope, Constraint other);
	
	// by the way
	// if constraint A implies constraint B
	// and constraint B doesn't imply constraint A
	// then constrain B is stricter
	// and if both of them imply each other or none of them imply another one they are equal/uncomparable
	
	
	// simplified check for cases where the value is unknown
	@Deprecated
	default boolean isSatisfiable(Scope scope) {
		return true; // feasible by default
	}
	
	/** if this is a function (guaranteed), it returns constraint for result of function based on arguments
	 (or throws LangValidationException if function is not a function or following arguments are inappropriate) */
	ValidatedValCall getInvocationConstraint(Location location, Scope scope, Expr[] args) throws LangValidationException;
	
	static boolean areEqual(Scope scope, Constraint a, Constraint b) {
		return a.implies(scope, b) && b.implies(scope, a);
	}
}
