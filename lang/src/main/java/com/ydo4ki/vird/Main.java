package com.ydo4ki.vird;

import com.github.freva.asciitable.AsciiTable;
import com.ydo4ki.vird.base.*;
import com.ydo4ki.vird.lang.*;
import com.ydo4ki.vird.lang.constraint.EqualityConstraint;
import com.ydo4ki.vird.lang.constraint.InstanceOfConstraint;
import lombok.NonNull;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Main {
	
	public static void main(String[] __args) throws IOException {
		printPrjInfo(System.out);
		File src = new File("vird/file.vird");
		
		Scope scope = new Scope(null);
		
		scope.push("echo", echo);
		scope.push("get-echo", new Val() {
			@Override
			public ValidatedValCall invocation(Location location, Scope caller, Expr[] args) throws LangValidationException {
				if (args.length != 0) throw new LangValidationException(location, "0 arguments expected");
				return new ValidatedValCall(new EqualityConstraint(echo)) {
					@Override
					public Val invoke() {
						// debug, this is not a side effect, you don't understand this is different
						System.out.println("# get-echo is called!");
						
						return echo;
					}
				};
			}
		});
		
		scope.push("+", plus);
		scope.push(":", define);
		scope.push("UNIT", Val.unit);
		try {
			System.exit(Interpreter.run(src, scope, true));
		} catch (LangException e) {
			try {
				throw Interpreter.handleLangException(e,
						String.join("\n", Files.readAllLines(e.getLocation().getSourceFile().toPath())),
						e.getLocation().getSourceFile(), 1);
			} catch (IOException ex) {
				e.printStackTrace(System.err);
				System.exit(1);
			}
		}
	}
	
	static Val echo = new Val() {
		@Override
		public ValidatedValCall invocation(Location location, Scope caller, Expr[] args) throws LangValidationException {
			String error = "1 argument expected";
			if (args.length == 1) {
				Expr arg = args[0];
				if (arg instanceof Symbol) {
					String v = ((Symbol) arg).getValue();
					int lastChar = v.length() - 1;
					if (v.length() >= 2 && v.charAt(0) == '"' && v.charAt(lastChar) == '"') {
						String content = v.substring(1, lastChar);
						return new ValidatedValCall(new EqualityConstraint(Val.unit)) {
							@Override
							public Val invoke() {
								System.out.println(content);
								return Val.unit;
							}
						};
					} else {
						ValidatedValCall call = Interpreter.evaluateValCall(caller, arg);
						return new ValidatedValCall(new EqualityConstraint(Val.unit)) {
							@Override
							public @NonNull Val invoke() {
								System.out.println(call.invoke());
								return Val.unit;
							}
						};
					}
				} else if (arg instanceof ExprList
						&& ((ExprList) arg).getBracketsType() == BracketsType.ROUND) {
					ValidatedValCall call = Interpreter.evaluateValCall(caller, arg);
					return new ValidatedValCall(new EqualityConstraint(Val.unit)) {
						@Override
						public Val invoke() {
							System.out.println(call.invoke());
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
			int sumOfKnownValues = 0;
			List<ValidatedValCall> leftToEvaluate = new ArrayList<>();
			
			for (int i = 0; i < args.length; i++) {
				Expr arg = args[i];
				ValidatedValCall c = Interpreter.evaluateValCall(new Scope(caller), arg);
				if (c.getConstraint() instanceof EqualityConstraint /* ?? */ && c.isPure()) {
					sumOfKnownValues += ((Blob)c.invoke()).toInt();
				} else {
					if (!c.getConstraint().implies(caller, new InstanceOfConstraint(Blob.class)))
						ex.apply("Number expected (" + i + ")");
					leftToEvaluate.add(c);
				}
			}
			final int sokv = sumOfKnownValues;
			return new ValidatedValCall(new InstanceOfConstraint(Blob.class)) {
				@Override
				public Val invoke() {
					int sum = sokv;
					for (ValidatedValCall arg : leftToEvaluate) {
						Blob c = (Blob) arg.invoke();
						sum += c.toInt();
					}
					return Blob.ofInt(sum);
				}
			};
		}
	}, define = new Val() {
		@Override
		public ValidatedValCall invocation(Location location, Scope caller, Expr[] args) throws LangValidationException {
			if (args.length != 2) throw new LangValidationException(location, "2 arguments expected");
			// todo: computed names
			if (!(args[0] instanceof Symbol)) throw new LangValidationException(args[0].getLocation(), "Symbol expected (" + args[0] + ")");
			String name = ((Symbol) args[0]).getValue();
			ValidatedValCall value = Interpreter.evaluateValCall(caller, args[1]);
			Scope scope = caller.getParent();
			scope.predefine(name, value);
			return new ValidatedValCall(value.getConstraint()) {
				@Override
				public Val invoke() {
					return scope.define(name);
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
