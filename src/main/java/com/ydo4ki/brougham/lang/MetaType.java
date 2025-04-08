package com.ydo4ki.brougham.lang;

import java.util.ArrayList;
import java.util.Objects;

final class MetaType extends Type {
	private static final ArrayList<MetaType> types = new ArrayList<>();
	
	static MetaType of(int depth) {
		while (depth >= types.size()) {
			types.add(new MetaType(types.size()));
		}
		return types.get(depth);
	}
	
	
	
	private final int depth;
	
	private MetaType(int depth) {
		this.depth = depth;
	}
	
	@Override
	public Type getType() {
		return MetaType.of(depth + 1);
	}
	
	@Override
	public String toString() {
		if (depth == 0) return "Type";
		return "Type" + depth;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		MetaType metaType = (MetaType) o;
		return depth == metaType.depth;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(depth);
	}
}
