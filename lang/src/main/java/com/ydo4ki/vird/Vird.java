package com.ydo4ki.vird;

import com.ydo4ki.vird.ast.BracketsTypes;
import com.ydo4ki.vird.ast.lexer.ExprOutput;
import com.ydo4ki.vird.ast.lexer.TokenOutput;
import com.ydo4ki.vird.lang.Env;
import com.ydo4ki.vird.lang.LangValidationException;
import com.ydo4ki.vird.lang.Val;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;

/**
 * @since 7/5/2025 8:06 PM
 * @author Sulphuris
 */
public final class Vird {
	
	private static final BracketsTypes bracketsTypes = new BracketsTypes(
			BracketsTypes.round, BracketsTypes.square, BracketsTypes.braces
	);
	
	@RequiredArgsConstructor
	private static final class LangUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
		private final Thread.UncaughtExceptionHandler h;
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			if (e instanceof LangValidationException) {
				try {
					((LangValidationException) e).handle(System.err);
				} catch (Exception ex) {
					h.uncaughtException(t, ex);
					h.uncaughtException(t, e);
				}
			} else {
				h.uncaughtException(t, e);
			}
		}
	}
	
	public static void establishValidationErrorsHandler() {
		establishValidationErrorsHandler(Thread.currentThread());
	}
	public static void establishValidationErrorsHandler(Thread thread) {
		Thread.UncaughtExceptionHandler h = thread.getUncaughtExceptionHandler();
		if (!(h instanceof LangUncaughtExceptionHandler))
			thread.setUncaughtExceptionHandler(new LangUncaughtExceptionHandler(h));
	}
	
	public static Val run(VirdSrc vird) throws LangValidationException {
		return run(vird, new DefaultEnv());
	}
	public static Val run(VirdSrc vird, Env env) throws LangValidationException {
		if (vird.sourceFile == null) {
			ExprOutput expressions = new ExprOutput(new TokenOutput(vird.source, null, bracketsTypes));
			return FileInterpreter.evaluateExpressions(env, expressions, null, vird.source);
		} else {
			File src = vird.sourceFile;
			ExprOutput expressions;
			try {
				expressions = new ExprOutput(new TokenOutput(src, bracketsTypes));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return FileInterpreter.evaluateExpressions(env, expressions, src, vird.source);
		}
	}
}
