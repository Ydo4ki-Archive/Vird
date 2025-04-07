package com.ydo4ki.brougham.data;

import java.util.Arrays;
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
		if (len == 0) this.type = new VectorType(SymbolType.instance);
		else {
			Type type = values[0].getType();
			for (int i = 1; i < len; i++) {
				if (!values[i].getType().equals(type))
					throw new IllegalArgumentException("Vector element types mismatch (" + values[i].getType() + " found, " + type + " extepcted)");
			}
			this.type = new VectorType(type);
		}
		this.values = values;
	}
	
	public Val[] getValues() {
		return values;
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "{" + Arrays.stream(values).map(Val::toString).collect(Collectors.joining(" ")) + "}";
	}
}
