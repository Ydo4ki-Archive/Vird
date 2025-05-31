package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.base.ExprList;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.constraint.Constraint;
import com.ydo4ki.vird.lang.constraint.EqualityConstraint;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * @since 5/7/2025 8:01 PM
 * @author Sulphuris
 */
@RequiredArgsConstructor
public abstract class ValidatedValCall {
	@Getter
	protected final Constraint constraint;
	
	public final Val invoke() {
		try {
			return Objects.requireNonNull(invoke0());
		} catch (Throwable e) {
			System.err.println("Unexpected error occurred");
			e.printStackTrace(System.err);
			System.exit(2);
			throw e;
		}
	}
	
	protected abstract Val invoke0();
	
	public static ValidatedValCall promiseVal(Val val) {
		Objects.requireNonNull(val);
		return new ValidatedValCall(new EqualityConstraint(val)) {
			@Override
			public Val invoke0() {
				return val;
			}
			
			@Override
			public boolean isPure() {
				return true;
			}
		};
	}
	
	public boolean isPure() {
		return false;
	}
	
	public ValidatedValCall getInvocationConstraint(Scope scope, ExprList f) throws LangValidationException {
		ValidatedValCall cCall = constraint.getInvocationConstraint(scope, f);
		if (cCall.isPure())
			return cCall;
		
		// basically "apply side effects"
		return new ValidatedValCall(cCall.getConstraint()) {
			@Override
			public @NonNull Val invoke0() {
				ValidatedValCall.this.invoke(); // basically "apply side effects"
				return cCall.invoke();
			}
		};
	}
	
	@Override
	public String toString() {
		return this.getClass().getTypeName() + "{" +
				"constraint=" + constraint +
				'}';
	}
}
