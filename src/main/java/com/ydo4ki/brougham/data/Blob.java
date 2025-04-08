package com.ydo4ki.brougham.data;


public final class Blob implements Val {
	private final byte[] data;
	
	public Blob(byte[] data) {
		this.data = data;
	}
	
	@Override
	public Type getType() {
		return BlobType.of(data.length);
	}
}
