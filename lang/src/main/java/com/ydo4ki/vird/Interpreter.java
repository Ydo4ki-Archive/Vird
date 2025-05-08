package com.ydo4ki.vird;

import com.ydo4ki.vird.base.*;
import com.ydo4ki.vird.lang.*;
import com.ydo4ki.vird.lang.constraint.Constraint;
import com.ydo4ki.vird.lang.constraint.EqualityConstraint;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Sulphuris
 * @since 4/10/2025 4:04 PM
 */
@Getter
public class Interpreter {
	private final Scope program = new Scope(Vird.GLOBAL);
	
	public Interpreter() {}
	
	// honestly at this point I don't really feel understanding how does this work
	public static ValidatedValCall evaluateValCall(Scope scope, Expr val) throws LangValidationException {
		if (val instanceof ExprList) {
			ExprList f = (ExprList) val;
			if (f.getBracketsType() == BracketsType.BRACES) {
				throw new UnsupportedOperationException(f.getBracketsType().name());
			}
			if (f.getBracketsType() == BracketsType.SQUARE) {
				throw new UnsupportedOperationException(f.getBracketsType().name());
			}
			
			Expr functionId = f.get(0);
			Constraint function = evaluateValCall(scope, functionId).getConstraint();
			
			final Expr[] args;
			{
				List<Expr> args0 = f.getElements();
				args0.remove(0);
				args = args0.toArray(new Expr[0]);
			}
			return function.getInvocationConstraint(f.getLocation(), new Scope(scope), args);
		}
		if (val instanceof Symbol) {
			// todo: make it flexible
			String str = ((Symbol) val).getValue();
			ValidatedValCall call = scope.preresolve(str);
			if (call == null) {
				try {
					int v = Integer.parseInt(str);
					Blob b = Blob.ofInt(v);
					return ValidatedValCall.promiseVal(b);
				} catch (NumberFormatException e) {
					throw new LangValidationException(val.getLocation(), "Undefined symbol: " + val);
				}
			}
			if (call.getConstraint() instanceof EqualityConstraint) {
				return call; // we already know the exact value
			}
			return new ValidatedValCall(call.getConstraint()) {
				@Override
				public Val invoke() {
					return scope.resolve(str);
				}
			};
		}
		throw new LangValidationException(val.getLocation(), "Unknown val: " + val);
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
		StringBuilder msg = new StringBuilder(e.getClass().getSimpleName()).append(": ").append(e.getRawMessage()).append(" (")
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
