package com.ydo4ki.brougham;

import com.ydo4ki.brougham.lang.*;

import java.io.*;

public class Main {
	public static void main(String[] args) throws IOException {
		
		File source = new File("brougham/source.bham");
		DList program = (DList) ((DList) new Parser().read(null, source).getElements().get(1)).getElements().get(0);
		System.out.println(program);
		
		
		System.out.println(test_evaluate(program));
	}
	
	static Val test_evaluate(Val program) {
		if (program instanceof Tuple) {
			Tuple t = (Tuple) program;
			Val functionName = t.getValues()[0];
		}
		return program;
	}
}
