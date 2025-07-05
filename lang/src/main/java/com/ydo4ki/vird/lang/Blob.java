package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.lang.constraint.Constraint;
import com.ydo4ki.vird.lang.constraint.FreeConstraint;
import com.ydo4ki.vird.project.Stability;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Stability(Stability.PROB)
@EqualsAndHashCode(callSuper = false)
@Getter
public final class Blob implements Val, Comparable<Blob> {
	private final byte[] data;
	// for optimization purposes due to frequent usage of blob as a number
	private BigInteger bigInteger = null;
	
	public Blob(byte[] data) {
		this.data = data;
	}
	
	public Blob(BigInteger bigInteger) {
		byte[] data = bigInteger.toByteArray();
		while (data.length > 1 && data[0] == 0) {
			int len = data.length;
			byte[] d = new byte[len-1];
			System.arraycopy(data, 1, d, 0, len-1);
			data = d;
		}
		this.data = data;
		this.bigInteger = bigInteger;
	}
	
	public Blob(BigInteger bigInteger, int byteSize) {
		this.data = new byte[byteSize];
		this.bigInteger = bigInteger;
		byte[] data = bigInteger.toByteArray();
		while (data.length > 1 && data[0] == 0) {
			int len = data.length;
			byte[] d = new byte[len-1];
			System.arraycopy(data, 1, d, 0, len-1);
			data = d;
		}
		int bytesLen = Math.min(this.data.length, data.length);
		System.arraycopy(data, 0, this.data, this.data.length - bytesLen, bytesLen);
	}
	
	public BigInteger bigInteger() {
		if (bigInteger == null)
			bigInteger = new BigInteger(data);
		return bigInteger;
	}
	
	@Override
	public String toString() {
		return "0x" + bytesToHex(data);
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
	
	
	public static Blob ofInt(int value) {
		return new Blob(new byte[]{
				(byte) (value >>> 24),
				(byte) (value >>> 16),
				(byte) (value >>> 8),
				(byte) value});
	}
	
	public int toInt() {
		if (data.length == 4)
			return data[0] << 24 | (data[1] & 0xFF) << 16 | (data[2] & 0xFF) << 8 | (data[3] & 0xFF);
		return bigInteger().intValue();
	}
	
	@Override
	public int compareTo(Blob o) {
		return this.bigInteger().compareTo(o.bigInteger());
	}
	
	@Override
	public Type getType() {
		return type(data.length);
	}
	
	private static final Map<Integer, BlobType> types = new HashMap<>();
	
	public static Type type(int size) {
		return types.computeIfAbsent(size, s -> new BlobType(FreeConstraint.INSTANCE, s));
	}
	
	private static final class BlobType extends Type {
		
		private final int size;
		
		public BlobType(Constraint implications, int size) {
			super(implications);
			this.size = size;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			if (!super.equals(o)) return false;
			BlobType blobType = (BlobType) o;
			return size == blobType.size;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(super.hashCode(), size);
		}
	}
}
