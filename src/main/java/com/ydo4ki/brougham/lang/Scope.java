package com.ydo4ki.brougham.lang;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author Sulphuris
 * @since 4/11/2025 12:16 PM
 */
@RequiredArgsConstructor
@Getter
public class Scope {
	private final Scope parent;
	private final Map<String, Val> definedSymbols = new HashMap<>();
	
	public void define(String name, Val value) {
		if (definedSymbols.containsKey(name))
			throw new IllegalArgumentException(name + " is already defined");
		if (name.isEmpty() && value instanceof FunctionSet) ((FunctionSet)value).cast = true;
		definedSymbols.put(name, value);
	}
	public void defineFunction(String name, FunctionImpl... function) {
		FunctionSet set = resolveFunction(name);
		if (set == null) {
			set = new FunctionSet(function);
			define(name, set);
		} else {
			for (FunctionImpl f : function) {
				set.addImpl(f);
			}
		}
	}
	public void defineImplicitCast(TypeRef returnType, TypeRef argType, BiFunction<Scope, Val, Val> transformer, boolean pure) {
		defineFunction("",
				new FunctionImpl(
						new FunctionType(
								returnType,
								new TypeRef[]{argType}
						),
						(caller, args) -> transformer.apply(caller, args[0]), pure
				)
		);
	}
	
	
	
	public Val resolve(Symbol symbol) {
		Val dereferenced = definedSymbols.get(symbol.getValue());
		if (dereferenced != null) return dereferenced;
		return parent == null ? null : parent.resolve(symbol);
	}
	public FunctionSet resolveFunction(String name) {
		Val dereferenced = definedSymbols.get(name);
		if (dereferenced instanceof FunctionSet) return (FunctionSet)dereferenced;
		return parent == null ? null : parent.resolveFunction(name);
	}
	
	private FunctionSet resolveFunctionNoParents(String name) {
		Val dereferenced = definedSymbols.get(name);
		if (dereferenced instanceof FunctionSet) return (FunctionSet)dereferenced;
		return null;
	}
	public FunctionCall resolveFunctionImpl(String name, TypeRef returnType, Val[] args) {
		return resolveFunctionImpl(name, returnType, Arrays.stream(args).map(Val::getType).toArray(TypeRef[]::new));
	}
	public FunctionCall resolveFunctionImpl(String name, TypeRef returnType, TypeRef[] argTypes) {
		Scope caller = this;
		while (true) {
			FunctionSet functionSet = caller.resolveFunctionNoParents(name);
			if (functionSet != null) {
				FunctionCall call = functionSet.findImplForArgs(caller, returnType, argTypes);
				if (call != null) return call;
			}
			caller = caller.parent;
			if (caller == null) return null;
		}
	}
}
