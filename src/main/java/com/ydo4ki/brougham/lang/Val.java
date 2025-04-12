package com.ydo4ki.brougham.lang;

import com.ydo4ki.brougham.lang.constraint.EqualityConstraint;

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
