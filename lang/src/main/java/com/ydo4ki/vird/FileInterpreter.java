package com.ydo4ki.vird;

import com.ydo4ki.vird.base.*;
import com.ydo4ki.vird.base.lexer.ExprOutput;
import com.ydo4ki.vird.base.lexer.TokenOutput;
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
	
	public static int run(File src, Env env, BracketsTypes bracketsTypes, boolean measure) throws IOException, LangException {
		long start = 0;
		long end;
		long time;
		
		if (measure) {
			System.out.println("\nValidation...");
			start = System.currentTimeMillis();
		}
		
		
		ExprOutput expressions = new ExprOutput(new TokenOutput(src, bracketsTypes));
		List<Expr> elements = new ArrayList<>();
		elements.add(new Symbol(Location.unknown(src), "do"));
		for (Expr expression : expressions) {
			elements.add(expression);
		}
		ExprList _do = ExprList.of(Location.unknown(src), Functional.round, elements);
		ValidatedValCall call = Functional._do.invocation(env, _do);
		
		
		if (measure) {
			end = System.currentTimeMillis();
			time = end - start;
			System.out.println("Validated successfully (" + time + "ms)\n");
			
			start = System.currentTimeMillis();
		}
		
		try {
			call.invoke();
		} catch (RuntimeOperation e) {
			throw new AssertionError(e);
		}
		if (measure) {
			end = System.currentTimeMillis();
			time = end - start;
			System.out.println("\nFinished in " + time + "ms");
		}
		return 0;
	}
	
	// honestly at this point I don't really feel understanding how does this work
	public static ValidatedValCall evaluateValCall(Env env, Expr expr) throws LangValidationException {
		if (expr instanceof ExprList) {
			ExprList f = (ExprList) expr;
			return f.get(0).invocation(env, f);
		}
		if (expr instanceof Symbol) {
			// todo: make it flexible
			final String str = ((Symbol) expr).getValue();
			ValidatedValCall call = env.preresolve(str);
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
					return env.resolve(str);
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
