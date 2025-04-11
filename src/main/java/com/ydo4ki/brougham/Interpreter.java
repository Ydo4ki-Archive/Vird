package com.ydo4ki.brougham;

import com.ydo4ki.brougham.lang.*;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sulphuris
 * @since 4/10/2025 4:04 PM
 */
@Getter
public class Interpreter {
	private final Scope program = new Scope(null);
	
	public Interpreter() {
		program.define("Symbol", SymbolType.instance);
		program.define("DListB", DListType.of(BracketsType.BRACES));
		program.define("DListR", DListType.of(BracketsType.ROUND));
		program.define("DListS", DListType.of(BracketsType.SQUARE));
		
		program.defineFunction("define", new FunctionImpl(
				new FunctionType(
						BlobType.of(4).ref(),
						new TypeRef[]{
								SymbolType.instance.ref(),
								BlobType.of(4).ref()
						}
				),
				(caller, args) -> {
					caller.getParent().define(((Symbol) args[0]).getValue(), args[1]);
					return args[1];
				}, false
		));
		
		program.defineImplicitCast(
				BlobType.of(4).ref(), DListType.of(BracketsType.ROUND).ref(),
				(caller, arg) -> evaluate_function(caller, BlobType.of(4).ref(), (DList)arg),
				true
		);
		program.defineImplicitCast(
				BlobType.of(4).ref(), SymbolType.instance.ref(),
				(caller, arg) -> {
					try {
						return Blob.ofInt(Integer.parseInt(arg.toString()));
					} catch (NumberFormatException e) {
						return caller.resolve((Symbol) arg);
					}
				},
				true
		);
		program.defineImplicitCast(
				FunctionSetType.instance.ref(), SymbolType.instance.ref(
						new ComplexComputingEquipment.HasDefinedInContext(MetaType.of(0).ref())
				),
				(caller, arg) -> caller.resolveFunction(((Symbol) arg).getValue()),
				true
		);
		program.defineImplicitCast(
				MetaType.of(0).ref(),
				SymbolType.instance.ref(new ComplexComputingEquipment.HasDefinedInContext(MetaType.of(0).ref())),
				(caller, arg) -> {
					String name = ((Symbol) arg).getValue();
					Val type = caller.resolve(((Symbol) arg));
					if (type != null && MetaType.of(0).ref().matches(caller, type)) return type;
					throw new ThisIsNotTheBookClubException(name);
				}, true
		);
		
		
		program.define("+",
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
		
		program.defineFunction("include",
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
								return evaluate(caller, null, p);
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}, true
				)
		);
		program.defineFunction("run",
				new FunctionImpl(
						new FunctionType(
								null,
								new TypeRef[]{DListType.of(BracketsType.BRACES).ref()}
						),
						(caller, args) -> {
							DList body = ((DList) args[0]);
							Val last = null;
							for (Val val : body.getElements()) {
								last = evaluate(caller, null, val);
							}
							return last;
						}, true
				)
		);
	}
	
	public Val next(String in) throws IOException {
		return evaluate(program, null, new Parser().read(program, new Source.OfString(in)));
	}
	
	public Val next(BufferedReader in) throws IOException {
		return evaluate(program, null, new Parser().read(program, new Source.Raw(in)));
	}
	
	private Val evaluate(Scope caller, TypeRef expectedType, Val val) {
		if (expectedType != null && expectedType.matches(caller, val)) return val;
		if (val instanceof DList) return evaluate_function(caller, expectedType, (DList) val);
		if (val instanceof Symbol) return resolveFunctionSet((Symbol) val);
		if (expectedType != null) {
			// maybe find a cast function... idk
		}
		return val;
	}
	
	private Val evaluate_function(Scope caller, TypeRef expectedType, DList f) {
		Val functionId = f.getElements().get(0);
		Val function = evaluate(caller, null, functionId);
		if (function == null) {
			f.getLocation().print(System.err);
			throw new ThisIsNotTheBookClubException("Function not found: " + functionId);
		}
		
		final Val[] args;
		{
			List<Val> args0 = new ArrayList<>(f.getElements());
			args0.remove(0);
			args = args0.toArray(new Val[0]);
		}
		FunctionCall call = null;
		if (function instanceof FunctionSet) {
			FunctionSet set = (FunctionSet) function;
			call = set.findImplForArgs(f, expectedType, args);
			if (call == null) {
				f.getLocation().print(System.err);
				throw new IllegalArgumentException("Function not found: " + Arrays.stream(args).map(Val::getType).collect(Collectors.toList()));
			}
		} else if (function instanceof FunctionImpl) {
			call = function_call(f, (FunctionImpl) function, expectedType, args);
		}
		if (call == null) {
			f.getLocation().print(System.err);
			throw new ThisIsNotTheBookClubException("Cannot create function call: " + String.valueOf(function));
		}
		
		return call.invoke(f, args);
	}
	
	private FunctionSet resolveFunctionSet(Symbol name) {
		return name.getParent().resolveFunction(name.getValue());
	}
	
	private FunctionCall function_call(TypeRef expectedType, Symbol name, Val[] args) {
		FunctionCall func = name.getParent().resolveFunctionImpl(name.getValue(), expectedType, args);
		if (func == null)
			throw new IllegalArgumentException("Function not found: " + name + " " + Arrays.stream(args).map(Val::getType).collect(Collectors.toList()));
		return func;
	}
	
	private FunctionCall function_call(Scope caller, FunctionImpl function, TypeRef expectedType, Val[] args) {
		FunctionCall call = FunctionCall.makeCall(caller, function, expectedType, Arrays.stream(args).map(Val::getType).toArray(TypeRef[]::new), false);
		if (call == null) {
			throw new IllegalArgumentException("Function not found: " + Arrays.stream(args).map(Val::getType).collect(Collectors.toList()));
		}
		return call;
	}
}
