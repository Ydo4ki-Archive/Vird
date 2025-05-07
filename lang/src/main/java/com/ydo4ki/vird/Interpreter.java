package com.ydo4ki.vird;

import com.ydo4ki.vird.base.*;
import com.ydo4ki.vird.lang.*;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
	
	public static Val evaluate(Scope scope, Expr val) {
		Objects.requireNonNull(val, "why null");
		if (val instanceof ExprList) {
			ExprList f = (ExprList)val;
			
			if (f.getBracketsType() == BracketsType.BRACES) {
				throw new UnsupportedOperationException(f.getBracketsType().name());
			}
			if (f.getBracketsType() == BracketsType.SQUARE) {
				throw new UnsupportedOperationException(f.getBracketsType().name());
			}
			
			try {
				// validate, constraint result is probably not needed at this state
				Constraint c = evaluateNotEvaluateJustConstraintYouGiveMePls(scope, f);
			} catch (LangException e) {
				try {
					throw handleLangException(e,
							String.join("\n", Files.readAllLines(e.getLocation().getSourceFile().toPath())),
							e.getLocation().getSourceFile());
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
			Expr functionId = f.get(0);
			
			Val func = evaluate(scope, functionId);
			
			final Expr[] args;
			{
				List<Expr> args0 = f.getElements();
				args0.remove(0);
				args = args0.toArray(new Expr[0]);
			}
			return Objects.requireNonNull(
					func.invoke(new Scope(scope), args),
					"Cannot evaluate function: " + val
			);
		}
		if (val instanceof Symbol) return Objects.requireNonNull(
					scope.resolve(((Symbol) val).getValue()),
					"Cannot resolve symbol: " + val
		);
		return val;
	}
	
	private static Constraint evaluateNotEvaluateJustConstraintYouGiveMePls(Scope scope, Expr val) throws LangValidationException {
		Objects.requireNonNull(val, "why null");
		if (val instanceof ExprList) {
			ExprList f = (ExprList)val;
			
			if (f.getBracketsType() == BracketsType.BRACES) {
				throw new UnsupportedOperationException(f.getBracketsType().name());
			}
			if (f.getBracketsType() == BracketsType.SQUARE) {
				throw new UnsupportedOperationException(f.getBracketsType().name());
			}
			
			Constraint function;
			try {
				Expr functionId = f.get(0);
				function = evaluateNotEvaluateJustConstraintYouGiveMePls(scope, functionId);
			} catch (LangException e) {
				try {
					throw handleLangException(e,
							String.join("\n", Files.readAllLines(e.getLocation().getSourceFile().toPath())),
							e.getLocation().getSourceFile());
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
			
			final Expr[] args;
			{
				List<Expr> args0 = f.getElements();
				args0.remove(0);
				args = args0.toArray(new Expr[0]);
			}
			return function.getInvokationConstraint(f.getLocation(), new Scope(scope), args);
		}
		if (val instanceof Symbol) return new EqualityConstraint(scope.resolve(((Symbol) val).getValue()));
		return new EqualityConstraint(val);
	}
	
	
	
	
	private static Error handleLangException(LangException e, String source, File file) {
		String filename = file.getAbsolutePath();
		filename = filename.substring(1).replaceAll("\\|/", ".");
		System.err.println(getErrorDescription(e, filename, source));
		if (e.getCause() != e && e.getCause() instanceof LangException) {
			System.err.println("for:");
			System.err.println(getErrorDescription((LangException) e.getCause(), filename, source));
		}
//		e.printStackTrace();
		System.exit(30);
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
