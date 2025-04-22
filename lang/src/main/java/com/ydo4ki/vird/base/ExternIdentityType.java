package com.ydo4ki.vird.base;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 4/21/2025 6:06 PM
 * @author Sulphuris
 */
public final class ExternIdentityType extends Type {
	private static final Map<Class<? extends ExternIdentityTypeVal>, ExternIdentityType> types = new HashMap<>();
	
	public static ExternIdentityType of(Class<? extends ExternIdentityTypeVal> cls) {
		return types.computeIfAbsent(cls, ExternIdentityType::new);
	}
	
	private final Class<? extends ExternIdentityTypeVal> cls;
	private ExternIdentityType(Class<? extends ExternIdentityTypeVal> cls) {
		this.cls = cls;
	}
	
	@Override
	public String toString() {
		return cls.getSimpleName();
	}
}
