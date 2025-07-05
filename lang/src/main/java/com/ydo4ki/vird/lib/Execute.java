package com.ydo4ki.vird.lib;

/**
 * The most basic primitive of interpretation that can be expressed in programming languages at all (с)
 * @since 6/2/2025 11:49 PM
 * @author alignie
 * */
//class Execute implements Val {
//	public static ValidatedValCall execution(Env caller, Expr[] expressions, ExecutionHandler... handlers) throws LangValidationException {
//		Constraint retc = new EqualityConstraint(Val.unit);
//		List<ValidatedValCall> calls = new ArrayList<>();
//		for (Expr expr : expressions) {
//			ValidatedValCall call = FileInterpreter.evaluateValCall(caller, expr);
//			for (ExecutionHandler handler : handlers) {
//				handler.handleCall(caller, call);
//			}
//
//			calls.add(call);
//			retc = call.getConstraint();
//		}
//
//		return new ValidatedValCall(retc) {
//			@Override
//			protected Val invoke0() throws RuntimeOperation {
//				Val ret = Val.unit;
//				for (ValidatedValCall call : calls) {
//					ret = call.invoke();
//					for (ExecutionHandler handler : handlers) {
//						handler.handleVal(caller, ret);
//					}
//				}
//				return ret;
//			}
//		};
//	}
//}
//
