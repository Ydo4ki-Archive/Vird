package com.ydo4ki.brougham.lang;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

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
	
	public String toString() {
		return "(" + Arrays.stream(types).map(Val::toString).collect(Collectors.joining(" ")) + ")";
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		TupleType tupleType = (TupleType) o;
		return Objects.deepEquals(types, tupleType.types);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(types);
	}
}
