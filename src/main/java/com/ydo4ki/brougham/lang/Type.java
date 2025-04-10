package com.ydo4ki.brougham.lang;


/**
* @since 4/7/2025 9:43 PM
* @author Sulphuris
*/
public abstract class Type implements Val {
	@Override
	public Type getRawType() {
		return MetaType.of(0);
	}
	
	public final TypeRef ref() {
		return new TypeRef(this, false);
	}
	public final TypeRef vararg() {
		return new TypeRef(this, true);
	}
}

