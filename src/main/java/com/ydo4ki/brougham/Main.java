package com.ydo4ki.brougham;

import com.ydo4ki.brougham.lang.*;

import java.io.*;

public class Main {
	public static void main(String[] __args) throws IOException {
		Interpreter interpreter = new Interpreter();
//		Val ret = interpreter.next("(include 'brougham/source.bham')");
		Val ret = interpreter.next("(+ (Blob4 5) (Blob4 4))");
		System.out.println(ret);
		
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
