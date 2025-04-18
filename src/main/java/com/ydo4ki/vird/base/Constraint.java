package com.ydo4ki.vird.base;

import com.ydo4ki.vird.lang.Scope;

/**
 * @since 4/18/2025 12:21 AM
 * @author Sulphuris
 */
public interface Constraint {
	boolean test(Scope scope, Val value);
	
	// checks if the current constraint implies other (f.e., x > 0 => x >= 0)
	boolean implies(Scope scope, Constraint other);
	
	// simplified check for cases where the value is unknown
	default boolean isSatisfiable(Scope scope) {
		return true; // feasible by default
	}
}
