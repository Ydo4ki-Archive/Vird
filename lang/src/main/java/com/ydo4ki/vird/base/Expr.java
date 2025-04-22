package com.ydo4ki.vird.base;

/**
 * @author Sulphuris
 * @since 4/11/2025 4:36 PM
 */
public abstract class Expr implements ExternIdentityTypeVal {
	
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
		return Expr.TYPE_RAW;
	}
	
	public static final Type TYPE_RAW = ExternIdentityType.of(Expr.class);
}

