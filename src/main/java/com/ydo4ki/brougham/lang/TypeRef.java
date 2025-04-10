package com.ydo4ki.brougham.lang;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Sulphuris
 * @since 4/9/2025 12:26 PM
 */
public final class TypeRef {
	private final Type type;
	private final boolean vararg;
	private final Predicate<Val> constraints;
	
	public TypeRef(Type type, boolean vararg) {
		this(type, vararg, val -> true);
	}
	// todo
	private TypeRef(Type type, boolean vararg, Predicate<Val> constraints) {
		this.type = type;
		this.vararg = vararg;
		this.constraints = constraints;
	}
	
	public Type getType() {
		return type;
	}
	
	public boolean isVararg() {
		return vararg;
	}
	
	public boolean matches(Val val) {
		return val.getRawType().equals(type) && constraints.test(val);
	}
	public boolean matchesType(TypeRef type) {
		return this.equals(type);
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
	
	// TEMP
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		TypeRef typeRef = (TypeRef) o;
		return Objects.equals(type, typeRef.type);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type);
	}
}
