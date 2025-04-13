package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.lang.Val;

/**
 * @author Sulphuris
 * @since 4/12/2025 12:15 PM
 */
public abstract class Constraint {
	
	Constraint() {}
	
	public abstract boolean test(Scope scope, Val value);
	
	// checks if the current constraint implies other (f.e., x > 0 => x >= 0)
	public abstract boolean implies(Scope scope, Constraint other);
	
	// simplified check for cases where the value is unknown
	public boolean isSatisfiable(Scope scope) {
		return true; // feasible by default
	}
}
