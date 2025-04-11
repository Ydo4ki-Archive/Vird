package com.ydo4ki.brougham.lang;

public final class TypeRefType extends Type {
	public static final TypeRefType instance = new TypeRefType();
	
	private TypeRefType() {}
	
	@Override
	public String toString() {
		return "TypeRef";
	}
}
