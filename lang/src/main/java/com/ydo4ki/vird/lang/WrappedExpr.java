package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.FileInterpreter;
import com.ydo4ki.vird.ast.Expr;
import com.ydo4ki.vird.ast.ExprList;
import com.ydo4ki.vird.lang.constraint.FreeConstraint;
import lombok.Getter;

/**
 * @since 4/17/2025 1:32 PM
 * @author Sulphuris
 */
@Getter
public final class WrappedExpr implements Val {
	
	private final Expr expr;
	
	public WrappedExpr(Expr expr) {
		this.expr = expr;
	}
	
	@Override
	public ValidatedValCall invocation(Env env, ExprList f) throws LangValidationException {
		ValidatedValCall function = FileInterpreter.evaluateValCall(env, expr);
		
		return function.getInvocationConstraint(env, f);
	}
	
	@Override
	public String toString() {
		return "<" + expr + ">";
	}
	
	@Override
	public Type getType() {
		return TYPE;
	}
	
	public static final Type TYPE = new Type(FreeConstraint.INSTANCE);
}
