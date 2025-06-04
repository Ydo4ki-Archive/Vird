package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.Scope;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * @since 6/4/2025 5:36 PM
 * @author alignie
 */
public final class Struct extends Constraint {
	private final Constraint[] fields;
	
	public Struct(Constraint[] fields) {
		this.fields = fields;
	}
	
	@Override
	public boolean test(Scope scope, Val value) {
		if (value instanceof StructVal) {
			Val[] vals = ((StructVal) value).getVals();
			if (vals.length != fields.length) return false;
			for (int i = 0; i < vals.length; i++) {
				if (!fields[i].test(scope, vals[i])) return false;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean implies(Scope scope, Constraint other) {
		if (other instanceof Struct) {
			Constraint[] oFields = ((Struct) other).fields;
			if (oFields.length != fields.length) return false;
			for (int i = 0; i < oFields.length; i++) {
				if (!fields[i].implies(scope, oFields[i])) return false;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Struct struct = (Struct) o;
		return Objects.deepEquals(fields, struct.fields);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(fields);
	}
	
	@Getter
	public static final class StructVal extends Val {
		private final Val[] vals;
		
		public StructVal(Val[] vals) {
			this.vals = vals;
		}
		
		public Val getVal(int index) {
			return vals[index];
		}
		
		public Struct struct() {
			Constraint[] fields = new Constraint[vals.length];
			for (int i = 0; i < fields.length; i++) {
				fields[i] = new EqualityConstraint(vals[i]);
			}
			return new Struct(fields);
		}
	}
}
