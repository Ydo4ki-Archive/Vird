package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.LangException;
import com.ydo4ki.vird.Location;

/**
 * @author Sulphuris
 * @since 4/16/2025 1:45 PM
 */
public class NoImplementationException extends LangException {
	public NoImplementationException(Location location, String message, Throwable cause) {
		super(location, message, cause);
	}
	
	public NoImplementationException(Location location, String message) {
		super(location, message);
	}
}
