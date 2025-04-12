package com.ydo4ki.brougham;

import com.ydo4ki.brougham.lang.*;

import java.io.*;

public class Main {
	public static void main(String[] __args) throws IOException {
		Interpreter interpreter = new Interpreter();
//		Val ret = interpreter.next("(include 'brougham/source.bham')");
//		Val ret = interpreter.next("(+ (evaluate (Blob4 5)) (Blob4 4))");
//		Val ret = interpreter.next("(typeOf (Blob4 5))");
		Source fileSource = new Source.OfFile(new File("brougham/file2.bham"));
		Val ret = null;
		try {
			do {
				ret = interpreter.next(fileSource);
				System.out.println(ret);
			} while(true);
		} catch (IOException e) {
		
		}
		
		
//		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while (true) try {
			ret = interpreter.next(in);
			System.out.println(ret);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		// in.close();
	}
}
