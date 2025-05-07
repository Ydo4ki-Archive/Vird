package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.base.Expr;
import com.ydo4ki.vird.base.Location;
import com.ydo4ki.vird.base.Val;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * @author Sulphuris
 * @since 4/9/2025 12:50 AM
 */
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
@Deprecated
public final class Func extends Val {
	private final BiFunction<Scope, Val[], Constraint> tester;
	private final BiFunction<Scope, Val[], Val> transformer;
	
	@Override
	public Constraint invokationConstraint(Location location, Scope caller, Val[] args) throws LangValidationException {
		Constraint c = tester.apply(caller, args);
		if (c == null) throw new LangValidationException(location, "Not applicable");
		return c;
	}
	
	@Override
	public Val invoke(Scope caller, Expr[] args) {
		// lmao
		return Objects.requireNonNull(
				transformer.apply(caller, args),
				"Function just returned null. This is outrageous. It's unfair. How can you be a function, and not return a value?" + Arrays.toString(args)
		);
	}
	
	@Override
	public String toString() {
		return "FUNCTION";
	}
}
