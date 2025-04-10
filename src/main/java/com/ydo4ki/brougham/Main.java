package com.ydo4ki.brougham;

import com.ydo4ki.brougham.lang.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
		DList program = (DList) new Parser().read(null, "(include 'brougham/source.bham')");
		System.out.println(program);
		
		program.defineFunction(new Symbol(""),
				DList2ToTuple,
				DList2ToFunctionCall(null),
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
//						null,
//						new TypeRef[]{MetaType.of(0).ref(), MetaType.of(0).ref(), BlobType.of(4).ref()}
//				),
//				(caller, args) -> {
//					Type from = ((Type)args[0]);
//					Type to = ((Type)args[1]);
//					Blob amount = ((Blob)args[2]);
//					return mapFunction()
//				}
//		);
		
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
		
		program.defineFunction(new Symbol("include"),
				new FunctionImpl(
						new FunctionType(
								null, //DListType.of(BracketsType.ROUND).ref(),
								new TypeRef[]{SymbolType.instance.ref()}
						),
						(caller, args) -> {
							String fileName = args[0].toString();
							fileName = fileName.substring(1, fileName.length()-1); // remove quotes
							try {
								return evaluate(null, ((DList)new Parser().read(caller, new File(fileName))).getElements().get(1));
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
				)
		);
		
		System.out.println(test_function_evaluate(null, program));
	}
	
	
//	static FunctionImpl mapFunction(Type from, Type to, int size, FunctionImpl mapper) {
//		Type[] fromTuple = new Type[size];
//		Arrays.fill(fromTuple, from);
//		Type[] toTuple = new Type[size];
//		Arrays.fill(toTuple, to);
//		return new FunctionImpl(
//				new FunctionType(
//						new TupleType(toTuple).ref(),
//						new TypeRef[]{new TupleType(fromTuple).ref()}
//				),
//				(caller, args) -> new Tuple(Arrays.stream(((Tuple)args[0]).getValues()).map(v -> mapper.invoke(caller, new Val[]{v})).toArray(Val[]::new))
//		);
//	}
	
	static Val evaluate(TypeRef expectedType, Val val) {
		if (expectedType != null && expectedType.matches(val)) return val;
		if (val instanceof DList) return test_function_evaluate(expectedType, (DList)val);
		if (expectedType != null) {
			// maybe find cast function... idk
		}
		return val;
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
			throw new IllegalArgumentException("Function not found: " + functionName + " " + Arrays.stream(args).map(Val::getTypeRef).collect(Collectors.toList()));
		return func.invoke(program, args);
	}
}
