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
//	public static Val evaluateFinale(Scope scope, Expr val) {
//		Val ret = evaluate(scope, val);
//		if (ret instanceof Expr) ret = evaluateFinale(scope, (Expr) ret);
//		if (ret instanceof WrappedExpr) return evaluate(scope, ((WrappedExpr) ret).getExpr());
//		return ret;
//	}
	
	// how did it turn out that evaluate is unused lmao
	public static Val evaluate(Scope scope, Expr val) throws LangException {
		Objects.requireNonNull(val, "why null");
		if (val instanceof ExprList) {
			ExprList f = (ExprList)val;
			
			if (f.getBracketsType() == BracketsType.BRACES) {
				throw new UnsupportedOperationException(f.getBracketsType().name());
			}
			if (f.getBracketsType() == BracketsType.SQUARE) {
				throw new UnsupportedOperationException(f.getBracketsType().name());
			}
			
			ValidatedValCall c = evaluateValCall(scope, f);
			
			final Expr[] args;
			{
				List<Expr> args0 = f.getElements();
				args0.remove(0);
				args = args0.toArray(new Expr[0]);
			}
			try {
				return Objects.requireNonNull(
						c.invoke(),
						"Function just returned null. This is outrageous. " +
								"It's unfair. How can you be a function, and not return a value?" +
								val + " " + Arrays.toString(args)
				);
			} catch (Exception e) {
				System.err.println("Unexpected error occurred: " + e);
				if (e instanceof LangException) {
					try {
						throw handleLangException((LangException) e,
								String.join("\n", Files.readAllLines(((LangException) e).getLocation().getSourceFile().toPath())),
								((LangException) e).getLocation().getSourceFile(), 2);
					} catch (IOException ex) {
						System.err.println("# error reading source file");
					}
				}
				e.printStackTrace(System.err);
				System.exit(2);
			}
		}
		if (val instanceof Symbol) return Objects.requireNonNull(
					scope.resolve(((Symbol) val).getValue()),
					"Cannot resolve symbol: " + val
		);
		return val;
	}
	
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
			ValidatedValCall call = scope.preresolve(((Symbol) val).getValue());
			if (call == null) throw new LangValidationException(val.getLocation(), "Undefined symbol: " + val);
			return call;
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
