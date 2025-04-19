package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.base.Type;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.base.Expr;
import lombok.Getter;

/**
 * @since 4/17/2025 1:32 PM
 * @author Sulphuris
 */
@Getter
public final class WrappedExpr implements Val {
	
	private final Expr expr;
	
	public WrappedExpr(Expr expr) {
		this.expr = expr;
	}
	
	@Override
	public String toString() {
		return "<" + expr + ">";
	}
	
	public static final Type TYPE = new Type() {
		@Override
		public String toString() {
			return "WrappedExpr";
		}
	};
	
	@Override
	public Type getRawType() {
		return TYPE;
	}
}
