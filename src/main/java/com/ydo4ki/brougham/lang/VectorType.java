package com.ydo4ki.brougham.lang;

import java.util.Objects;

/**
 * @author Sulphuris
 * @since 4/8/2025 1:50 AM
 */
public final class VectorType extends Type {
	private final int length;
	private final Type elementType;
	
	VectorType(int length, Type elementType) {
		this.length = length;
		this.elementType = elementType;
	}
	
	public Type getElementType() {
		return elementType;
	}
	
	@Override
	public String toString() {
		return "Vector" + length + "$" + elementType;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		VectorType that = (VectorType) o;
		return Objects.equals(elementType, that.elementType);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(elementType);
	}
}
