package com.ydo4ki.brougham.data;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @since 4/7/2025 9:28 PM
 * @author Sulphuris
 */
public final class Tuple extends Type implements Val {
	private Tuple type;
	private final Val[] values;
	
	public Tuple(Val[] values) {
		this.values = values;
	}
	
	@Override
	public Type getType() {
		if (type == null) {
			int len = values.length;
			Type[] types = new Type[len];
			for (int i = 0; i < len; i++) {
				types[i] = values[i].getType();
			}
			this.type = new Tuple(types);
		}
		return type;
	}
	
	@Override
	public boolean isType() {
		for (Val value : values) {
			if (!value.isType()) return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "(" + Arrays.stream(values).map(Val::toString).collect(Collectors.joining(" ")) + ")";
	}
}
