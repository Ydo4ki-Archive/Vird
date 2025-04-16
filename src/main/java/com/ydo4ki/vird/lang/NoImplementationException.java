package com.ydo4ki.vird.lang;

/**
 * @author Sulphuris
 * @since 4/16/2025 1:45 PM
 */
public class NoImplementationException extends Exception {
	public NoImplementationException() {
	}
	
	public NoImplementationException(Object message) {
		super(String.valueOf(message));
	}
}
