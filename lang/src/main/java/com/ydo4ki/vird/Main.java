package com.ydo4ki.vird;

import com.github.freva.asciitable.AsciiTable;
import com.ydo4ki.vird.base.Location;
import com.ydo4ki.vird.base.Symbol;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.*;
import com.ydo4ki.vird.base.lexer.ExprOutput;
import com.ydo4ki.vird.base.lexer.TokenOutput;

import java.io.*;
import java.nio.file.Files;

public class Main {
	public static void main(String[] __args) throws IOException {
		printPrjInfo(System.out);
		File src = new File("vird/file.vird");
		
		Scope scope = new Scope(Vird.GLOBAL);
		
		Val echo = new Func(
				(env, args) -> {
					Constraint c = FreeConstraint.INSTANCE;
					if (args.length != 1) return null;
					if (args[0] instanceof Symbol) {
						String v = ((Symbol) args[0]).getValue();
						if (v.length() < 2) return null;
						return v.charAt(0) == '"' && v.charAt(v.length()-1) == '"' ? c : null;
					}
					return null;
				},
				(env, args) -> {
					System.out.println(args[0].toString().substring(1, args[0].toString().length()-1));
					return new Val();
				}
		);
		scope.define("echo", echo);
		scope.define("get-echo", new Func(
				(env, args) -> args.length == 0 ? new EqualityConstraint(echo) : null,
				(env, args) -> {
					System.out.println("# get-echo is called!");
					return echo;
				}
		));
		
		// Using the new Stream API instead of the for-each loop
		new ExprOutput(new TokenOutput(src)).stream()
			.forEach(expr -> Interpreter.evaluate(scope, expr));
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
		for (int i = 0, Len = array.length; i < Len; c += accumulator.apply(array[i++]));
		return c;
	}
	@FunctionalInterface
	interface IOFunction<T, R> {
		R apply(T t) throws IOException;
	}
}
