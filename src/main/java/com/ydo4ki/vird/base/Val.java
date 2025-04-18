package com.ydo4ki.vird.base;

import com.ydo4ki.vird.lang.constraint.EqualityConstraint;

/**
 * @since 4/7/2025 9:43 PM
 * @author Sulphuris
 */
public interface Val {
	Type getRawType();
	
	default TypeRef getType() {
		return getRawType().ref(new EqualityConstraint(this));
	}
}
