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
	private final Map<ConversionRule.ConversionTypes, ConversionRule> conversionRules = new HashMap<>();
	
	public void defineConversionRule(ConversionRule rule) {
		if (conversionRules.containsKey(rule.getTypes()))
			throw new IllegalArgumentException("Conversion rule " + rule.getTypes() + " is already defined");
		conversionRules.put(rule.getTypes(), rule);
	}
	
	public ConversionRule resolveConversionRule(ConversionRule.ConversionTypes types) {
		ConversionRule dereferenced = conversionRules.get(types);
		return dereferenced != null || parent == null ? dereferenced : parent.resolveConversionRule(types);
	}
	
	public <T extends Val> T define(String name, T value) {
		if (definedSymbols.containsKey(name))
			throw new IllegalArgumentException(name + " is already defined");
		definedSymbols.put(name, value);
		return value;
	}
	
	public Val resolve(String name) {
		Val dereferenced = definedSymbols.get(name);
		return dereferenced != null || parent == null ? dereferenced : parent.resolve(name);
	}
	public <T extends Val> T resolve(String name, Class<T> jtype) {
		Val value = resolve(name);
		if (jtype.isInstance(value)) //noinspection unchecked
			return (T)value;
		return null;
	}
	
	
	
	
	
	
	
//	public void defineFunction(String name, FunctionImpl... function) {
//		FunctionSet set = resolveFunction(name);
//		if (set == null) {
//			set = new FunctionSetImpl(function);
//			define(name, set);
//		} else if (set instanceof FunctionSetImpl ){
//			for (FunctionImpl f : function) {
//				((FunctionSetImpl)set).addImpl(f);
//			}
//		}
//	}
//	public FunctionImpl resolveImplicitCast(TypeRef returnType, TypeRef argType) {
//
//	}
//	public void defineImplicitCast(TypeRef returnType, TypeRef argType, BiFunction<Scope, Val, Val> transformer, boolean pure) {
//		defineFunction("",
//				new FunctionImpl(
//						new FunctionType(
//								returnType,
//								new TypeRef[]{argType}
//						),
//						(caller, args) -> transformer.apply(caller, args[0]), pure
//				)
//		);
//	}
	
	

//	public FunctionSet resolveFunction(String name) {
//		Val dereferenced = definedSymbols.get(name);
//		if (dereferenced instanceof FunctionSet) return (FunctionSet)dereferenced;
//		return parent == null ? null : parent.resolveFunction(name);
//	}
//
//	private FunctionSet resolveFunctionNoParents(String name) {
//		Val dereferenced = definedSymbols.get(name);
//		if (dereferenced instanceof FunctionSet) return (FunctionSet)dereferenced;
//		return null;
//	}
//	public FunctionCall resolveFunctionImpl(String name, TypeRef returnType, Val[] args) {
//		return resolveFunctionImpl(name, returnType, Arrays.stream(args).map(Val::getType).toArray(TypeRef[]::new));
//	}
//	public FunctionCall resolveFunctionImpl(String name, TypeRef returnType, TypeRef[] argTypes) {
//		Scope caller = this;
//		while (true) {
//			FunctionSet functionSet = caller.resolveFunctionNoParents(name);
//			if (functionSet != null) {
//				FunctionCall call = functionSet.makeCall(caller, returnType, argTypes);
//				if (call != null) return call;
//			}
//			caller = caller.parent;
//			if (caller == null) return null;
//		}
//	}
}
