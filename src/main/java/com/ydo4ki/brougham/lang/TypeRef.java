package com.ydo4ki.brougham.lang;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Sulphuris
 * @since 4/9/2025 12:26 PM
 */
public final class TypeRef {
	private final Type type;
	private final boolean vararg;
	private final ComplexComputingEquipment constraints;
	
	public TypeRef(Type type, boolean vararg) {
		this(type, vararg, ComplexComputingEquipment.free);
	}
	
	TypeRef(Type type, boolean vararg, ComplexComputingEquipment constraints) {
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
	
	public boolean matches(DList caller, Val val) {
		if (!val.getRawType().equals(type)) return false;
		return constraints.test(caller, val);
	}
	public boolean valueOfGivenTypeMatchesMe(DList caller, TypeRef other) {
		if (!this.type.equals(other.type)) return false;
		return this.constraints.contains(caller, other.constraints);
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		TypeRef typeRef = (TypeRef) o;
		return vararg == typeRef.vararg && Objects.equals(type, typeRef.type) && Objects.equals(constraints, typeRef.constraints);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, vararg, constraints);
	}
}
