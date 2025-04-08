package com.ydo4ki.brougham.data;

import java.util.ArrayList;
import java.util.Objects;

public final class BlobType extends Type {
	
	private static final ArrayList<BlobType> types = new ArrayList<>();
	
	static BlobType of(int depth) {
		while (depth >= types.size()) {
			types.add(new BlobType(types.size()));
		}
		return types.get(depth);
	}
	
	
	private final int length;
	
	BlobType(int length) {
		this.length = length;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		BlobType blobType = (BlobType) o;
		return length == blobType.length;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(length);
	}
}
