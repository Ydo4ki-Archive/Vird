package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.ast.ExprList;
import com.ydo4ki.vird.lang.*;

/**
 * @since 4/18/2025 12:21 AM
 * @author Sulphuris
 */
// todo: extend Val interface
public abstract class AbstractConstraint implements Constraint {
	public abstract boolean test(Env env, Val value);
	
	// checks if the current constraint implies other (f.e., x > 0 => x >= 0)
	public abstract boolean implies(Env env, Constraint other);
	
	@SuppressWarnings("unchecked")
	public final <T extends PrimitiveConstraint> T extractImplication(Class<T> type) {
		if (type.isInstance(this)) return (T)this;
		if (type == FreeConstraint.class) return (T) FreeConstraint.INSTANCE;
		
		T ret = extractImplication0(type);
		if (ret == null && type == InstanceOfConstraint.class)
			return (T) InstanceOfConstraint.of(Val.class);
		return ret;
	}
	
	protected abstract <T extends PrimitiveConstraint> T extractImplication0(Class<T> type);
	
	// by the way
	// if constraint A implies constraint B
	// and constraint B doesn't imply constraint A
	// then constrain B is stricter
	// and if both of them imply each other or none of them imply another one they are equal/uncomparable
	
	/** if this is a function (guaranteed), it returns constraint for result of function based on arguments
	 (or throws LangValidationException if function is not a function or following arguments are inappropriate) */
	public ValidatedValCall getInvocationConstraint(Env env, ExprList f) throws LangValidationException {
		throw new LangValidationException(f.getLocation(), "Not callable");
	}
	
	public static boolean areEqual(Env env, Constraint a, Constraint b) {
		return a.implies(env, b) && b.implies(env, a);
	}
}
