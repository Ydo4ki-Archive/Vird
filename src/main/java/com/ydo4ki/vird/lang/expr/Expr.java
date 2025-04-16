package com.ydo4ki.vird.lang.expr;

import com.ydo4ki.vird.Location;
import com.ydo4ki.vird.lang.Type;
import com.ydo4ki.vird.lang.Val;

/**
 * @author Sulphuris
 * @since 4/11/2025 4:36 PM
 */
public interface Expr extends Val {
	
	Location getLocation();
	
	@Override
	default Type getRawType() {
		return Expr.TYPE;
	}
	
	Type TYPE = new Type() {
		@Override
		public String toString() {
			return "Expr";
		}
	};
}

