package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.base.Val;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

@EqualsAndHashCode(callSuper = false)
@Getter
public final class Blob extends Val {
	private final byte[] data;
	// for optimization purposes due to frequent usage of blob as a number
	private BigInteger bigInteger = null;
	
	public Blob(byte[] data) {
		this.data = data;
	}
	
	public Blob(BigInteger bigInteger) {
		this.data = bigInteger.toByteArray();
		this.bigInteger = bigInteger;
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
		return data[0] << 24 | (data[1] & 0xFF) << 16 | (data[2] & 0xFF) << 8 | (data[3] & 0xFF);
	}
}
