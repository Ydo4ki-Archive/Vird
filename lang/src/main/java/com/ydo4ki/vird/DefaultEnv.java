package com.ydo4ki.vird;

import com.ydo4ki.vird.ast.BracketsTypes;
import com.ydo4ki.vird.ast.Expr;
import com.ydo4ki.vird.ast.ExprList;
import com.ydo4ki.vird.lang.*;
import com.ydo4ki.vird.lang.constraint.FreeConstraint;
import com.ydo4ki.vird.lib.Functional;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 7/5/2025 9:30 PM
 * @author Sulphuris
 */
public final class DefaultEnv implements Env {
	private final Map<String, ValidatedValCall> symbols = new HashMap<>();
	
	public DefaultEnv() {
		push("echo", Functional.echo);
		push("currentEnv", new Val() {
			
			@Override
			public ValidatedValCall invocation(Env env, ExprList f) throws LangValidationException {
				if (!f.getBracketsType().equals(BracketsTypes.round)) return Val.super.invocation(env, f);
				VirdUtil.assertArgsAmount(f, 0);
				return ValidatedValCall.promiseVal(env);
			}
			
			@Override
			public String toString() {
				return "currentEnv";
			}
			
			@Override
			public Type getType() {
				return Type.ROOT_FUNCTION;
			}
		});
	}
	
	@Override
	public ValidatedValCall preresolve(String name, Expr expr) throws LangValidationException {
		ValidatedValCall sym = symbols.get(name);
		if (sym != null) return sym;
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
	public Val resolve(String name) throws RuntimeOperation {
		return symbols.get(name).invoke();
	}
	
	private DefaultEnv push(String name, Val value) {
		symbols.put(name, ValidatedValCall.promiseVal(value));
		return this;
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	private static final Type type = new Type(FreeConstraint.INSTANCE);
}
