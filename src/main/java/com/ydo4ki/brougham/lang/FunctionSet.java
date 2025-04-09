package com.ydo4ki.brougham.lang;

import java.util.*;

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
	
	public FunctionImpl findImplForArgs(Type expectedType, Val[] args) {
		List<FunctionImpl> candidates = new ArrayList<>(); // todo: implicit cast (search for implicit cast functions same way)
		for (FunctionImpl function : specificFunctions) {
			if ((expectedType == null || expectedType.equals(function.getType().getReturnType()))
					&& typeMatches(args, function.getType().getParams()))
				return function;
		}
		return null;
	}
	
	private static boolean typeMatches(Val[] args, Type[] types) {
		for (int i = 0; i < args.length; i++) {
			if (!args[i].getType().equals(types[i])) return false;
		}
		return true;
	}
}

