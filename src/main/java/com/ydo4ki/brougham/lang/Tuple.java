package com.ydo4ki.brougham.lang;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @since 4/7/2025 9:28 PM
 * @author Sulphuris
 */
public final class Tuple extends Type implements ReifiedVal {
	private Tuple type;
	private final Val[] values;
	
	public Tuple(Val[] values) {
		this.values = values;
	}
	
	public Val[] getValues() {
		return values;
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
		return "(" + Arrays.stream(values).map(Val::toString).collect(Collectors.joining(", ")) + ")";
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Tuple tuple = (Tuple) o;
		return Objects.deepEquals(values, tuple.values);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, Arrays.hashCode(values));
	}
	
	@Override
	public int size() {
		int v = 0;
		for (Val value : values) {
			if (!(value instanceof ReifiedVal)) return -1;
			int size = ((ReifiedVal)value).size();
			if (size < 0) return -1;
			v += size;
		}
		return v;
	}
}
