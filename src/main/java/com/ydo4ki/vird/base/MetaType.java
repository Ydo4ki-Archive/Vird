package com.ydo4ki.vird.base;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public final class MetaType extends Type {
	private static final ArrayList<MetaType> types = new ArrayList<>();
	
	public static MetaType of(int depth) {
		while (depth >= types.size()) {
			types.add(new MetaType(types.size()));
		}
		return types.get(depth);
	}
	
	
	private final int depth;
	
	@Override
	public Type getRawType() {
		return MetaType.of(depth + 1);
	}
	
	@Override
	public String toString() {
		if (depth == 0) return "Type";
		return "Type" + depth;
	}
}
