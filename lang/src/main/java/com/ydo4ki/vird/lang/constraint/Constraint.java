package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.base.ExprList;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.Env;
import com.ydo4ki.vird.lang.LangValidationException;
import com.ydo4ki.vird.lang.ValidatedValCall;

public interface Constraint {
	boolean implies(Env env, Constraint other);
	
	boolean test(Env env, Val value);
	
	<T extends PrimitiveConstraint> T extractImplication(Class<T> type);
	
	ValidatedValCall getInvocationConstraint(Env env, ExprList f) throws LangValidationException;
	
	default boolean isRuntimeCheckable() {
		return true;
	}
}
