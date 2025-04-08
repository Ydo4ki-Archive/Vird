package com.ydo4ki.brougham.lang;


import java.util.Arrays;
import java.util.Objects;

public final class Blob implements Val {
	private final byte[] data;
	
	public Blob(byte[] data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "b" + bytesToHex(data);
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
	
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	private static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}
}
