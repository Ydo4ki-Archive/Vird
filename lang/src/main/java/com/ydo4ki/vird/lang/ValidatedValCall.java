package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.constraint.Constraint;
import com.ydo4ki.vird.lang.constraint.EqualityConstraint;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @since 5/7/2025 8:01 PM
 * @author Sulphuris
 */

@RequiredArgsConstructor
public abstract class ValidatedValCall {
	@Getter
	protected final Constraint constraint;
	
	public abstract Val invoke();
	
	public static ValidatedValCall promiseVal(Val val) {
		return new ValidatedValCall(new EqualityConstraint(val)) {
			@Override
			public Val invoke() {
				return val;
			}
		};
	}
}
