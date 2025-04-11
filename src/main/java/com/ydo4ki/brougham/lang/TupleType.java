package com.ydo4ki.brougham.lang;

import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Sulphuris
 * @since 4/8/2025 10:18 PM
 */
@EqualsAndHashCode(callSuper = false)
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
}
