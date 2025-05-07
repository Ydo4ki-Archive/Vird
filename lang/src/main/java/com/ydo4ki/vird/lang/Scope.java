package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.base.Val;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sulphuris
 * @since 4/11/2025 12:16 PM
 */
@RequiredArgsConstructor
public final class Scope extends Val {
	@Getter
	private final Scope parent;
	private final Map<String, Val> definedSymbols = new HashMap<>();
	
	public Declaration define(String name, Val value) {
		if (definedSymbols.containsKey(name))
			throw new IllegalArgumentException(name + " is already defined");
		definedSymbols.put(name, value);
		return new Declaration(name, value);
	}
	
	/** define val in this scope, return scope */
	public Scope d(String name, Val value) {
		define(name, value);
		return this;
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
}
