package com.ydo4ki.brougham.lang;

import com.ydo4ki.brougham.Location;
import com.ydo4ki.brougham.lang.constraint.InstanceOfConstraint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @since 4/7/2025 10:33 PM
 * @author Sulphuris
 */
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public final class Symbol implements SyntaxElement {
	public static final TypeRef TYPE = SyntaxElementType.instance.ref(new InstanceOfConstraint(Symbol.class));
	
	private final Location location;
	private final Scope parent;
	private final String value;
	
	@Override
	public TypeRef getType() {
		return TYPE;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
