package com.ydo4ki.brougham.lang;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sulphuris
 * @since 4/8/2025 1:13 PM
 */
public final class FunctionSet implements Val {
	private final Set<FunctionImpl> specificFunctions = new HashSet<>();
	
	public FunctionSet(FunctionImpl... impl) {
		for (FunctionImpl function : impl) {
			addImpl(function);
		}
	}
	
	boolean cast = false;
	
	public boolean amIaCastFunction() {
		return cast;
	}
	
	@Override
	public Type getType() {
		return FunctionSetType.instance;
	}
	
	public void addImpl(FunctionImpl function) {
		if (findImplByType(function.getType()) != null)
			throw new IllegalArgumentException("This types of arguments are already occupied " + Arrays.toString(function.getType().getParams()));
		specificFunctions.add(function);
	}
	
	public FunctionImpl findImplByType(FunctionType type) {
		for (FunctionImpl function : specificFunctions) {
			if (function.getType().equals(type)) return function;
		}
		return null;
	}
	
	public FunctionCall findImplForArgs(DList caller, TypeRef expectedType, Val[] args) {
		return findImplForArgs(caller, expectedType, Arrays.stream(args).map(Val::getTypeRef).toArray(TypeRef[]::new));
	}
	public FunctionCall findImplForArgs(DList caller, TypeRef expectedType, TypeRef[] argsTypes) {
		System.out.println("# Finding function: " + Arrays.toString(argsTypes) + " -> " + expectedType);
		List<FunctionCall> candidates = new ArrayList<>();
		
		search:
		for (FunctionImpl function : specificFunctions) {
			boolean exactMatch = true;
			FunctionCall return_type_cast = null;
			TypeRef returnType = function.getType().getReturnType();
			if (expectedType != null) {
				if (returnType != null && !returnType.matchesType(expectedType)) {
					if (amIaCastFunction()) continue search;
					exactMatch = false;
					FunctionCall cast = caller.resolveFunctionImpl(new Symbol(""), expectedType, new TypeRef[]{returnType});
					if (cast == null) continue search;
					return_type_cast = cast;
				}
			} else {
				exactMatch = false;
			}
			
			TypeRef[] params = function.getType().getParams();
			FunctionCall[] casts = new FunctionCall[params.length];
			for (int i = 0; i < params.length; i++) {
				if (params[i].matchesType(argsTypes[i])) continue; // cast = null (not needed)
				else {
					exactMatch = false;
					FunctionCall cast;
					TypeRef neededReturnType = params[i];
					TypeRef inputWeHave = argsTypes[i];
					if (amIaCastFunction() && Objects.equals(neededReturnType, expectedType)) {
						cast = null;
					}else{
						cast = caller.resolveFunctionImpl(new Symbol(""), neededReturnType, new TypeRef[]{inputWeHave});
					}
					if (cast == null) continue search;
					casts[i] = cast;
				}
			}
			FunctionCall call = new FunctionCall(function, return_type_cast, casts);
			if (exactMatch) return call;
			else candidates.add(call);
		}
//		System.out.println("# candidates (" + Arrays.toString(argsTypes) + " -> " + expectedType + "): " + candidates);
		int minCasts = Integer.MAX_VALUE;
		for (FunctionCall candidate : candidates) {
			if (candidate.castsCount() < minCasts)
				minCasts = candidate.castsCount();
		}
		final int MinCasts = minCasts;
		candidates.removeIf(f -> f.castsCount() > MinCasts);
		List<FunctionCall> exact = candidates.stream()
				.filter(f -> !f.needsResultCast())
				.collect(Collectors.toList());
		if (exact.size() == 1) {
			return exact.get(0);
		}
		if (exact.isEmpty()) {
			if (candidates.size() == 1) {
				return candidates.get(0);
			}
			if (candidates.isEmpty()) {
				return null;
			}
			throw new IllegalArgumentException("Ambiguous call: " + candidates);
		}
		throw new IllegalArgumentException("Ambiguous call: " + exact);
	}
	
}

