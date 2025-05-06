package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.base.ExprList;
import com.ydo4ki.vird.base.LangException;
import com.ydo4ki.vird.base.Val;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * @author Sulphuris
 * @since 4/9/2025 12:50 AM
 */
@EqualsAndHashCode
@RequiredArgsConstructor
public final class Func extends Val {
	private final BiPredicate<Scope, Val[]> tester;
	private final BiFunction<Scope, Val[], Val> transformer;
	
	public boolean isApplicable(Scope caller, Val[] args) {
		return tester.test(caller, args);
	}
	
	public void validate(ExprList f, Scope caller, Val[] args) throws LangException {
		if (!isApplicable(caller, args)) throw new LangException(f.getLocation(), "validation error");
	}
	
	public Val invoke(Scope caller, Val[] args) {
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
