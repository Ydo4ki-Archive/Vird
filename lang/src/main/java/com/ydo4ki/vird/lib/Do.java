package com.ydo4ki.vird.lib;

import com.ydo4ki.vird.FileInterpreter;
import com.ydo4ki.vird.VirdUtil;
import com.ydo4ki.vird.base.Expr;
import com.ydo4ki.vird.base.ExprList;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.LangValidationException;
import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.lang.ValidatedValCall;
import com.ydo4ki.vird.lang.constraint.Constraint;
import com.ydo4ki.vird.lang.constraint.EqualityConstraint;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 6/2/2025 7:55 PM
 * @author alignie
 */
class Do extends Val {
	@Override
	public ValidatedValCall invocation(Scope caller, ExprList f) throws LangValidationException {
		if (f.getBracketsType().open != '(') return super.invocation(caller, f);
		Expr[] expressions = VirdUtil.args(f);
		
		return Execute.execution(caller, expressions);
	}
}
