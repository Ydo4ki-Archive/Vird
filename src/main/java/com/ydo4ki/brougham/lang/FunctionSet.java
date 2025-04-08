package com.ydo4ki.brougham.lang;

import java.util.*;

/**
 * @author Sulphuris
 * @since 4/8/2025 1:13 PM
 */
public final class FunctionSet implements Val {
	private static final Set<FunctionImpl> specificFunctions = new HashSet<>();
	
	public FunctionSet(FunctionImpl impl) {
		addImpl(impl);
	}
	
	@Override
	public Type getType() {
		return FunctionSetType.instance;
	}
	
	public void addImpl(FunctionImpl function) {
		if (findImplByParams(function.getType().getParams()) != null)
			throw new IllegalArgumentException("This types of arguments are already occupied " + Arrays.toString(function.getType().getParams()));
		specificFunctions.add(function);
	}
	
	public FunctionImpl findImplByParams(Type[] params) {
		for (FunctionImpl function : specificFunctions) {
			if (Arrays.deepEquals(function.getType().getParams(), params)) return function;
		}
		return null;
	}
	
	public FunctionImpl findImplForArgs(List<Val> args) {
		List<FunctionImpl> candidates = new ArrayList<>(); // todo: implicit cast (search for implicit cast functions same way)
		for (FunctionImpl function : specificFunctions) {
			if (typeMatches(args, function.getType().getParams())) return function;
		}
		return null;
	}
	
	private static boolean typeMatches(List<Val> args, Type[] types) {
		int i = 0;
		for (Val arg : args) {
			if (!arg.getType().equals(types[i++])) return false;
		}
		return true;
	}
}

