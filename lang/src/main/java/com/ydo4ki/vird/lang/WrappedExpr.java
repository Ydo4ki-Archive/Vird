package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.base.*;
import lombok.Getter;

/**
 * @since 4/17/2025 1:32 PM
 * @author Sulphuris
 */
@Getter
public final class WrappedExpr extends Val {
	
	private final Expr expr;
	
	public WrappedExpr(Expr expr) {
		this.expr = expr;
	}
	
	@Override
	public String toString() {
		return "<" + expr + ">";
	}
}
