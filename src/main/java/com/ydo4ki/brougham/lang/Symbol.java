package com.ydo4ki.brougham.lang;

import com.ydo4ki.brougham.Location;
import com.ydo4ki.brougham.lang.constraint.InstanceOfConstraint;
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
public final class Symbol implements SyntaxElement {
	public static final TypeRef TYPE = SyntaxElementType.instance.ref(new InstanceOfConstraint(Symbol.class));
	
	private final Location location;
	private final Scope parent;
	private final String value;
	
	@Override
	public String toString() {
		return value;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Symbol symbol = (Symbol) o;
		return Objects.equals(location, symbol.location) && Objects.equals(value, symbol.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(location, value);
	}
}
