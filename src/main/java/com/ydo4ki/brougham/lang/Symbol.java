package com.ydo4ki.brougham.lang;

import com.ydo4ki.brougham.Location;
import com.ydo4ki.brougham.Source;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * @since 4/7/2025 10:33 PM
 * @author Sulphuris
 */
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public final class Symbol implements Val {
	private final Location location;
	private final Scope parent;
	private final String value;
	
	@Override
	public Type getRawType() {
		return SymbolType.instance;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
