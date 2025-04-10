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
	public Type getRawType() {
		return FunctionSetType.instance;
	}
	
	public void addImpl(FunctionImpl function) {
		if (findImplByType(function.getRawType()) != null)
			throw new IllegalArgumentException("This types of arguments are already occupied " + Arrays.toString(function.getRawType().getParams()));
		specificFunctions.add(function);
	}
	
	public FunctionImpl findImplByType(FunctionType type) {
		for (FunctionImpl function : specificFunctions) {
			if (function.getRawType().equals(type)) return function;
		}
		return null;
	}
	
	public FunctionCall findImplForArgs(DList caller, TypeRef expectedType, Val[] args) {
		return findImplForArgs(caller, expectedType, Arrays.stream(args).map(Val::getType).toArray(TypeRef[]::new));
	}
	public FunctionCall findImplForArgs(DList caller, TypeRef expectedType, TypeRef[] argsTypes) {
//		System.out.println("# Finding function: " + Arrays.toString(argsTypes) + " -> " + expectedType);
		List<FunctionCall> candidates = new ArrayList<>();
		List<FunctionImpl> potentialTemplates = new ArrayList<>();
		
		search:
		for (FunctionImpl function : specificFunctions) {
			if (function.getRawType().isVarargFunction()) {
				if (function.getRawType().getParams().length > argsTypes.length) continue search;
			} else {
				if (function.getRawType().getParams().length != argsTypes.length) continue search;
			}
			
			FunctionCall call = FunctionCall.makeCall(caller, function, expectedType, argsTypes, amIaCastFunction());
			if (call == null) {
				if (!amIaCastFunction() && function.isPure() && (function.getRawType().getReturnType() == null
						|| function.getRawType().getReturnType().getType() instanceof FunctionType))
					potentialTemplates.add(function);
				continue search;
			}
			if (call.isExactMatch()) return call;
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
				TypeRef[] typesOfArgsTypes = Arrays.stream(argsTypes).map(t -> t.getType().getType()).toArray(TypeRef[]::new);
				Val[] rawTypes = Arrays.stream(argsTypes).map(TypeRef::getType).toArray(Val[]::new);
				for (FunctionImpl template : potentialTemplates) {
					FunctionCall call = FunctionCall.makeCall(
							caller, template, null, typesOfArgsTypes,
							amIaCastFunction()
					);
					if (call == null) continue;
					FunctionImpl toInvoke = (FunctionImpl)call.invoke(caller, rawTypes);
					return FunctionCall.makeCall(caller, toInvoke, expectedType, argsTypes, amIaCastFunction()); // todo: deal with all candidates
				}
				return null;
			}
			throw new IllegalArgumentException("Ambiguous call: " + candidates);
		}
		throw new IllegalArgumentException("Ambiguous call: " + exact);
	}
	
}

