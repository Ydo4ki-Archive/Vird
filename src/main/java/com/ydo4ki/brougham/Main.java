package com.ydo4ki.brougham;

import com.ydo4ki.brougham.lang.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
	
	static final FunctionImpl DList2ToTuple = new FunctionImpl(
			new FunctionType(
					new TupleType(
							SymbolType.instance,
							SymbolType.instance
					).ref(),
					new TypeRef[]{
							DListType.of(BracketsType.ROUND).ref()
					}
			),
			(caller, args) -> {
				DList list = (DList) args[0];
				Val[] values = new Val[list.getElements().size()];
				int i = 0;
				for (Val element : list.getElements()) {
					values[i++] = element;
				}
				return new Tuple(values);
			}
	);
	static FunctionImpl DList2ToFunctionCall(TypeRef returnType) {
		return new FunctionImpl(
				new FunctionType(
						returnType,
						new TypeRef[]{
								DListType.of(BracketsType.ROUND).ref()
						}
				),
				(caller, args) -> {
					DList list = (DList) args[0];
					return test_function_evaluate(returnType, list);
				}
		);
	}
	
	public static void main(String[] __args) throws IOException {
		DList program = (DList) new Parser().read(null, "(+ 5 (+ 2 2))");
		System.out.println(program);
		
		program.defineFunction(new Symbol(""),
				DList2ToTuple,
				new FunctionImpl(
						new FunctionType(
								new TupleType(
										BlobType.of(4),
										BlobType.of(4)
								).ref(),
								new TypeRef[]{
										new TupleType(
												SymbolType.instance,
												SymbolType.instance
										).ref()
								}
						),
						(caller, args) -> new Tuple(
								Arrays.stream(((Tuple) args[0]).getValues())
										.map(e -> Blob.ofInt(Integer.parseInt(e.toString())))
										.toArray(Val[]::new)
						)
				),
				new FunctionImpl(
						new FunctionType(
								new TypeRef(BlobType.of(4)),
								new TypeRef[]{new TypeRef(SymbolType.instance)}
						),
						(caller, args) -> Blob.ofInt(Integer.parseInt(args[0].toString()))
				)
		);

//		FunctionImpl getMapFunction = new FunctionImpl(
//				new FunctionType(
//
//				)
//		)
		
		program.define(new Symbol("+"),
				new FunctionSet(
						new FunctionImpl(
								new FunctionType(
										BlobType.of(4).ref(),
										new TypeRef[]{
												BlobType.of(4).ref(),
												BlobType.of(4).ref()
										}
								),
								(caller, allArgs) -> {
									Blob a = (Blob) allArgs[0];
									Blob b = (Blob) allArgs[1];
									return Blob.ofInt(a.toInt() + b.toInt());
								}
						)
				)
		);
		System.out.println(test_function_evaluate(null, program));
	}
	
	
	static FunctionImpl mapFunction(Type[] from, Type[] to, FunctionImpl mapper) {
		return new FunctionImpl(
				new FunctionType(
						new TupleType(to).ref(),
						new TypeRef[]{new TupleType(from).ref()}
				),
				(caller, args) -> new Tuple(Arrays.stream(((Tuple)args[0]).getValues()).map(v -> mapper.invoke(caller, new Val[]{v})).toArray(Val[]::new))
		);
	}
	
	static Val test_function_evaluate(TypeRef expectedType, DList program) {
		Val functionName = program.getElements().get(0);
		if (!(functionName instanceof Symbol))
			throw new IllegalArgumentException("This is not the book club! " + functionName);
		
		final Val[] args;
		{
			List<Val> args0 = new ArrayList<>(program.getElements());
			args0.remove(0);
			args = args0.toArray(new Val[0]);
		}
		FunctionCall func = program.resolveFunctionImpl((Symbol) functionName, expectedType, args);
		if (func == null)
			throw new IllegalArgumentException("Function not found: " + functionName + " " + Arrays.toString(args));
		return func.invoke(program, args);
	}
}
