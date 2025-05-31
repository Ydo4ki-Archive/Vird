package com.ydo4ki.vird.base;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * @since 4/6/2025 8:43 PM
 * @author Sulphuris
 */
@RequiredArgsConstructor
public final class BracketsType {
	
	public final char open;
	public final char close;
	
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		BracketsType that = (BracketsType) o;
		return open == that.open && close == that.close;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(open, close);
	}
}
