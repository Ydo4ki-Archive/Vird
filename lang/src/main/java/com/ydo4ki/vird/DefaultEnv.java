package com.ydo4ki.vird;

import com.ydo4ki.vird.ast.BracketsTypes;
import com.ydo4ki.vird.ast.Expr;
import com.ydo4ki.vird.ast.ExprList;
import com.ydo4ki.vird.ast.Symbol;
import com.ydo4ki.vird.lang.*;
import com.ydo4ki.vird.lang.constraint.FreeConstraint;
import com.ydo4ki.vird.lang.constraint.HasTypeConstraint;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @since 7/5/2025 9:30 PM
 * @author Sulphuris
 */
public final class DefaultEnv implements Env {
	private final Map<String, Function<Env, Val>> symbols = new HashMap<>();
	
	public DefaultEnv() {
		symbols.put("currentEnv", (env) -> env);
		push("typeOf", new Val() {
			@Override
			public ValidatedValCall invocation(Env env, ExprList f) throws LangValidationException {
				if (!f.getBracketsType().equals(BracketsTypes.round)) return Val.super.invocation(env, f);
				VirdUtil.assertArgsAmount(f, 1);
				ValidatedValCall call = FileInterpreter.evaluateValCall(env, f.get(1));
				return new ValidatedValCall(HasTypeConstraint.of(Type.TYPE)) {
					@Override
					protected Val invoke0() throws RuntimeOperation {
						return call.invoke().getType();
					}
				};
			}

			@Override
			public String toString() {
				return "typeOf";
			}

			@Override
			public Type getType() {
				return Type.ROOT_FUNCTION;
			}
		});
		push(".", new Val() {
			@Override
			public ValidatedValCall invocation(Env env, ExprList f) throws LangValidationException {
				if (!f.getBracketsType().equals(BracketsTypes.round)) return Val.super.invocation(env, f);
				VirdUtil.assertArgsAmount(f, 2);
				ValidatedValCall propertyContainer = FileInterpreter.evaluateValCall(env, f.get(1));
				
				Symbol symbol;
				Expr property = f.get(2);
				if (property instanceof Symbol) {
					symbol = (Symbol) property;
				} else {
					WrappedExpr exp = new WrappedExpr(property);
					ValidatedValCall symbolGetter = exp.invocation(env, (ExprList) property);
					throw new LangValidationException(property.getLocation(), "Nevermind, there gotta be a symbol");
				}
				String propertyName = symbol.getValue();
				
				return propertyContainer.getPropertyGetterConstraint(env, propertyName, property.getLocation());
			}
			
			@Override
			public String toString() {
				return "<get-property>";
			}
			
			@Override
			public Type getType() {
				return Type.ROOT_FUNCTION;
			}
		});
	}
	
	@Override
	public ValidatedValCall preresolve(Env callerEnv, String name, Expr expr) throws LangValidationException {
		Val sym = symbols.get(name).apply(callerEnv);
		if (sym != null) return ValidatedValCall.promiseVal(sym);
		try {
			Blob b;
			if (name.startsWith("0x")) {
				String num = name.substring(2);
				if (num.length() % 2 == 1)
					throw new LangValidationException(expr.getLocation(), "Amount of digits must be even: " + expr);
				BigInteger integer = new BigInteger(num, 16);
				int bytes = num.length() / 2;
				b = new Blob(integer, bytes);
			} else {
				b = new Blob(new BigInteger(name));
			}
			return ValidatedValCall.promiseVal(b);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	@Override
	public Val resolve(Env callerEnv, String name) throws RuntimeOperation {
		return symbols.get(name).apply(callerEnv);
	}
	
	private DefaultEnv push(String name, Val value) {
		symbols.put(name, (env) -> value);
		return this;
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	private static final Type type = new IdentityType(FreeConstraint.INSTANCE);
}
