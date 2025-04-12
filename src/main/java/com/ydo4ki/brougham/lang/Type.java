package com.ydo4ki.brougham.lang;

import com.ydo4ki.brougham.lang.constraint.Constraint;

/**
* @since 4/7/2025 9:43 PM
* @author Sulphuris
*/
public abstract class Type extends FunctionSetImpl {
	@Override
	public Type getRawType() {
		return MetaType.of(0);
	}
	
	public final TypeRef ref() {
		return new TypeRef(this, false);
	}
	public final TypeRef ref(Constraint constraints) {
		return new TypeRef(this, false, constraints);
	}
	public final TypeRef vararg() {
		return new TypeRef(this, true);
	}
}

