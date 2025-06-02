package com.ydo4ki.vird.lib;

import com.ydo4ki.vird.FileInterpreter;
import com.ydo4ki.vird.base.Expr;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.LangValidationException;
import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.lang.ValidatedValCall;
import com.ydo4ki.vird.lang.constraint.Constraint;
import com.ydo4ki.vird.lang.constraint.EqualityConstraint;

import java.util.ArrayList;
import java.util.List;

/**
 * The most basic primitive of interpretation that can be expressed in programming languages at all.
 * @since 6/2/2025 11:49 PM
 * @author alignie
 * */
class Execute extends Val {
	public static ValidatedValCall execution(Scope caller, Expr[] expressions, ExecutionHandler... handlers) throws LangValidationException {
		Constraint retc = new EqualityConstraint(Val.unit);
		List<ValidatedValCall> calls = new ArrayList<>();
		for (Expr expr : expressions) {
			ValidatedValCall call = FileInterpreter.evaluateValCall(caller, expr);
			for (ExecutionHandler handler : handlers) {
				handler.handleCall(caller, call);
			}
			
			calls.add(call);
			retc = call.getConstraint();
		}
		
		return new ValidatedValCall(retc) {
			@Override
			protected Val invoke0() {
				Val ret = Val.unit;
				for (ValidatedValCall call : calls) {
					ret = call.invoke();
					for (ExecutionHandler handler : handlers) {
						handler.handleVal(caller, ret);
					}
				}
				return ret;
			}
		};
	}
}

