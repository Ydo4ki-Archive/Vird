package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.base.ExprList;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.LangValidationException;
import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.lang.ValidatedValCall;

/**
 * @since 4/18/2025 12:21 AM
 * @author Sulphuris
 */
// todo: extend Val interface
public abstract class Constraint {
	public abstract boolean test(Scope scope, Val value);
	
	// checks if the current constraint implies other (f.e., x > 0 => x >= 0)
	public abstract boolean implies(Scope scope, Constraint other);
	
	@SuppressWarnings("unchecked")
	public final <T extends Constraint> T extractImplication(Class<T> type) {
		if (type.isInstance(this)) return (T)this;
		if (type == FreeConstraint.class) return (T) FreeConstraint.INSTANCE;
		
		T ret = extractImplication0(type);
		if (ret == null && type == InstanceOfConstraint.class)
			return (T) InstanceOfConstraint.of(Val.class);
		return ret;
	}
	
	protected abstract <T extends Constraint> T extractImplication0(Class<T> type);
	
	// by the way
	// if constraint A implies constraint B
	// and constraint B doesn't imply constraint A
	// then constrain B is stricter
	// and if both of them imply each other or none of them imply another one they are equal/uncomparable
	
	/** if this is a function (guaranteed), it returns constraint for result of function based on arguments
	 (or throws LangValidationException if function is not a function or following arguments are inappropriate) */
	public ValidatedValCall getInvocationConstraint(Scope scope, ExprList f) throws LangValidationException {
		throw new LangValidationException(f.getLocation(), "Not callable");
	}
	
	public static boolean areEqual(Scope scope, Constraint a, Constraint b) {
		return a.implies(scope, b) && b.implies(scope, a);
	}
}
