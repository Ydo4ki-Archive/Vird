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
			}, true
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
					return test_function_evaluate(caller, returnType, list);
				}, true
		);
	}
	
	public static void main(String[] __args) throws IOException {
		DList program = (DList) new Parser().read(null, "(include 'brougham/source.bham')");
		System.out.println(program);
		
		program.define(new Symbol(program, "Symbol"), SymbolType.instance);
		program.define(new Symbol(program, "DListB"), DListType.of(BracketsType.BRACES));
		program.define(new Symbol(program, "DListR"), DListType.of(BracketsType.ROUND));
		program.define(new Symbol(program, "DListS"), DListType.of(BracketsType.SQUARE));
		
		program.defineFunction(new Symbol(program, ""),
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
						), true
				),
				new FunctionImpl(
						new FunctionType(
								BlobType.of(4).ref(),
								new TypeRef[]{SymbolType.instance.ref()}
						),
						(caller, args) -> Blob.ofInt(Integer.parseInt(args[0].toString())),
						true
				),
				new FunctionImpl(
						new FunctionType(
								FunctionSetType.instance.ref(),
								new TypeRef[]{SymbolType.instance.ref(
										new ComplexComputingEquipment.HasDefinedInContext(MetaType.of(0).ref())
								)}
						),
						(caller, args) -> caller.resolveFunction((Symbol) args[0]),
						true
				),
				new FunctionImpl(
						new FunctionType(
								MetaType.of(0).ref(),
								new TypeRef[]{SymbolType.instance.ref(new ComplexComputingEquipment.HasDefinedInContext(MetaType.of(0).ref()))}
						),
						(caller, args) -> {
							String name = ((Symbol)args[0]).getValue();
							Val type = caller.resolve(((Symbol)args[0]));
							if (type != null && MetaType.of(0).ref().matches(caller, type)) return type;
//							switch (name) {
//								case "Symbol":
//									return SymbolType.instance;
//								case "DListR":
//									return DListType.of(BracketsType.ROUND);
//							}
							throw new ThisIsNotTheBookClubException(name);
						}, true
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
		
		program.define(new Symbol(program, "+"),
				new FunctionSet(
						new FunctionImpl(
								new FunctionType(
										BlobType.of(4).ref(),
										new TypeRef[]{
												BlobType.of(4).vararg(),
										}
								),
								(caller, allArgs) -> {
									int sum = 0;
									for (Val arg : allArgs) {
										sum += ((Blob) arg).toInt();
									}
									return Blob.ofInt(sum);
								}, true
						)
				)
		);
		
		program.defineFunction(new Symbol(program, "include"),
				new FunctionImpl(
						new FunctionType(
								null, //DListType.of(BracketsType.ROUND).ref(),
								new TypeRef[]{SymbolType.instance.ref()}
						),
						(caller, args) -> {
							String fileName = args[0].toString();
							fileName = fileName.substring(1, fileName.length() - 1); // remove quotes
							try {
								Val p = ((DList) new Parser().read(caller, new File(fileName))).getElements().get(1);
//								System.out.println(p);
								return evaluate(caller,null, p);
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}, true
				)
		);
		program.defineFunction(new Symbol(program, "run&"),
				new FunctionImpl(
						new FunctionType(
								null,
								new TypeRef[]{MetaType.of(0).vararg()}
						),
						(caller_, args_) -> {
							TypeRef[] argumentTypes = new TypeRef[args_.length];
							for (int i = 0; i < argumentTypes.length; i++) {
								argumentTypes[i] = ((Type)args_[i]).ref();
							}
							return new FunctionImpl(
									new FunctionType(
											null,
											argumentTypes
									),
									(caller, args) -> {
										Val last = null;
										for (Val arg : args) {
											last = evaluate(caller, null, arg);
										}
										return last;
									}, true
							);
						}, true
				)
		);
		program.defineFunction(new Symbol(program,"run"),
				new FunctionImpl(
						new FunctionType(
								null,
								new TypeRef[]{DListType.of(BracketsType.BRACES).ref()}
						),
						(caller, args) -> {
							DList body = ((DList)args[0]);
							Val last = null;
							for (Val val : body.getElements()) {
								last = evaluate(caller, null, val);
							}
							return last;
						}, true
				)
		);

		System.out.println(test_function_evaluate(program, null, program));
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
	
	static Val evaluate(DList caller, TypeRef expectedType, Val val) {
		if (expectedType != null && expectedType.matches(caller, val)) return val;
		if (val instanceof DList) return test_function_evaluate(caller, expectedType, (DList) val);
		if (val instanceof Symbol) return resolveFunctionSet((Symbol) val);
		if (expectedType != null) {
			// maybe find a cast function... idk
		}
		return val;
	}
	
	static Val test_function_evaluate(DList caller, TypeRef expectedType, DList program) {
		Val functionId = program.getElements().get(0);
		Val function = evaluate(caller, null, functionId);
		
		
		final Val[] args;
		{
			List<Val> args0 = new ArrayList<>(program.getElements());
			args0.remove(0);
			args = args0.toArray(new Val[0]);
		}
		FunctionCall call = null;
		if (function instanceof FunctionSet) {
			FunctionSet set = (FunctionSet) function;
			call = set.findImplForArgs(program, expectedType, args);
			if (call == null)
				throw new IllegalArgumentException("Function not found: " + Arrays.stream(args).map(Val::getType).collect(Collectors.toList()));
		} else if (function instanceof FunctionImpl) {
			call = function_call(program, (FunctionImpl)function, expectedType, args);
		}
		if (call == null) throw new ThisIsNotTheBookClubException(String.valueOf(function));
		
		return call.invoke(program, args);
	}
	
	static FunctionSet resolveFunctionSet(Symbol name) {
		return name.getParent().resolveFunction(name);
	}
	
	static FunctionCall function_call(TypeRef expectedType, Symbol name, Val[] args) {
		FunctionCall func = name.getParent().resolveFunctionImpl(name.getValue(), expectedType, args);
		if (func == null)
			throw new IllegalArgumentException("Function not found: " + name + " " + Arrays.stream(args).map(Val::getType).collect(Collectors.toList()));
		return func;
	}
	
	static FunctionCall function_call(DList caller, FunctionImpl function, TypeRef expectedType, Val[] args) {
		FunctionCall call = FunctionCall.makeCall(caller, function, expectedType, Arrays.stream(args).map(Val::getType).toArray(TypeRef[]::new), false);
		if (call == null) {
			throw new IllegalArgumentException("Function not found: " + Arrays.stream(args).map(Val::getType).collect(Collectors.toList()));
		}
		return call;
	}
}
