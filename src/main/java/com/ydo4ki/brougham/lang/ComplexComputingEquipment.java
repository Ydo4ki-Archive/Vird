package com.ydo4ki.brougham.lang;

import java.util.Objects;

public abstract class ComplexComputingEquipment {
	abstract boolean test(Scope caller, Val val);
	
	abstract boolean contains(Scope caller, ComplexComputingEquipment constraint);
	
	public static final Free free = new Free();
	public static class Free extends ComplexComputingEquipment {
		Free() {}
		
		@Override
		boolean test(Scope caller, Val val) {
			return true;
		}
		
		@Override
		boolean contains(Scope caller, ComplexComputingEquipment constraint) {
			return true;
		}
	}
	
	public static class HasDefinedInContext extends ComplexComputingEquipment {
		private final TypeRef type;
		
		public HasDefinedInContext(TypeRef type) {
			this.type = type;
		}
		
		@Override
		boolean test(Scope caller, Val val) {
			Val resolved = caller.resolve(((Symbol)val));
			return resolved != null && type.matches(caller, resolved);
		}
		
		@Override
		boolean contains(Scope caller, ComplexComputingEquipment constraint) {
			if (this.equals(constraint)) return true;
			if (constraint instanceof Equality) {
				return test(caller, ((Equality) constraint).value);
			}
			return false;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			HasDefinedInContext that = (HasDefinedInContext) o;
			return Objects.equals(type, that.type);
		}
		
		@Override
		public int hashCode() {
			return Objects.hashCode(type);
		}
	}
	public static class Equality extends ComplexComputingEquipment {
		private final Val value;
		
		public Equality(Val value) {
			this.value = value;
		}
		
		public Val getValue() {
			return value;
		}
		
		@Override
		boolean test(Scope caller, Val val) {
			return val.equals(value);
		}
		
		@Override
		boolean contains(Scope caller, ComplexComputingEquipment constraint) {
			return this.equals(constraint);
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			Equality equality = (Equality) o;
			return Objects.equals(value, equality.value);
		}
		
		@Override
		public int hashCode() {
			return Objects.hashCode(value);
		}
	}
}
