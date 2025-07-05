package com.ydo4ki.vird.lib;

import com.ydo4ki.vird.FileInterpreter;
import com.ydo4ki.vird.VirdUtil;
import com.ydo4ki.vird.ast.Expr;
import com.ydo4ki.vird.ast.ExprList;
import com.ydo4ki.vird.lang.*;
import com.ydo4ki.vird.lang.constraint.Constraint;
import com.ydo4ki.vird.lang.constraint.EqualityConstraint;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 6/2/2025 7:55 PM
 * 
 */
class Do implements Val {
	@Override
	public Type getType() {
		return Type.ROOT_FUNCTION;
	}

	@Override
	public ValidatedValCall invocation(Env caller, ExprList f) throws LangValidationException {
		if (f.getBracketsType().open != '(') return Val.super.invocation(caller, f);
		Expr[] expressions = VirdUtil.args(f);

//		List<ValidatedValCall> declarations = new ArrayList<>();
////		Map<String, Integer> fieldOffsets = new HashMap<>();
		
		Constraint retc = new EqualityConstraint(Val.unit);
		List<ValidatedValCall> calls = new ArrayList<>();
		for (Expr expr : expressions) {
			ValidatedValCall call = FileInterpreter.evaluateValCall(caller, expr);
//			if (call.getConstraint().implies(caller, Functional.declaration)) {
//				Struct str = call.getConstraint().extractImplication(Struct.class);
//				Constraint symConstr = str.getFields()[0];
//				Constraint valConstr = str.getFields()[1];
//				EqualityConstraint symEq = symConstr.extractImplication(EqualityConstraint.class);
//				if (symEq != null) {
//					Symbol sym = (Symbol) symEq.getExpected();
//					fieldOffsets.put(sym.getValue(), declarations.size());
//				} else {
//					throw new LangValidationException(expr.getLocation(), "Dynamic declaration names are not supported yet");
//				}
//				declarations.add(call);
//			}
			calls.add(call);
			retc = call.getConstraint();
		}
//
//		Struct stackFrame = new Struct(declarations.stream().map(ValidatedValCall::getConstraint).toArray(Constraint[]::new));


		return new ValidatedValCall(retc) {
			@Override
			protected Val invoke0() throws RuntimeOperation {
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