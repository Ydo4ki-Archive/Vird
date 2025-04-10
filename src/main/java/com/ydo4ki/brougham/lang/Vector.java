package com.ydo4ki.brougham.lang;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Sulphuris
 * @since 4/7/2025 11:31 PM
 */
public final class Vector implements Val {
	private final VectorType type;
	private final Val[] values;
	
	public Vector(Val[] values) {
		int len = values.length;
		
		Type type = len == 0 ? SymbolType.instance : values[0].getRawType();
		for (int i = 1; i < len; ++i) {
			if (!values[i].getRawType().equals(type))
				throw new IllegalArgumentException("Vector element type mismatch (" + values[i].getRawType() + " found, " + type + " expected)");
		}
		this.type = new VectorType(len, type);
		this.values = values;
	}
	
	public Val[] getValues() {
		return values;
	}
	
	@Override
	public Type getRawType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "{" + Arrays.stream(values).map(Val::toString).collect(Collectors.joining(", ")) + "}";
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Vector vector = (Vector) o;
		return Objects.equals(type, vector.type) && Objects.deepEquals(values, vector.values);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, Arrays.hashCode(values));
	}
}
