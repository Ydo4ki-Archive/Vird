package com.ydo4ki.vird.ast;

import java.util.Collection;

/**
 * @author Sulphuris
 * @since 4/11/2025 4:36 PM
 */
public abstract class Expr {
	
	private final Location location;
	
	// sealed
	Expr(Location location) {
		this.location = location;
	}
	
	public final Location getLocation() {
		return location;
	}
	
	
	public abstract Collection<? extends Expr> split(String... separateLines);
}

