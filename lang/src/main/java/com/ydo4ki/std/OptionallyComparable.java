package com.ydo4ki.std;

import java.util.OptionalInt;

/**
 * @since 4/23/2025 11:09 AM
 * @author Sulphuris
 */
public interface OptionallyComparable<T> {
	public OptionalInt compareTo(T o);
	
	default boolean isComparableWith(T o) {
		return compareTo(o).isPresent();
	}
}
