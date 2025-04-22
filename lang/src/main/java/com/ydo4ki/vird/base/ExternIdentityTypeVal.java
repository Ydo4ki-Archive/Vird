package com.ydo4ki.vird.base;

/**
 * @since 4/21/2025 8:06 PM
 * @author Sulphuris
 */
public interface ExternIdentityTypeVal extends Val {
	@Override
	default Type getRawType() {
		return ExternIdentityType.of(this.getClass());
	}
}
