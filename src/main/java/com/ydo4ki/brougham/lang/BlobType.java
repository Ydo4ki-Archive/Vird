package com.ydo4ki.brougham.lang;

import java.util.ArrayList;
import java.util.Objects;

public final class BlobType extends Type {
	
	private static final ArrayList<BlobType> types = new ArrayList<>();
	
	public static BlobType of(int length) {
		while (length >= types.size()) {
			types.add(new BlobType(types.size()));
		}
		return types.get(length);
	}
	
	
	private final int length;
	
	private BlobType(int length) {
		this.length = length;
	}
	
	@Override
	public String toString() {
		return "Blob"+length;
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
