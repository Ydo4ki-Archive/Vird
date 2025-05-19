package com.ydo4ki.vird;

import com.ydo4ki.vird.base.*;
import com.ydo4ki.vird.base.lexer.ExprOutput;
import com.ydo4ki.vird.base.lexer.TokenOutput;
import com.ydo4ki.vird.lang.*;
import com.ydo4ki.vird.lang.constraint.EqualityConstraint;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sulphuris
 * @since 4/10/2025 4:04 PM
 */
public class Interpreter {
	
	public static int run(File src, Scope scope, boolean measure) throws IOException, LangException {
		long start = 0;
		long end;
		long time;
		
		if (measure) {
			System.out.println("\nValidation...");
			start = System.currentTimeMillis();
		}
		
		
		List<ValidatedValCall> calls = new ArrayList<>();
		for (Expr expr : new ExprOutput(new TokenOutput(src))) {
			calls.add(Interpreter.evaluateValCall(scope, expr));
		}
		
		
		if (measure) {
			end = System.currentTimeMillis();
			time = end - start;
			System.out.println("Validated successfully (" + time + "ms)\n");
			
			start = System.currentTimeMillis();
		}
		
		for (ValidatedValCall call : calls) {
			call.invoke();
		}
		if (measure) {
			end = System.currentTimeMillis();
			time = end - start;
			System.out.println("\nFinished in " + time + "ms");
		}
		return 0;
	}
	
	public static Expr[] args(ExprList f) {
		final Expr[] args;
		{
			List<Expr> args0 = f.getElements();
			args0.remove(0);
			args = args0.toArray(new Expr[0]);
		}
		return args;
	}
	
	// honestly at this point I don't really feel understanding how does this work
	public static ValidatedValCall evaluateValCall(Scope scope, Expr expr) throws LangValidationException {
		if (expr instanceof ExprList.Round) {
			ExprList.Round f = (ExprList.Round) expr;
			return f.get(0).invocation(scope, f);
		}
		if (expr instanceof Symbol) {
			// todo: make it flexible
			final String str = ((Symbol) expr).getValue();
			ValidatedValCall call = scope.preresolve(str);
			if (call == null) try {
				Blob b;
				if (str.startsWith("0x")) {
					String num = str.substring(2);
					if (num.length() % 2 == 1)
						throw new LangValidationException(expr.getLocation(), "Amount of digits must be even: " + expr);
					BigInteger integer = new BigInteger(num, 16);
					int bytes = num.length() / 2;
					b = new Blob(integer, bytes);
				} else {
					b = new Blob(new BigInteger(str));
				}
				return ValidatedValCall.promiseVal(b);
			} catch (NumberFormatException e) {
				throw new LangValidationException(expr.getLocation(), "Undefined symbol: " + expr);
			}
			
			if (call.getConstraint() instanceof EqualityConstraint && call.isPure()) {
				return call; // we already know the exact value
			}
			return new ValidatedValCall(call.getConstraint()) {
				@Override
				public Val invoke0() {
					return scope.resolve(str);
				}
			};
		}
		throw new LangValidationException(expr.getLocation(), "Unknown val: " + expr);
	}
	
	
	public static Error handleLangException(LangException e, String source, File file, int code) {
		String filename = file.getAbsolutePath();
		filename = filename.substring(1).replaceAll("\\|/", ".");
		System.err.println(getErrorDescription(e, filename, source));
		if (e.getCause() != e && e.getCause() instanceof LangException) {
			System.err.println("for:");
			System.err.println(getErrorDescription((LangException) e.getCause(), filename, source));
		}
//		e.printStackTrace();
		System.exit(code);
		return new Error();
	}
	
	private static String getErrorDescription(LangException e, String filename, String source) {
		StringBuilder msg = new StringBuilder(e.errName()).append(": ").append(e.getRawMessage()).append(" (")
				.append(filename);
		if (e.getLocation() != null) {
			msg.append(':').append(e.getLocation().getStartLine());
		}
		msg.append(')').append("\n\n");
		
		if (e.getLocation() != null) {
			String line;
			try {
				line = source.split("\n")[e.getLocation().getStartLine() - 1];
			} catch (ArrayIndexOutOfBoundsException ee) {
				line = " ";
			}
			
			int linePos = source.substring(0, e.getLocation().getStartPos()).lastIndexOf('\n');
			int errStart = e.getLocation().getStartPos() - linePos;
			int errEnd = e.getLocation().getEndPos() - linePos;
			
			msg.append(line).append('\n');
			
			char[] underline = new char[line.length()];
			for (int i = 0; i < underline.length; i++) {
				if (i >= errStart - 1 && i < errEnd - 1) underline[i] = '~';
				else if (line.charAt(i) == '\t') underline[i] = '\t';
				else underline[i] = ' ';
			}
			return msg.append(underline).append('\n').toString();
		}
		
		return msg.append('\n').toString();
	}
}
