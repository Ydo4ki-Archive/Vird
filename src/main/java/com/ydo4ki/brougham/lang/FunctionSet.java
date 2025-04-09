package com.ydo4ki.brougham.lang;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sulphuris
 * @since 4/8/2025 1:13 PM
 */
public final class FunctionSet implements Val {
	private final Set<FunctionImpl> specificFunctions = new HashSet<>();
	
	public FunctionSet(FunctionImpl impl) {
		addImpl(impl);
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
	
	public FunctionCall findImplForArgs(DList caller, Type expectedType, Val[] args) {
		return findImplForArgs(caller, expectedType, Arrays.stream(args).map(Val::getType).toArray(Type[]::new));
	}
	public FunctionCall findImplForArgs(DList caller, Type expectedType, Type[] argsTypes) {
		List<FunctionCall> candidates = new ArrayList<>();
		
		search:
		for (FunctionImpl function : specificFunctions) {
			boolean exactMatch = true;
			FunctionCall return_type_cast = null;
			Type returnType = function.getType().getReturnType();
			if (expectedType != null) {
				if (!returnType.equals(expectedType)) {
					exactMatch = false;
					FunctionCall cast = caller.resolveFunctionImpl(new Symbol(""), expectedType, new Type[]{returnType});
					if (cast == null) continue search;
					return_type_cast = cast;
				}
			} else {
				exactMatch = false;
			}
			
			Type[] params = function.getType().getParams();
			FunctionCall[] casts = new FunctionCall[params.length];
			for (int i = 0; i < params.length; i++) {
				if (params[i].equals(argsTypes[i])) continue; // cast = null (not needed)
				else {
					exactMatch = false;
					FunctionCall cast = caller.resolveFunctionImpl(new Symbol(""), params[i], new Type[]{argsTypes[i]});
					if (cast == null) continue search;
					casts[i] = cast;
				}
			}
			FunctionCall call = new FunctionCall(function, return_type_cast, casts);
			if (exactMatch) return call;
			else candidates.add(call);
		}
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
			if (candidates.size() == 1) return candidates.get(0);
			throw new IllegalArgumentException("Ambiguous call: " + candidates);
		}
		throw new IllegalArgumentException("Ambiguous call: " + exact);
	}
	
}

