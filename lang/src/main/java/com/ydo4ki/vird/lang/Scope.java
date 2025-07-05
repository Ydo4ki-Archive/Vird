package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.ast.Expr;
import com.ydo4ki.vird.ast.Location;
import com.ydo4ki.vird.lang.constraint.FreeConstraint;
import com.ydo4ki.vird.project.Stability;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sulphuris
 * @since 4/11/2025 12:16 PM
 */
@Stability(Stability.DESIRE_REP)
@RequiredArgsConstructor
public final class Scope implements Env {
	@Getter
	private final Env parent;
	private final Map<String, Val> definedSymbols = new HashMap<>();
	private final Map<String, ValidatedValCall> preDefinedSymbols = new HashMap<>();
	
	public void predefine(Location errLocation, String name, ValidatedValCall constraint) throws LangValidationException {
		if (preDefinedSymbols.containsKey(name))
			throw new LangValidationException(errLocation, name + " is already predefined");
		preDefinedSymbols.put(name, constraint);
	}
	
	public Val define(String name) throws RuntimeOperation {
		ValidatedValCall call = preDefinedSymbols.get(name);
		Val v = call.invoke();
		definedSymbols.put(name, v);
		return v;
	}
	@Override
	public ValidatedValCall preresolve(String name, Expr expr) throws LangValidationException {
		ValidatedValCall dereferenced = preDefinedSymbols.get(name);
		return dereferenced != null || parent == null ? dereferenced : parent.preresolve(name, expr);
	}
	@Override
	public Val resolve(String name) throws RuntimeOperation {
		Val dereferenced = definedSymbols.get(name);
		return dereferenced != null || parent == null ? dereferenced : parent.resolve(name);
	}
	
	
	public Scope push(String name, Val value) {
		if (definedSymbols.containsKey(name))
			throw new IllegalArgumentException(name + " is already defined");
		definedSymbols.put(name, value);
		preDefinedSymbols.put(name, ValidatedValCall.promiseVal(value));
		return this;
	}
	
	@Override
	public Type getType() {
		return type;
	}

	private static final Type type = new Type(FreeConstraint.INSTANCE);
}
