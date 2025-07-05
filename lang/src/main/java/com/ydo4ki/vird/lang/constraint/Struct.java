package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.ast.Location;
import com.ydo4ki.vird.lang.*;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * @since 6/4/2025 5:36 PM
 * @author alignie
 */
@Getter
public final class Struct extends PrimitiveConstraint {
	private final Constraint[] fields;
	
	public Struct(Constraint... fields) {
		this.fields = fields;
	}
	
	public ValidatedValCall newVal(Env env, Location location, Val... vals) throws LangValidationException {
		if (vals.length != fields.length)
			throw new LangValidationException(location, "Invalid fields amount (" + vals.length + ", " + fields.length + " expected)");
		
		for (int i = 0; i < vals.length; i++) {
			if (!fields[i].test(env, vals[i]))
				throw new LangValidationException(location, "Constraint violation: " + vals[i] + " does not satisfy " + fields[i]);
		}
		
		return new ValidatedValCall(new EqualityConstraint(new StructVal(vals))) {
			@Override
			protected Val invoke0() {
				return new StructVal(vals);
			}
		};
	}
	
	
	@Override
	public boolean test(Env env, Val value) {
		if (value instanceof StructVal) {
			Val[] vals = ((StructVal) value).getVals();
			if (vals.length != fields.length) return false;
			for (int i = 0; i < vals.length; i++) {
				if (!fields[i].test(env, vals[i])) return false;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean implies(Env env, Constraint other) {
		if (other instanceof Struct) {
			Constraint[] oFields = ((Struct) other).fields;
			if (oFields.length != fields.length) return false;
			for (int i = 0; i < oFields.length; i++) {
				if (!fields[i].implies(env, oFields[i])) return false;
			}
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T extends PrimitiveConstraint> T extractImplication0(Class<T> type) {
		if (type == EqualityConstraint.class) {
			Val[] vals = new Val[fields.length];
			for (int i = 0; i < fields.length; i++) {
				EqualityConstraint eq = fields[i].extractImplication(EqualityConstraint.class);
				if (eq == null) return null;
				vals[i] = eq.getExpected();
			}
			return (T)new EqualityConstraint(new StructVal(vals));
		}
		return null;
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
	public static final class StructVal implements Val {
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
		
		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			StructVal structVal = (StructVal) o;
			return Objects.deepEquals(vals, structVal.vals);
		}
		
		@Override
		public int hashCode() {
			return Arrays.hashCode(vals);
		}
		
		@Override
		public Type getType() {
			return null;
		}
		
		public static final Type TYPE = new Type(FreeConstraint.INSTANCE);
	}
}
