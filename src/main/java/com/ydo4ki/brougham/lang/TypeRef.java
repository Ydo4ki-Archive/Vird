package com.ydo4ki.brougham.lang;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Sulphuris
 * @since 4/9/2025 12:26 PM
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class TypeRef implements Val {
	private final Type targetType;
	private final boolean vararg;
	private final ComplexComputingEquipment constraints;
	
	TypeRef(Type targetType, boolean vararg) {
		this(targetType, vararg, ComplexComputingEquipment.free);
	}
	
	public TypeRef also(ComplexComputingEquipment constraints) {
		return new TypeRef(targetType, vararg, ComplexComputingEquipment.And.of(this.constraints, constraints));
	}
	
	public boolean matches(Scope caller, Val val) {
		if (!val.getRawType().equals(targetType)) return false;
		return constraints.test(caller, val);
	}
	public boolean valueOfGivenTypeMatchesMe(Scope caller, TypeRef other) {
		if (!this.targetType.equals(other.targetType)) return false;
		return this.constraints.contains(caller, other.constraints);
	}
	
	@Override
	public String toString() {
		return targetType.toString();
	}
	
	@Override
	public Type getRawType() {
		return TypeRefType.instance;
	}
}
