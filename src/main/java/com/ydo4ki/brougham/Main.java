package com.ydo4ki.brougham;

import com.ydo4ki.brougham.lang.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
	public static void main(String[] __args) throws IOException {
		File source = new File("brougham/source.bham");
		DList program = (DList) ((DList) new Parser().read(null, source).getElements().get(1)).getElements().get(0);
		System.out.println(program);
		
		program.define(new Symbol("+"),
				new FunctionSet(
						new FunctionImpl(
								new FunctionType(
										BlobType.of(4),
										new Type[]{
												new TupleType(
														BlobType.of(4),
														BlobType.of(4)
												)
										}
								),
								(allArgs) -> {
									Tuple args = (Tuple)allArgs[0];
									Blob a = (Blob)args.getValues()[0];
									Blob b = (Blob)args.getValues()[1];
									return Blob.ofInt(a.toInt() + b.toInt());
								}
						)
				)
		);
		System.out.println(test_function_evaluate(null, program));
	}
	
	static Val test_function_evaluate(Type expectedType, DList program) {
		Val functionName = program.getElements().get(0);
		if (!(functionName instanceof Symbol))
			throw new IllegalArgumentException("This is not the book club! " + functionName);
		
		FunctionSet functionSet = program.resolveFunction((Symbol)functionName);
		if (functionSet == null)
			throw new IllegalArgumentException("Function not found: " + functionName);
		
		final Val[] args; {
			List<Val> args0 = new ArrayList<>(program.getElements());
			args0.remove(0);
			args = args0.toArray(new Val[0]);
		}
		FunctionImpl func = functionSet.findImplForArgs(expectedType, args);
		if (func == null)
			throw new IllegalArgumentException("Function for specific arg types not found: " + Arrays.toString(args));
		return func.invoke(args);
	}
}
