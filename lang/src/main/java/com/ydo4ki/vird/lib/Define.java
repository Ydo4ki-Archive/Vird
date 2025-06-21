package com.ydo4ki.vird.lib;

import com.ydo4ki.vird.FileInterpreter;
import com.ydo4ki.vird.VirdUtil;
import com.ydo4ki.vird.base.*;
import com.ydo4ki.vird.lang.*;

/**
 * @since 6/3/2025 12:09 AM
 */
class Define implements Val {
	@Override
	public ValidatedValCall invocation(Env caller, ExprList f) throws LangValidationException {
		if (!f.getBracketsType().equals(Functional.round)) return Val.super.invocation(caller, f);
		Expr[] args = VirdUtil.args(f);
		
		if (args.length != 2)
			throw new LangValidationException(f.getLocation(), "2 arguments expected");
		
		
		// todo: computed names
		if (!(args[0] instanceof Symbol))
			throw new LangValidationException(args[0].getLocation(), "Symbol expected (" + args[0] + ")");
		
		Symbol nameSym = ((Symbol) args[0]);
		String name = nameSym.getValue();
		ValidatedValCall value = FileInterpreter.evaluateValCall(caller, args[1]);
		Env env = caller.getParent();
		
		env.predefine(f.getLocation(), name, value);
		return new ValidatedValCall(value.getConstraint()) {
			@Override
			public Val invoke0() throws RuntimeOperation {
				return env.define(name);
			}
		};
//		return Functional.declaration.newVal(caller, f.getLocation(),nameSym, value);
//		return new ValidatedValCall(new Declaration.DeclarationConstraint(nameSym, value)) {
//			@Override
//			public Val invoke0() {
//				return new Declaration(nameSym, scope.define(name));
//			}
//		};
	}
	
	@Override
	public Type getType() {
		return Type.ROOT_FUNCTION;
	}
}
