package com.ydo4ki.brougham.lang;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @since 4/7/2025 9:28 PM
 * @author Sulphuris
 */
@EqualsAndHashCode
@RequiredArgsConstructor
public final class Tuple implements Val {
	private TupleType type;
	@Getter
	private final Val[] values;
	
	@Override
	public Type getRawType() {
		if (type == null) {
			int len = values.length;
			Type[] types = new Type[len];
			for (int i = 0; i < len; i++) {
				types[i] = values[i].getRawType();
			}
			this.type = new TupleType(types);
		}
		return type;
	}
	
	@Override
	public String toString() {
		return "(" + Arrays.stream(values).map(Val::toString).collect(Collectors.joining(" ")) + ")";
	}
}
