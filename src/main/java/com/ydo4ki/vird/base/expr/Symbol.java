package com.ydo4ki.vird.base.expr;

import com.ydo4ki.vird.base.Location;
import com.ydo4ki.vird.base.TypeRef;
import com.ydo4ki.vird.lang.constraint.InstanceOfConstraint;
import lombok.Getter;

import java.util.Objects;

/**
 * @since 4/7/2025 10:33 PM
 * @author Sulphuris
 */
@Getter
public final class Symbol extends Expr {
	public static final TypeRef TYPE = Expr.TYPE.ref(new InstanceOfConstraint(Symbol.class));
	
	private final String value;
	
	public Symbol(Location location, String value) {
		super(location);
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Symbol symbol = (Symbol) o;
		return Objects.equals(value, symbol.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
