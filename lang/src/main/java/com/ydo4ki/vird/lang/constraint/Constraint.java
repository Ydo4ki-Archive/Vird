package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.ast.ExprList;
import com.ydo4ki.vird.ast.Location;
import com.ydo4ki.vird.lang.*;

public interface Constraint {
	boolean implies(Env env, Constraint other);
	
	boolean test(Env env, Val value);
	
	<T extends PrimitiveConstraint> T extractImplication(Class<T> type);
	
	ValidatedValCall getInvocationConstraint(Env env, ExprList f) throws LangValidationException;
	
	ValidatedValCall getPropertyGetterConstraint(Env env, String property, Location l) throws LangValidationException;
	
	default boolean isRuntimeCheckable() {
		return true;
	}
}
