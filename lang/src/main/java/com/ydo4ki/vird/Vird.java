package com.ydo4ki.vird;

import com.ydo4ki.vird.ast.BracketsTypes;
import com.ydo4ki.vird.ast.lexer.ExprOutput;
import com.ydo4ki.vird.ast.lexer.TokenOutput;
import com.ydo4ki.vird.lang.LangValidationException;
import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.lang.Val;
import lombok.RequiredArgsConstructor;

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
	
	public static Val run(String vird) throws LangValidationException {
		Thread.UncaughtExceptionHandler h = Thread.currentThread().getUncaughtExceptionHandler();
		if (!(h instanceof LangUncaughtExceptionHandler))
			Thread.currentThread().setUncaughtExceptionHandler(new LangUncaughtExceptionHandler(h));
		
		ExprOutput expressions = new ExprOutput(new TokenOutput(vird, null, bracketsTypes));
		return FileInterpreter.evaluateExpressions(new Scope(null), expressions, null, vird);
	}
}
