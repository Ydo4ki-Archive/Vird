package com.ydo4ki.vird.lang.constraint;

import com.ydo4ki.vird.FileInterpreter;
import com.ydo4ki.vird.base.ExprList;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.Env;
import com.ydo4ki.vird.lang.LangValidationException;
import com.ydo4ki.vird.lang.RuntimeOperation;
import com.ydo4ki.vird.lang.ValidatedValCall;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public final class OrConstraint extends AbstractConstraint {
	private final Set<Constraint> constraints;
	
	public static Constraint of(Constraint... constraints) {
		return OrConstraint.of(Arrays.stream(constraints).collect(Collectors.toSet()));
	}
	
	public static Constraint of(Set<Constraint> constraints) {
		if (constraints.size() == 1) return constraints.stream().findAny().orElse(null);
		return new OrConstraint(constraints.stream()
				.flatMap(c -> c instanceof OrConstraint
						? ((OrConstraint) c).constraints.stream()
						: Stream.of(c))
				.collect(Collectors.toSet()));
	}
	
	@Override
	public boolean test(Env env, Val value) {
		return constraints.stream().anyMatch(c -> c.test(env, value));
	}
	
	@Override
	public boolean implies(Env env, Constraint other) {
		return constraints.stream().allMatch(c -> c.implies(env, other));
	}
	
	@Override
	protected <T extends PrimitiveConstraint> T extractImplication0(Class<T> type) {
		return null; // todo
	}
	
	@Override
	public ValidatedValCall getInvocationConstraint(Env env, ExprList f) throws LangValidationException {
		List<Constraint> constraints = new ArrayList<>();
		for (Constraint c : this.constraints) {
			// if even one value is not callable, so the whole thing is. Validation error passed
			ValidatedValCall cCall = c.getInvocationConstraint(env, f);
			
			constraints.add(cCall.getConstraint());
		}
		Constraint c = OrConstraint.of(new HashSet<>(constraints));
		
		// (get-random-echo)
		ValidatedValCall actual = FileInterpreter.evaluateValCall(env, f.get(0));
		if (actual.isPure()) {
			ValidatedValCall result = ValidatedValCall.invokePure(f.getLocation(), actual).invocation(env, f);
			return new ValidatedValCall(c) {
				@Override
				protected Val invoke0() throws RuntimeOperation {
					return result.invoke();
				}
			};
		}
		return new ValidatedValCall(c) {
			@Override
			protected Val invoke0() throws RuntimeOperation {
				try {
						  // (get-random-echo) 				 // ((get-random-echo) "You're lucky!")
					return actual.invoke().invocation(env, f).invoke(); // I really don't like this, but I've got no idea how t avoid double-check here
				} catch (LangValidationException e) {
					System.err.println("Incorrect constraints");
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	@Override
	public String toString() {
		return "Or" + constraints;
	}
}
