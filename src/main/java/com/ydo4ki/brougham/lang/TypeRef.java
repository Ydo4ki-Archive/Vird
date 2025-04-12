package com.ydo4ki.brougham.lang;

import com.ydo4ki.brougham.lang.constraint.AndConstraint;
import com.ydo4ki.brougham.lang.constraint.Constraint;
import com.ydo4ki.brougham.lang.constraint.FreeConstraint;
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
	private final Type baseType;
	private final boolean vararg;
	private final Constraint constraint;
	
	TypeRef(Type baseType, boolean vararg) {
		this(baseType, vararg, FreeConstraint.INSTANCE);
	}
	
	public TypeRef also(Constraint constraints) {
		return new TypeRef(baseType, vararg, AndConstraint.of(this.constraint, constraints));
	}
	
	public boolean matches(Scope scope, Val val) {
		if (!val.getRawType().equals(baseType)) return false;
		return constraint.test(scope, val);
	}
	public boolean isCompatibleWith(Scope scope, TypeRef other) {
		if (!this.baseType.equals(other.baseType)) return false;
		
		return this.constraint.implies(scope, other.constraint);
	}
	
	@Override
	public String toString() {
		return baseType.toString();
	}
	
	@Override
	public Type getRawType() {
		return TypeRefType.instance;
	}
}
