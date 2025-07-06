package com.ydo4ki.vird;

import com.ydo4ki.vird.ast.*;
import com.ydo4ki.vird.ast.lexer.ExprOutput;
import com.ydo4ki.vird.ast.lexer.TokenOutput;
import com.ydo4ki.vird.lang.*;
import com.ydo4ki.vird.lang.constraint.EqualityConstraint;
import com.ydo4ki.vird.lib.Functional;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sulphuris
 * @since 4/10/2025 4:04 PM
 */
public class FileInterpreter {
	
	private static final boolean measure = true;
	
	public static int run(File src, Env env, BracketsTypes bracketsTypes) throws IOException, LangException {
		ExprOutput expressions = new ExprOutput(new TokenOutput(src, bracketsTypes));
		evaluateExpressions(env, expressions, src, null);
		return 0;
	}
	
	static Val evaluateExpressions(Env env, Iterable<Expr> expressions, File src, String source) throws LangValidationException {
		long start = 0;
		long end;
		long time;
		
		if (measure) {
			System.out.println("\nValidation...");
			start = System.currentTimeMillis();
		}
		List<Expr> elements = new ArrayList<>();
		elements.add(new Symbol(Location.unknown(src, source), "do"));
		for (Expr expression : expressions) {
			elements.add(expression);
		}
		ExprList _do = ExprList.of(Location.unknown(src, source), BracketsTypes.round, elements);
		ValidatedValCall call = Functional._do.invocation(env, _do);
		
		
		if (measure) {
			end = System.currentTimeMillis();
			time = end - start;
			System.out.println("Validated successfully (" + time + "ms)\n");
			
			start = System.currentTimeMillis();
		}
		
		Val ret;
		try {
			ret = call.invoke();
		} catch (RuntimeOperation e) {
			throw new AssertionError(e);
		}
		if (measure) {
			end = System.currentTimeMillis();
			time = end - start;
			System.out.println("\nFinished in " + time + "ms");
		}
		return ret;
	}
	
	// honestly at this point I don't really feel understanding how does this work
	public static ValidatedValCall evaluateValCall(Env env, Expr expr) throws LangValidationException {
		if (expr instanceof ExprList) {
			ExprList f = (ExprList) expr;
			return new WrappedExpr(f.get(0)).invocation(env, f);
		}
		if (expr instanceof Symbol) {
			final String str = ((Symbol) expr).getValue();
			
			ValidatedValCall call = env.preresolve(env, str, expr);
			if (call == null)
				throw new LangValidationException(expr.getLocation(), "Undefined symbol: " + expr);
			
			
			if (call.getConstraint() instanceof EqualityConstraint && call.isPure()) {
				return call; // we already know the exact value
			}
			return new ValidatedValCall(call.getConstraint()) {
				@Override
				public Val invoke0() throws RuntimeOperation {
					return env.resolve(env, str);
				}
			};
		}
		throw new LangValidationException(expr.getLocation(), "Unknown val: " + expr);
	}
	
	
	public static Error handleLangException(LangException e, int code) throws IOException {
		e.handle(System.err);
		System.exit(code);
		return new Error();
	}
}
