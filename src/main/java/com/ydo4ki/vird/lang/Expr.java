package com.ydo4ki.vird.lang;

/**
 * @author Sulphuris
 * @since 4/11/2025 4:36 PM
 */
public interface Expr extends Val {
	@Override
	default Type getRawType() {
		return Expr.TYPE;
	}
	
	Type TYPE = new Type() {
		@Override
		public String toString() {
			return "SyntaxElement";
		}
	};
}

