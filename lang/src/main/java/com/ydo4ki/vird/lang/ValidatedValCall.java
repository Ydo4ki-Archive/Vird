package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.ast.ExprList;
import com.ydo4ki.vird.ast.Location;
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
	
	public final Val invoke() throws RuntimeOperation {
		try {
			return Objects.requireNonNull(invoke0());
		} catch (Throwable e) {
			System.err.println("Unexpected error occurred");
			e.printStackTrace(System.err);
			System.exit(2);
			throw e;
		}
	}
	
	protected abstract Val invoke0() throws RuntimeOperation;
	
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
	
	public ValidatedValCall getInvocationConstraint(Env env, ExprList f) throws LangValidationException {
		ValidatedValCall cCall = constraint.getInvocationConstraint(env, f);
		if (cCall.isPure())
			return cCall;
		
		// basically "apply side effects"
		return new ValidatedValCall(cCall.getConstraint()) {
			@Override
			public @NonNull Val invoke0() throws RuntimeOperation {
				ValidatedValCall.this.invoke(); // basically "apply side effects" (why did I write it twice xd)
				return cCall.invoke();
			}
		};
	}
	
	public ValidatedValCall getPropertyGetterConstraint(Env env, String property, Location l) throws LangValidationException {
		ValidatedValCall cCall = constraint.getPropertyGetterConstraint(env, property, l);
		if (cCall.isPure())
			return cCall;
		
		// basically "apply side effects"
		return new ValidatedValCall(cCall.getConstraint()) {
			@Override
			public @NonNull Val invoke0() throws RuntimeOperation {
				ValidatedValCall.this.invoke(); // basically "apply side effects" (why did I write it twice xd)
				return cCall.invoke();
			}
		};
	}
	
	// todo: pure calls should be basically calls with EqualityConstraint, so you should extract it instead
	public static Val invokePure(Location location, ValidatedValCall call) throws LangValidationException {
		if (call.isPure()) {
			try {
				return call.invoke();
			} catch (RuntimeOperation e) {
				throw new AssertionError(e);
			}
		} else throw new LangValidationException(location, "Attempt to call non-pure function during validation stage: " + call);
	}
	
	@Override
	public String toString() {
		return this.getClass().getTypeName() + "{" + "constraint=" + constraint + '}';
	}
}
