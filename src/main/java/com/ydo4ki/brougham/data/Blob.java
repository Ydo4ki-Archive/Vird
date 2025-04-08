package com.ydo4ki.brougham.data;


import java.util.Arrays;
import java.util.Objects;

public final class Blob implements Val {
	private final byte[] data;
	
	public Blob(byte[] data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "B" + Arrays.toString(data);
	}
	
	@Override
	public Type getType() {
		return BlobType.of(data.length);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Blob blob = (Blob) o;
		return Objects.deepEquals(data, blob.data);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}
}
