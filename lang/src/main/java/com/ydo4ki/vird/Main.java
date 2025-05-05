package com.ydo4ki.vird;

import com.github.freva.asciitable.AsciiTable;
import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.base.lexer.ExprOutput;
import com.ydo4ki.vird.base.lexer.TokenOutput;

import java.io.*;
import java.nio.file.Files;

public class Main {
	public static void main(String[] __args) throws IOException {
		printPrjInfo(System.out);
		File src = new File("vird/file.vird");
		
		Scope scope = new Scope(Vird.GLOBAL);
		
		// Using the new Stream API instead of the for-each loop
		new ExprOutput(new TokenOutput(src)).stream()
			.map(expr -> Interpreter.evaluateFinale(scope, expr))
			.forEach(System.out::println);
	}
	
	public static void printPrjInfo(PrintStream out) throws IOException {
		File src = new File("lang/src/main/java");
		
		String[][] data = {
				{"Classes", String.valueOf(countClasses(src))},
				{"Lines of code", String.valueOf(countLines(src))}
		};
		
		System.out.println(AsciiTable.getTable(AsciiTable.BASIC_ASCII, new String[0], new String[0], data));
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
