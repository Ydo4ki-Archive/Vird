package com.ydo4ki.brougham.lang;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ComplexComputingEquipment {
	abstract boolean test(Scope caller, Val val);
	
	public final boolean contains(Scope caller, ComplexComputingEquipment constraint) {
		if (this.equals(constraint)) return true;
		if (constraint instanceof Equality) {
			return test(caller, ((Equality) constraint).value);
		}
		return containsIfNotEquals(caller, constraint);
	}
	
	abstract boolean containsIfNotEquals(Scope caller, ComplexComputingEquipment constraint);
	
	public static final Free free = new Free();
	public static class Free extends ComplexComputingEquipment {
		Free() {}
		
		@Override
		boolean test(Scope caller, Val val) {
			return true;
		}
		
		@Override
		boolean containsIfNotEquals(Scope caller, ComplexComputingEquipment constraint) {
			return true;
		}
	}
	
	@Getter
	@RequiredArgsConstructor
	@EqualsAndHashCode(callSuper = false)
	public static class HasDefinedInContext extends ComplexComputingEquipment {
		private final TypeRef type;
		
		@Override
		boolean test(Scope caller, Val val) {
			Val resolved = caller.resolve(((Symbol)val));
			return resolved != null && type.matches(caller, resolved);
		}
		
		@Override
		boolean containsIfNotEquals(Scope caller, ComplexComputingEquipment constraint) {
			return false;
		}
	}
	@Getter
	@RequiredArgsConstructor
	@EqualsAndHashCode(callSuper = false)
	public static class Equality extends ComplexComputingEquipment {
		private final Val value;
		
		@Override
		boolean test(Scope caller, Val val) {
			return val.equals(value);
		}
		
		@Override
		boolean containsIfNotEquals(Scope caller, ComplexComputingEquipment constraint) {
			return false;
		}
	}
	public static final IsSymbol isSymbol = new IsSymbol();
	public static class IsSymbol extends ComplexComputingEquipment {
		IsSymbol() {}
		
		@Override
		boolean test(Scope caller, Val val) {
			return val instanceof Symbol;
		}
		
		@Override
		boolean containsIfNotEquals(Scope caller, ComplexComputingEquipment constraint) {
			return false;
		}
	}
	
	@Getter
	@RequiredArgsConstructor
	@EqualsAndHashCode(callSuper = false)
	public static class IsDList extends ComplexComputingEquipment {
		private final BracketsType bracketsType;
		
		@Override
		boolean test(Scope caller, Val val) {
			return (val instanceof DList) && ((DList)val).getBracketsType() == bracketsType;
		}
		
		@Override
		boolean containsIfNotEquals(Scope caller, ComplexComputingEquipment constraint) {
			return false;
		}
	}
	
	@Getter
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@EqualsAndHashCode(callSuper = false)
	public static class And extends ComplexComputingEquipment {
		private final Set<ComplexComputingEquipment> conditions;
		
		public static And of(ComplexComputingEquipment... eq) {
			return new And(Arrays.stream(eq).filter(e -> !(e instanceof Free)).collect(Collectors.toSet()));
		}
		
		@Override
		boolean test(Scope caller, Val val) {
			for (ComplexComputingEquipment condition : conditions) {
				if(!condition.test(caller, val)) return false;
			}
			return true;
		}
		
		@Override
		boolean containsIfNotEquals(Scope caller, ComplexComputingEquipment constraint) {
			for (ComplexComputingEquipment condition : conditions) {
				if(!condition.containsIfNotEquals(caller, constraint)) return false;
			}
			return true;
		}
	}
}
