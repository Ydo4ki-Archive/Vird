package com.ydo4ki.vird.base;

import com.ydo4ki.vird.lang.ValidatedValCall;
import com.ydo4ki.vird.lang.LangValidationException;
import com.ydo4ki.vird.lang.Scope;

/**
 * @since 4/7/2025 9:43 PM
 * @author Sulphuris
 */
public class Val {
	public ValidatedValCall invocation(Scope caller, ExprList me) throws LangValidationException {
		throw new LangValidationException(me.getLocation(), "Not callable (" + this.getClass().getSimpleName() + " «" + this + "»)");
	}
	
	public static final Val unit = new Val();
}
