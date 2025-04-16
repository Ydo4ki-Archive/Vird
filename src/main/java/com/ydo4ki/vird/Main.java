package com.ydo4ki.vird;

import com.ydo4ki.vird.lang.*;

import java.io.*;
import java.nio.file.Files;

public class Main {
	@SuppressWarnings("InfiniteLoopStatement")
	public static void main(String[] __args) throws IOException {
		printPrjInfo(System.out);
		Interpreter interpreter = new Interpreter();
//		Val ret = interpreter.next("(include 'vird/source.vird')");
//		Val ret = interpreter.next("(+ (evaluate (Blob4 5)) (Blob4 4))");
//		Val ret = interpreter.next("(typeOf (Blob4 5))");
		Source fileSource = new Source.OfFile(new File("vird/file2.vird"));
		Val ret = null;
//		try {
//			for (;;) {
//				System.out.println(ret = interpreter.next(fileSource));
//			}
//		} catch (IOException ignored) {
//
//		}
		System.exit(0);
		
//		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		for (;;) try {
//			System.out.println(ret = interpreter.next(in));
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		// in.close();
	}
	
	private static void printPrjInfo(PrintStream out) throws IOException {
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
