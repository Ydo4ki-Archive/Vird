package com.ydo4ki.vird.lang;


/**
 * Marker exception for runtime (no validation time) operations.<br>
 * Not creatable
 * @since 6/5/2025 9:35 AM
 * @author Sulphuris
 */
public final class RuntimeOperation extends Exception {
	private RuntimeOperation(String message) {
		throw new SecurityException(message);
	}
}
