package com.ydo4ki.vird.base;

import com.ydo4ki.vird.lang.Constraint;
import com.ydo4ki.vird.lang.LangValidationException;
import com.ydo4ki.vird.lang.Scope;

/**
 * @since 4/7/2025 9:43 PM
 * @author Sulphuris
 */
public class Val {
	public Constraint invokationConstraint(Location location, Scope caller, Expr[] args) throws LangValidationException {
		throw new LangValidationException(location, "Not callable");
	}
	
	public Val invoke(Scope caller, Expr[] args) {
		throw new UnsupportedOperationException();
	}
}
