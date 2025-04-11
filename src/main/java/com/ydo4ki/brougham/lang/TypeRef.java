package com.ydo4ki.brougham.lang;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Sulphuris
 * @since 4/9/2025 12:26 PM
 */
@EqualsAndHashCode
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class TypeRef {
	private final Type type;
	private final boolean vararg;
	private final ComplexComputingEquipment constraints;
	
	TypeRef(Type type, boolean vararg) {
		this(type, vararg, ComplexComputingEquipment.free);
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
}
