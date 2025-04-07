package com.ydo4ki.brougham.data;

import java.util.Objects;

final class MetaType extends Type {
	private final int depth;
	
	MetaType(int depth) {
		this.depth = depth;
	}
	
	@Override
	public Type getType() {
		return new MetaType(depth+1);
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
