package com.ydo4ki.vird.base;

import com.ydo4ki.vird.lang.Env;
import com.ydo4ki.vird.lang.LangValidationException;
import com.ydo4ki.vird.lang.Type;
import com.ydo4ki.vird.lang.ValidatedValCall;
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
	
	Type unit_type = new Type(new EqualityConstraint(unit)); // singleton
	
	
	default ValidatedValCall invocation(Env caller, ExprList f) throws LangValidationException {
		throw new LangValidationException(f.getLocation(),
				"Not callable (" + this.getClass().getSimpleName() + " «" + this + "») with " + f.getBracketsType());
	}
	
	Type getType();
}
