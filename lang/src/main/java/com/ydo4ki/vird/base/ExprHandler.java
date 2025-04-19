package com.ydo4ki.vird.base;

import java.util.function.Function;

/**
 * @since 4/18/2025 12:01 AM
 * @author Sulphuris
 */
@FunctionalInterface
public interface ExprHandler extends Function<Expr, Val> {
	@Override
	Val apply(Expr expr);
}
