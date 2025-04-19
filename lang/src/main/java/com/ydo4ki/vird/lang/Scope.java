package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.Vird;
import com.ydo4ki.vird.base.Expr;
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
public final class Scope {
	@Getter
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
		if (dereferenced != null) return dereferenced;
		if (types.getFrom().getBaseType().equals(Expr.TYPE)) {
			return new ConversionRule(types, Vird.evaluate);
		}
		return parent == null ? null : parent.resolveConversionRule(types);
	}
	
	public <T extends Val> T define(String name, T value) {
		if (definedSymbols.containsKey(name))
			throw new IllegalArgumentException(name + " is already defined");
		definedSymbols.put(name, value);
		return value;
	}
	
	/** define val in scope, return it */
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
