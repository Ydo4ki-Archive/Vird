package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.ast.ExprList;
import com.ydo4ki.vird.ast.Location;
import com.ydo4ki.vird.lang.constraint.EqualityConstraint;

/**
 * @since 6/5/2025 7:50 PM
 */
public interface Val {
	Val unit = new Val() {
		@Override
		public Type getType() {
			return unit_type;
		}
		
		@Override
		public String toString() {
			return "UNIT";
		}
	};
	
	Type unit_type = new IdentityType(new EqualityConstraint(unit)); // singleton
	
	
	default ValidatedValCall invocation(Env env, ExprList f) throws LangValidationException {
		throw new LangValidationException(f.getLocation(),
				"Not callable (" + this.getClass().getSimpleName() + " «" + this + "») with " + f.getBracketsType());
	}
	
	default ValidatedValCall propertyGetter(Env env, String property, Location l) throws LangValidationException {
		throw new LangValidationException(l,
				"Not such property: " + property + " (" + this.getClass().getSimpleName() + " «" + this + "»)");
	}
	
	Type getType();
}
