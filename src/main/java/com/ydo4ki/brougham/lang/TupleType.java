package com.ydo4ki.brougham.lang;

/**
 * @author Sulphuris
 * @since 4/8/2025 10:18 PM
 */
public final class TupleType extends Type {
	private final Type[] types;
	
	public TupleType(Type... types) {
		this.types = types;
	}
	
	public Tuple toTuple() {
		return new Tuple(types);
	}
}
