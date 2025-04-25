package com.ydo4ki.vird.lang.layout;

import com.ydo4ki.vird.base.Type;
import com.ydo4ki.vird.base.Val;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * @since 4/25/2025 7:14 AM
 * @author Sulphuris
 */
public final class LayoutedVal implements Val {
	private final LayoutedType type;
	@Getter
	private final byte[] payload;
	
	public LayoutedVal(LayoutedType type, byte[] payload) {
		this.type = type;
		this.payload = payload;
	}
	
	@Override
	public Type getRawType() {
		return type;
	}
	
	public Layout getLayout() {
		return type.getLayout();
	}
	
	@Override
	public String toString() {
		return "LayoutedVal{" +
				"type=" + type +
				", payload=" + Arrays.toString(payload) +
				'}';
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		LayoutedVal that = (LayoutedVal) o;
		return Objects.equals(type, that.type) && Objects.deepEquals(payload, that.payload);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, Arrays.hashCode(payload));
	}
}
