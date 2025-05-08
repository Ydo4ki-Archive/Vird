package com.ydo4ki.vird;

import com.github.freva.asciitable.AsciiTable;
import com.ydo4ki.vird.base.*;
import com.ydo4ki.vird.lang.*;
import com.ydo4ki.vird.lang.constraint.EqualityConstraint;
import com.ydo4ki.vird.lang.constraint.OrConstraint;
import com.ydo4ki.vird.lib.Functional;
import lombok.NonNull;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
	
	public static void main(String[] __args) throws IOException {
		printPrjInfo(System.out);
		File src = new File("vird/file.vird");
		
		Scope scope = Functional.scope;
		
		Val echo = scope.resolve("echo");
		scope.push("get-echo", new Val() {
			@Override
			public ValidatedValCall invocation(Scope caller, ExprList.Round me) throws LangValidationException {
				if (me.size() != 1) throw new LangValidationException(me.getLocation(), "0 arguments expected");
				return new ValidatedValCall(new EqualityConstraint(echo)) {
					@Override
					public Val invoke0() {
						// !!! side effect !!!
						// ! This is why this vvc is not marked as pure !
						System.out.println("# get-echo is called!");
						
						return echo;
					}
				};
			}
			
			@Override
			public String toString() {
				return "get-echo";
			}
		});
		scope.push("get-echo-sym", new Val() {
			@Override
			public ValidatedValCall invocation(Scope caller, ExprList.Round me) throws LangValidationException {
				if (me.size() != 1) throw new LangValidationException(me.getLocation(), "0 arguments expected");
				Symbol echoSym = new Symbol(me.getLocation(), "echo");
				return new ValidatedValCall(new EqualityConstraint(echoSym)) {
					@Override
					public Val invoke0() {
						System.out.println("# get-echo-sym is called!");
						
						return echoSym;
					}
				};
			}
			
			@Override
			public String toString() {
				return "get-echo-sym";
			}
		});
		Val fakeEcho = new Val() {
			@Override
			public ValidatedValCall invocation(Scope caller, ExprList.Round f) throws LangValidationException {
				if (f.size() != 2) throw new LangValidationException(f.getLocation(), "1 argument expected");
				Expr arg = f.get(1);
				if (arg instanceof Symbol) {
					String v = ((Symbol) arg).getValue();
					int lastChar = v.length() - 1;
					if (v.length() >= 2 && v.charAt(0) == '"' && v.charAt(lastChar) == '"') {
						String content = v.substring(1, lastChar);
						return new ValidatedValCall(new EqualityConstraint(Val.unit)) {
							@Override
							public Val invoke0() {
								System.out.println("troll");
								return Val.unit;
							}
						};
					}
				}
				ValidatedValCall call = Interpreter.evaluateValCall(caller, arg);
				return new ValidatedValCall(new EqualityConstraint(Val.unit)) {
					@Override
					public @NonNull Val invoke0() {
						System.out.println("troll");
						return Val.unit;
					}
				};
			}
			
			@Override
			public String toString() {
				return "fakeEcho";
			}
		};
		scope.push("get-random-echo", new Val() {
			@Override
			public ValidatedValCall invocation(Scope caller, ExprList.Round me) throws LangValidationException {
				if (me.size() != 1) throw new LangValidationException(me.getLocation(), "0 arguments expected");
				return new ValidatedValCall(OrConstraint.of(new EqualityConstraint(echo), new EqualityConstraint(fakeEcho))) {
					@Override
					public Val invoke0() {
						return ThreadLocalRandom.current().nextBoolean() ? echo : fakeEcho;
					}
				};
			}
			
			@Override
			public String toString() {
				return "get-random-echo";
			}
		});
		
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
