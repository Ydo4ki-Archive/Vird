package com.ydo4ki.vird.lib;

import com.ydo4ki.vird.FileInterpreter;
import com.ydo4ki.vird.VirdUtil;
import com.ydo4ki.vird.base.Expr;
import com.ydo4ki.vird.base.ExprList;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.Declaration;
import com.ydo4ki.vird.lang.LangValidationException;
import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.lang.ValidatedValCall;
import com.ydo4ki.vird.lang.constraint.Constraint;
import com.ydo4ki.vird.lang.constraint.EqualityConstraint;
import com.ydo4ki.vird.lang.constraint.InstanceOfConstraint;

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
		
		List<ValidatedValCall> declarations = new ArrayList<>();
		
		Constraint retc = new EqualityConstraint(Val.unit);
		List<ValidatedValCall> calls = new ArrayList<>();
		for (Expr expr : expressions) {
			ValidatedValCall call = FileInterpreter.evaluateValCall(caller, expr);
			if (call.getConstraint().implies(caller, InstanceOfConstraint.of(Declaration.class))) {
				declarations.add(call);
			}
			calls.add(call);
			retc = call.getConstraint();
		}
		
		StackFrame frame = new StackFrame(declarations.toArray(new ValidatedValCall[0])); // that same "do" struct
		
		
		return new ValidatedValCall(retc) {
			@Override
			protected Val invoke0() {
				Val ret = Val.unit;
				for (ValidatedValCall call : calls) {
					ret = call.invoke();
				}
				return ret;
			}
		};
		// return Execute.execution(caller, expressions);
	}
}

class StackFrame {
	private final ValidatedValCall[] declarations_layout;
	private final Declaration[] declarations_values;
	
	StackFrame(ValidatedValCall[] declarationsLayout) {
		declarations_layout = declarationsLayout;
		declarations_values = new Declaration[declarationsLayout.length];
	}
}
