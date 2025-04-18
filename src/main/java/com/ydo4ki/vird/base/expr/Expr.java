package com.ydo4ki.vird.base.expr;

import com.ydo4ki.vird.base.Location;
import com.ydo4ki.vird.base.Type;
import com.ydo4ki.vird.base.Val;

/**
 * @author Sulphuris
 * @since 4/11/2025 4:36 PM
 */
public abstract class Expr implements Val {
	
	private final Location location;
	
	// sealed
	Expr(Location location) {
		this.location = location;
	}
	
	public final Location getLocation() {
		return location;
	}
	
	@Override
	public Type getRawType() {
		return Expr.TYPE;
	}
	
	public static final Type TYPE = new Type() {
		@Override
		public String toString() {
			return "Expr";
		}
	};
}

