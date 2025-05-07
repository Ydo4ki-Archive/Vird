package com.ydo4ki.vird;

import com.github.freva.asciitable.AsciiTable;
import com.ydo4ki.vird.base.*;
import com.ydo4ki.vird.lang.*;
import com.ydo4ki.vird.base.lexer.ExprOutput;
import com.ydo4ki.vird.base.lexer.TokenOutput;
import com.ydo4ki.vird.lang.constraint.Constraint;
import com.ydo4ki.vird.lang.constraint.EqualityConstraint;
import com.ydo4ki.vird.lang.constraint.FreeConstraint;
import com.ydo4ki.vird.lang.constraint.InstanceOfConstraint;

import java.io.*;
import java.nio.file.Files;
import java.util.function.Function;

public class Main {
	
	public static void main(String[] __args) throws IOException {
		printPrjInfo(System.out);
		File src = new File("vird/file.vird");
		
		Scope scope = new Scope(Vird.GLOBAL);
		
		
		scope.define("echo", echo);
		scope.define("get-echo", new Val() {
			@Override
			public ValidatedValCall invocation(Location location, Scope caller, Expr[] args) throws LangValidationException {
				if (args.length != 0) throw new LangValidationException(location, "0 arguments expected");
				return new ValidatedValCall(new EqualityConstraint(echo), caller, args) {
					@Override
					public Val invoke() {
						// debug, this is not a side effect, you don't understand this is different
						System.out.println("# get-echo is called!");
						
						return echo;
					}
				};
			}
		});
		
		scope.define("+", plus);
		
		// Using the new Stream API instead of the for-each loop
		new ExprOutput(new TokenOutput(src)).stream()
				.forEach(expr -> Interpreter.evaluate(scope, expr));
	}
	
	static Val echo = new Val() {
		@Override
		public ValidatedValCall invocation(Location location, Scope caller, Expr[] args) throws LangValidationException {
			String error = "1 argument expected";
			if (args.length == 1) {
				if (args[0] instanceof Symbol) {
					String v = ((Symbol) args[0]).getValue();
					if (v.length() >= 2 && v.charAt(0) == '"' && v.charAt(v.length() - 1) == '"')
						return new ValidatedValCall(FreeConstraint.INSTANCE, caller, args) {
							@Override
							public Val invoke() {
								System.out.println(args[0].toString().substring(1, args[0].toString().length() - 1));
								return Val.unit;
							}
						};
					error = "String literal expected";
				} else if (args[0] instanceof ExprList
						&& ((ExprList) args[0]).getBracketsType() == BracketsType.ROUND) {
					return new ValidatedValCall(FreeConstraint.INSTANCE, caller, args) {
						@Override
						public Val invoke() {
							System.out.println(Interpreter.evaluate(scope, args[0]));
							return Val.unit;
						}
					};
				}
			}
			throw new LangValidationException(location, error);
		}
		
	}, plus = new Val() {
		@Override
		public ValidatedValCall invocation(Location location, Scope caller, Expr[] args) throws LangValidationException {
			Function<String, LangValidationException> ex = (msg) -> new LangValidationException(location, msg);
			if (args.length < 2) throw ex.apply("2 or more args expected");
			for (int i = 0; i < args.length; i++) {
				Expr arg = args[i];
				if (arg instanceof Symbol) {
					try {
						Integer.parseInt(((Symbol) arg).getValue());
					} catch (NumberFormatException e) {
						throw new LangValidationException(location, "Number expected (" + i + ")", e);
					}
				} else if (arg instanceof ExprList && ((ExprList) arg).getBracketsType() == BracketsType.ROUND) {
					Constraint c = Interpreter.evaluateNotEvaluateJustConstraintYouGiveMePls(new Scope(caller), arg);
					if (!c.implies(caller, new InstanceOfConstraint(Blob.class)))
						ex.apply("Number expected (" + i + ")");
				}
			}
			return new ValidatedValCall(new InstanceOfConstraint(Blob.class), caller, args) {
				@Override
				public Val invoke() {
					int sum = 0;
					for (Expr arg : args) {
						if (arg instanceof Symbol) {
							sum += Integer.parseInt(((Symbol) arg).getValue());
						} else if (arg instanceof ExprList && ((ExprList) arg).getBracketsType() == BracketsType.ROUND) {
							Blob c = (Blob) Interpreter.evaluate(new Scope(scope), arg);
							sum += c.toInt();
						}
					}
					return Blob.ofInt(sum);
				}
			};
		}
	};
	
	
	public static void printPrjInfo(PrintStream out) throws IOException {
		File src = new File("lang/src/main/java");
		
		String[][] data = {
				{"Classes", String.valueOf(countClasses(src))},
				{"Lines of code", String.valueOf(countLines(src))}
		};
		
		out.println(AsciiTable.getTable(AsciiTable.BASIC_ASCII, new String[0], new String[0], data));
	}
	
	private static int countLines(File file) throws IOException {
		return count(file, f -> Files.readAllLines(f.toPath()).size());
	}
	
	private static int countClasses(File file) throws IOException {
		return count(file, f -> f.getName().endsWith(".java") ? 1 : 0);
	}
	
	private static int count(File file, IOFunction<File, Integer> counter) throws IOException {
		if (file.isFile()) return counter.apply(file);
		File[] files = file.listFiles();
		if (files != null) return accumulate(files, (f) -> count(f, counter));
		return 0;
	}
	
	private static <T> int accumulate(T[] array, IOFunction<T, Integer> accumulator) throws IOException {
		int c = 0;
		for (int i = 0, Len = array.length; i < Len; c += accumulator.apply(array[i++])) ;
		return c;
	}
	
	@FunctionalInterface
	interface IOFunction<T, R> {
		R apply(T t) throws IOException;
	}
}
