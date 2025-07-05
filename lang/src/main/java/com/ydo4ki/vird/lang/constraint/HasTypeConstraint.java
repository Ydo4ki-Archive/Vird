package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.lang.Env;
import com.ydo4ki.vird.lang.Val;
import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.lang.Type;
import lombok.RequiredArgsConstructor;

/**
 * @since 6/5/2025 10:19 PM
 */
@RequiredArgsConstructor(staticName = "of")
public final class HasTypeConstraint extends PrimitiveConstraint {
	private final Type type;
	
	@Override
	public boolean test(Env env, Val value) {
		return false; // value.getTypeMark() == type;
	}
	
	@Override
	public boolean implies(Env env, Constraint other) {
		return type.getImplications().implies(env, other);
	}
	
	@Override
	protected <T extends PrimitiveConstraint> T extractImplication0(Class<T> type) {
		return this.type.getImplications().extractImplication(type);
	}
}
