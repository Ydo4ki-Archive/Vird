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
			
			Func func;
			try {
				func = validate(scope, f);
			} catch (LangException e) {
				try {
					throw handleLangException(e,
							String.join("\n", Files.readAllLines(e.getLocation().getSourceFile().toPath())),
							e.getLocation().getSourceFile());
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
			
			final Val[] args;
			{
				List<Expr> args0 = f.getElements();
				args0.remove(0);
				args = args0.toArray(new Val[0]);
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
	
	
	
	public static Func validate(Scope scope, ExprList f) throws LangException {
		if (f.getBracketsType() == BracketsType.BRACES) {
			throw new UnsupportedOperationException(f.getBracketsType().name());
		}
		if (f.getBracketsType() == BracketsType.SQUARE) {
			throw new UnsupportedOperationException(f.getBracketsType().name());
		}
		
		Expr functionId = f.get(0);
		// todo: fix this nonsense
		Val function = evaluate(scope, functionId);
		if (!(function instanceof Func)) {
			throw new IllegalArgumentException("Function not found: " + functionId);
		}
		Func func = ((Func) function);
		
		final Val[] args;
		{
			List<Expr> args0 = f.getElements();
			args0.remove(0);
			args = args0.toArray(new Val[0]);
		}
		func.validate(f, new Scope(scope), args);
		return func;
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
