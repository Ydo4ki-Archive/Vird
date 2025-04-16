package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.lang.constraint.EqualityConstraint;
import com.ydo4ki.vird.lang.expr.Expr;
import com.ydo4ki.vird.lang.expr.ExprList;

/**
 * @since 4/7/2025 9:43 PM
 * @author Sulphuris
 */
public interface Val {
	Type getRawType();
	
	default TypeRef getType() {
		return getRawType().ref(new EqualityConstraint(this));
	}
	
	default Val call(Scope scope, TypeRef expectedType, ExprList val) throws NoImplementationException {
		throw new NoImplementationException(this);
	}
}
