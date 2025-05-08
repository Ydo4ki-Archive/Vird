package com.ydo4ki.vird.base;

import com.ydo4ki.vird.Interpreter;
import com.ydo4ki.vird.lang.LangValidationException;
import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.lang.ValidatedValCall;

/**
 * @author Sulphuris
 * @since 4/11/2025 4:36 PM
 */
public abstract class Expr extends Val {
	
	private final Location location;
	
	// sealed
	Expr(Location location) {
		this.location = location;
	}
	
	public final Location getLocation() {
		return location;
	}
	
	@Override
	public ValidatedValCall invocation(Scope caller, ExprList f) throws LangValidationException {
		ValidatedValCall function = Interpreter.evaluateValCall(caller, this);
		
		return function.getInvocationConstraint(new Scope(caller), f);
	}
}

