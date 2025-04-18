package com.ydo4ki.vird;

import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.base.expr.Expr;
import com.ydo4ki.vird.base.lexer.ExprOutput;
import com.ydo4ki.vird.base.lexer.TokenOutput;

import java.io.*;
import java.nio.file.Files;

public class Main {
	public static void main(String[] __args) throws IOException {
		printPrjInfo(System.out);
		File src = new File("vird/file2.vird");
		
		Scope scope = new Scope(Vird.GLOBAL);
		for (Expr expr : new ExprOutput(new TokenOutput(src))) {
			Val result = Interpreter.evaluateFinale(scope, null, expr);
			System.out.println(result);
		}
	}
	
	public static void printPrjInfo(PrintStream out) throws IOException {
		File src = new File("src/main/java");
		out.println("###########################");
		out.println("Classes: " + countClasses(src));
		out.println("Lines of code: " + countLines(src));
		out.println("###########################");
		out.println();
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
