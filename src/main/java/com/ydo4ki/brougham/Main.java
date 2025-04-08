package com.ydo4ki.brougham;

import com.ydo4ki.brougham.lang.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) throws IOException {
		
		File source = new File("brougham/source.bham");
		DList program = (DList) ((DList) new Parser().read(null, source).getElements().get(1)).getElements().get(0);
		System.out.println(program);
		
		
		System.out.println(test_function_evaluate(program));
	}
	
	static Val test_function_evaluate(DList program) {
		Val functionName = program.getElements().get(0);
		if (!(functionName instanceof Symbol)) throw new IllegalArgumentException("This is not the book club! " + functionName);
		FunctionSet functionSet = program.resolveFunction((Symbol)functionName);
		if (functionSet == null) throw new IllegalArgumentException("Function not found: " + functionName);
		List<Val> args = new ArrayList<>(program.getElements());
		args.remove(0);
		FunctionImpl func = functionSet.findImplForArgs(args);
		if (func == null) throw new IllegalArgumentException("Function for specific arg types not found: " + args);
		return func.invoke(args.toArray(new Val[0]));
	}
}
